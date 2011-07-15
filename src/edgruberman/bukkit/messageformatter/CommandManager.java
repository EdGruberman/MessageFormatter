package edgruberman.bukkit.messageformatter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;

public class CommandManager implements CommandExecutor {
    private Main plugin;

    protected CommandManager (Main plugin) {
        this.plugin = plugin;
        
        this.plugin.getCommand("say").setExecutor(this);
        this.plugin.getCommand("me").setExecutor(this);
        this.plugin.getCommand("tell").setExecutor(this);
        this.plugin.getCommand("broadcast").setExecutor(this);
        this.plugin.getCommand("send").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Main.getMessageManager().log(
                ((sender instanceof Player) ? ((Player) sender).getName() : "CONSOLE")
                    + " issued command: " + label + " " + this.join(split)
                , MessageLevel.FINE
        );
        
        if (label.toLowerCase().equals("say"))
            return this.executeSay(sender, this.join(split));
        
        else if (label.toLowerCase().equals("me"))
            return this.executeMe(sender, this.join(split));
        
        else if (label.toLowerCase().equals("tell"))
            return this.executeTell(sender, this.join(split));
        
        else if (label.toLowerCase().equals("broadcast"))
            return this.executeBroadcast(sender, this.join(split));
        
        else if (label.toLowerCase().equals("send"))
            return this.executeSend(sender, this.join(split));
        
        return false;
    }
    
    private boolean executeSay(CommandSender sender, String message) {
        if (message.length() == 0) {
            Main.getMessageManager().respond(sender, "Syntax Error: /say <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        this.plugin.broadcastSay(sender, message.trim());
        
        return true;
    }
    
    private boolean executeMe(CommandSender sender, String message) {
        if (message.length() == 0) {
            Main.getMessageManager().respond(sender, "Syntax Error: /me <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        this.plugin.broadcastMe(sender, message.trim());
        
        return true;
    }
    
    private boolean executeTell(CommandSender sender, String text) {
        String[] split = text.split(" ", 2);
        if (split.length < 2) {
            Main.getMessageManager().respond(sender, "Syntax Error: /tell <Player> <Message>", MessageLevel.SEVERE);
            return true;
        }
        
        String recipient = split[0];
        Player target = this.plugin.getServer().getPlayer(recipient);
        if (target == null) {
            Main.getMessageManager().respond(sender, "There's no player by the name of \"" + recipient + "\" online.", MessageLevel.SEVERE);
            return true;
        }
           
        this.plugin.sendTell(sender, target, split[1].trim());
        
        return true;
    }
    
    private boolean executeBroadcast(CommandSender sender, String text) {
        if (!sender.isOp()) return false;
        
        if (text.length() == 0) {
            Main.getMessageManager().respond(sender, "Syntax Error: /broadcast [(+|-)timestamp ][<Level> ]<Message>", MessageLevel.SEVERE);
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
        
        Main.getMessageManager().broadcast(message.trim(), level, isTimestamped);
        
        return true;
    }
    
    private boolean executeSend(CommandSender sender, String text) {
        if (!sender.isOp()) return false;
        
        String[] split = text.split(" ", 2);
        if (split.length < 2) {
            Main.getMessageManager().respond(sender, "Syntax Error: /send <Player> [(+|-)timestamp ][<Level> ]<Message>", MessageLevel.SEVERE);
            return true;
        }
        
        String recipient = split[0];
        Player target = this.plugin.getServer().getPlayer(recipient);
        if (target == null) {
            Main.getMessageManager().respond(sender, "There's no player by the name of \"" + recipient + "\" online.", MessageLevel.SEVERE);
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
        
        Main.getMessageManager().send(target, message.trim(), level, isTimestamped);
        
        return true;
    }
    
    private String join(String[] s) {
        return this.join(Arrays.asList(s), " ");
    }
    
    private String join(List<String> list, String delim) {
        if (list == null || list.isEmpty()) return "";
     
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());
        
        return sb.toString();
    }
}