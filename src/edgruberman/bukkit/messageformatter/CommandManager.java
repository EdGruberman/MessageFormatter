package edgruberman.bukkit.messageformatter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

final class CommandManager implements CommandExecutor {

    CommandManager (JavaPlugin plugin) {
        plugin.getCommand("say").setExecutor(this);
        plugin.getCommand("me").setExecutor(this);
        plugin.getCommand("tell").setExecutor(this);
        plugin.getCommand("broadcast").setExecutor(this);
        plugin.getCommand("send").setExecutor(this);
        plugin.getCommand("messageformatter").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Main.messageManager.log(
                ((sender instanceof Player) ? ((Player) sender).getName() : "CONSOLE")
                    + " issued command: " + label + " " + CommandManager.join(split)
                , MessageLevel.FINE
        );
        
        if (label.toLowerCase().equals("say"))
            return CommandManager.executeSay(sender, CommandManager.join(split));
        
        else if (label.toLowerCase().equals("me"))
            return CommandManager.executeMe(sender, CommandManager.join(split));
        
        else if (label.toLowerCase().equals("tell"))
            return CommandManager.executeTell(sender, CommandManager.join(split));
        
        else if (label.toLowerCase().equals("broadcast"))
            return CommandManager.executeBroadcast(sender, CommandManager.join(split));
        
        else if (label.toLowerCase().equals("send"))
            return CommandManager.executeSend(sender, CommandManager.join(split));
        
        else if (label.toLowerCase().equals("messageformatter"))
            return CommandManager.executeMessageFormatter(sender, CommandManager.join(split));
        
        return false;
    }
    
    private static boolean executeMessageFormatter(final CommandSender sender, final String parameters) {
        if (!sender.isOp()) return true;
        
        if (parameters.length() == 0) {
            Main.messageManager.respond(sender, "Syntax Error: /messageformatter reload", MessageLevel.SEVERE);
            return true;
        }
        
        Main.configurationFile.load();
        Main.messageManager.respond(sender, "MessageFormatter configuration reloaded.", MessageLevel.CONFIG);
        
        return true;
    }
    
    private static boolean executeSay(final CommandSender sender, final String message) {
        if (message.length() == 0) {
            Main.messageManager.respond(sender, "Syntax Error: /say <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        Main.say(sender, message.trim());
        
        return true;
    }
    
    private static boolean executeMe(final CommandSender sender, final String message) {
        if (message.length() == 0) {
            Main.messageManager.respond(sender, "Syntax Error: /me <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        Main.me(sender, message.trim());
        
        return true;
    }
    
    private static boolean executeTell(final CommandSender sender, final String text) {
        String[] split = text.split(" ", 2);
        if (split.length < 2) {
            Main.messageManager.respond(sender, "Syntax Error: /tell <Player> <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        String recipient = split[0];
        Player target = Bukkit.getServer().getPlayer(recipient);
        if (target == null) {
            Main.messageManager.respond(sender, "There's no player by the name of \"" + recipient + "\" online.", MessageLevel.SEVERE);
            return true;
        }
           
        Main.tell(sender, target, split[1].trim());
        
        return true;
    }
    
    private static boolean executeBroadcast(final CommandSender sender, final String text) {
        if (!sender.isOp()) return true;
        
        if (text.length() == 0) {
            Main.messageManager.respond(sender, "Syntax Error: /broadcast [(+|-)timestamp ][<Level> ]<Message>", MessageLevel.SEVERE);
            return true;
        }
        
        String message = text;
        
        boolean isTimestamped = true;
        if (message.matches("^[+\\-]timestamp .+")) {
            String[] split = message.split(" ", 2);
            isTimestamped = !split[0].equals("-timestamp");
            message = split[1];
        }
        
        MessageLevel level = null;
        if (message.matches("^%[^%]+% .+")) {
            String[] split = message.split("%", 3);
            level = MessageLevel.parse(split[1]);
            message = split[2];
        }
        
        Main.messageManager.broadcast(message.trim(), level, isTimestamped);
        
        return true;
    }
    
    private static boolean executeSend(final CommandSender sender, final String text) {
        if (!sender.isOp()) return true;
        
        String[] split = text.split(" ", 2);
        if (split.length < 2) {
            Main.messageManager.respond(sender, "Syntax Error: /send <Player> [(+|-)timestamp ][<Level> ]<Message>", MessageLevel.SEVERE);
            return true;
        }
        
        String recipient = split[0];
        Player target = Bukkit.getServer().getPlayer(recipient);
        if (target == null) {
            Main.messageManager.respond(sender, "There's no player by the name of \"" + recipient + "\" online.", MessageLevel.SEVERE);
            return true;
        }
        
        String message = split[1];
        String[] splitMessage;
        
        boolean isTimestamped = true;
        if (message.matches("^[+\\-]timestamp .+")) {
            splitMessage = message.split(" ", 2);
            isTimestamped = !splitMessage[0].equals("-timestamp");
            message = splitMessage[1];
        }
        
        MessageLevel level = null;
        if (message.matches("^%[^%]+% .+")) {
            splitMessage = message.split("%", 3);
            level = MessageLevel.parse(splitMessage[1]);
            message = splitMessage[2];
        }
        
        Main.messageManager.send(target, message.trim(), level, isTimestamped);
        
        return true;
    }
    
    private static String join(final String[] s) {
        return CommandManager.join(Arrays.asList(s), " ");
    }
    
    private static String join(final List<String> list, final String delim) {
        if (list == null || list.isEmpty()) return "";
     
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());
        
        return sb.toString();
    }
}