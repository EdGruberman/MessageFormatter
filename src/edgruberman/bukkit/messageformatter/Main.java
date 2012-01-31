package edgruberman.bukkit.messageformatter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.commands.Broadcast;
import edgruberman.bukkit.messageformatter.commands.Me;
import edgruberman.bukkit.messageformatter.commands.Reply;
import edgruberman.bukkit.messageformatter.commands.Say;
import edgruberman.bukkit.messageformatter.commands.Send;
import edgruberman.bukkit.messageformatter.commands.Tell;
import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public final class Main extends JavaPlugin {

    public static MessageManager messageManager;

    private static ConfigurationFile configurationFile;

    private static String senderPlayer = null;
    private static String senderOther = null;
    private static String senderConsole = null;

    public void onLoad() {
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());

        Main.configurationFile = new ConfigurationFile(this);
    }

    public void onEnable() {
        this.loadConfiguration();

        new Messager(this);
        new Formatter(this);
        new Say(this);
        new Me(this);
        new Tell(this);
        new Reply(this);
        new Broadcast(this);
        new Send(this);

        Main.messageManager.log("Plugin Enabled");
    }

    public void onDisable() {
        Main.messageManager.log("Plugin Disabled");
    }

    public void loadConfiguration() {
        FileConfiguration config = Main.configurationFile.load();

        Main.senderPlayer = config.getString("senders.player");
        Main.senderOther = config.getString("senders.other");
        Main.senderConsole = config.getString("senders.console");

        Formatter.cancelQuitAfterKick = config.getBoolean("PlayerKickEvent.cancelQuit", Formatter.cancelQuitAfterKick);
    }

    public static MessageLevel getMessageLevel(final String path) {
        return MessageLevel.parse(Main.configurationFile.getConfig().getString(path + ".level"));
    }

    public static String getMessageFormat(final String path) {
        return Main.configurationFile.getConfig().getString(path + ".format");
    }

    public static EventPriority getEventPriority(final String path) {
        return EventPriority.valueOf(Main.configurationFile.getConfig().getString(path + ".priority"));
    }

    public static String formatSender(final CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            if (Main.senderConsole != null)
                return String.format(Main.senderConsole, sender.getName());

        if (sender instanceof Player)
            if (Main.senderPlayer != null)
                return String.format(Main.senderPlayer, ((Player) sender).getDisplayName());

        if (Main.senderOther != null)
            return String.format(Main.senderOther, sender.getName());

        return sender.getName();
    }

    /**
     * Strip any color codes from message if sender does not have permission to
     * use colors.
     *
     * @param sender
     * @param message
     * @return
     */
    public static String formatColors(final CommandSender sender, final String message) {
        if (sender.hasPermission("messageformatter.colors")) return message;

        String stripped = ChatColor.stripColor(message);
        stripped = MessageManager.stripColor(message);

        return stripped;
    }

}