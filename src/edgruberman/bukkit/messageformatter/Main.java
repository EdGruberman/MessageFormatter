package edgruberman.bukkit.messageformatter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;
import edgruberman.bukkit.messagemanager.channels.ServerChannel;

public final class Main extends org.bukkit.plugin.java.JavaPlugin {
    
    private static ConfigurationFile configurationFile;
    private static MessageManager messageManager;
    
    public void onLoad() {
        Main.configurationFile = new ConfigurationFile(this);
        Main.getConfigurationFile().load();
        
        Main.messageManager = new MessageManager(this);
        Main.getMessageManager().log("Version " + this.getDescription().getVersion());
    }
    
    public void onEnable() {
        new PlayerListener(this);
        new CommandManager(this);
        Main.messageManager.log("Plugin Enabled");
    }
    
    public void onDisable() {
        Main.messageManager.log("Plugin Disabled");
    }
    
    static ConfigurationFile getConfigurationFile() {
        return Main.configurationFile;
    }
    
    static MessageManager getMessageManager() {
        return Main.messageManager;
    }
    
    String formatChat(Player player, String message) {
        return this.formatChat(player, message, null);
    }
    
    String formatChat(Player player, String message, ChatColor color) {
        String type;
        String name = null;
        if (player != null) {
            type = "player";
            name = player.getDisplayName();
        } else {
            type = "server";
        }
        
        if (color != null)
            message = color + message;
        
        return Main.getMessageManager().format(
                ServerChannel.getInstance(this.getServer())
                , this.getMessageLevel("broadcast." + type + ".chat")
                , String.format(this.getMessageFormat("broadcast." + type + ".chat"), message, name)
        );
    }
    
    void broadcastSay(CommandSender sender, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.getMessageManager().broadcast(
                String.format(this.getMessageFormat("broadcast." + type + ".say"), message, name)
                , this.getMessageLevel("broadcast." + type + ".say")
        );
    }
    
    void broadcastMe(CommandSender sender, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.getMessageManager().broadcast(
                String.format(this.getMessageFormat("broadcast." + type + ".me"), message, name)
                , this.getMessageLevel("broadcast." + type + ".me")
        );
    }
    
    void sendTell(CommandSender sender, Player target, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.getMessageManager().send(
                target
                , String.format(this.getMessageFormat("send." + type + ".tell"), message, name)
                , this.getMessageLevel("send." + type + ".tell")
        );
    }
    
    MessageLevel getMessageLevel(String path) {
        return MessageLevel.parse(this.getConfiguration().getString(path + ".level"));
    }
    
    String getMessageFormat(String path) {
        return this.getConfiguration().getString(path + ".format");
    }
}