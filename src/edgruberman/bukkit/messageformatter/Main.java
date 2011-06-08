package edgruberman.bukkit.messageformatter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public class Main extends org.bukkit.plugin.java.JavaPlugin {
    
    protected static MessageManager messageManager = null;
    
    public void onLoad() {
        Configuration.load(this);
    }
    
    public void onEnable() {
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());

        new CommandManager(this);
        
        this.registerEvents();
        
        Main.messageManager.log("Plugin Enabled");
    }
    
    public void onDisable() {
        Main.messageManager.log("Plugin Disabled");
        Main.messageManager = null;
    }
    
    private void registerEvents() {
        org.bukkit.plugin.PluginManager pluginManager = this.getServer().getPluginManager();
        PlayerListener playerListener = new PlayerListener(this);
        
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, this.getEventPriority("broadcast.player.join"), this);
        pluginManager.registerEvent(Event.Type.PLAYER_CHAT, playerListener, this.getEventPriority("broadcast.player.chat"), this);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, this.getEventPriority("broadcast.player.quit"), this);
        pluginManager.registerEvent(Event.Type.PLAYER_KICK, playerListener, this.getEventPriority("broadcast.player.kick"), this);
        pluginManager.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, this.getEventPriority("playerLogin"), this);
    }
    
    private Event.Priority getEventPriority(String path) {
        return Main.parseEventPriority(this.getConfiguration().getString(path + ".priority"));
    }
    
    private static Event.Priority parseEventPriority(String name) {
            if (name.toUpperCase().equals("LOWEST"))  return Event.Priority.Lowest;
       else if (name.toUpperCase().equals("LOW"))     return Event.Priority.Low;
       else if (name.toUpperCase().equals("NORMAL"))  return Event.Priority.Normal;
       else if (name.toUpperCase().equals("HIGH"))    return Event.Priority.High;
       else if (name.toUpperCase().equals("HIGHEST")) return Event.Priority.Highest;
       else if (name.toUpperCase().equals("MONITOR")) return Event.Priority.Monitor;
       
       return null;
    }
    
    protected String formatChat(Player player, String message) {
        return this.formatChat(player, message, null);
    }
    
    protected String formatChat(Player player, String message, ChatColor color) {
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
        
        return Main.messageManager.formatBroadcast(this.getMessageLevel("broadcast." + type + ".chat")
                , String.format(this.getMessageFormat("broadcast." + type + ".chat"), message, name)
        );
    }
    
    protected void broadcastSay(CommandSender sender, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.messageManager.broadcast(
                this.getMessageLevel("broadcast." + type + ".say")
                , String.format(this.getMessageFormat("broadcast." + type + ".say"), message, name)
        );
    }
    
    protected void broadcastMe(CommandSender sender, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.messageManager.broadcast(
                this.getMessageLevel("broadcast." + type + ".me")
                , String.format(this.getMessageFormat("broadcast." + type + ".me"), message, name)
        );
    }
    
    protected void sendTell(CommandSender sender, Player target, String message) {
        String type;
        String name = null;
        if (sender instanceof Player) {
            type = "player";
            name = ((Player) sender).getDisplayName();
        } else {
            type = "server";
        }
        
        Main.messageManager.send(
                target
                , this.getMessageLevel("send." + type + ".tell")
                , String.format(this.getMessageFormat("send." + type + ".tell"), message, name)
        );
    }
    
    protected MessageLevel getMessageLevel(String path) {
        return MessageLevel.parse(this.getConfiguration().getString(path + ".level"));
    }
    
    protected String getMessageFormat(String path) {
        return this.getConfiguration().getString(path + ".format");
    }
}