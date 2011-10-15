package edgruberman.bukkit.messageformatter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public final class Main extends JavaPlugin {
    
    final static String eventPrefix = "MessageFormatter"; 
    
    static ConfigurationFile configurationFile;
    static MessageManager messageManager;
    
    public void onLoad() {
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());

        Main.configurationFile = new ConfigurationFile(this);
    }
    
    public void onEnable() {
        new PlayerListener(this);
        new CommandManager(this);
        Main.messageManager.log("Plugin Enabled");
    }
    
    public void onDisable() {
        Main.messageManager.log("Plugin Disabled");
    }
    
    static void say(final CommandSender sender, final String message) {
        String name = sender.getName();
        if (sender instanceof Player) name = ((Player) sender).getDisplayName();
        
        Main.messageManager.broadcast(
                String.format(Main.getMessageFormat("say"), message, name)
                , Main.getMessageLevel("say")
        );
    }
    
    static void me(final CommandSender sender, final String message) {
        String name = sender.getName();
        if (sender instanceof Player) name = ((Player) sender).getDisplayName();
        
        Main.messageManager.broadcast(
                String.format(Main.getMessageFormat("me"), message, name)
                , Main.getMessageLevel("me")
        );
    }
    
    static void tell(final CommandSender sender, final Player target, final String message) {
        String name = sender.getName();
        if (sender instanceof Player) name = ((Player) sender).getDisplayName();
        
        Main.messageManager.send(
                target
                , String.format(Main.getMessageFormat("tell"), message, name)
                , Main.getMessageLevel("tell")
        );
    }
    
    static MessageLevel getMessageLevel(final String path) {
        return MessageLevel.parse(Main.configurationFile.getConfiguration().getString(path + ".level"));
    }
    
    static String getMessageFormat(final String path) {
        return Main.configurationFile.getConfiguration().getString(path + ".format");
    }
    
    static Event.Priority getEventPriority(final String path) {
        return Event.Priority.valueOf(Main.configurationFile.getConfiguration().getString(path + ".priority"));
    }
}