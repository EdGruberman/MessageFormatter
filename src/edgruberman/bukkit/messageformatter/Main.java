package edgruberman.bukkit.messageformatter;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.commands.Broadcast;
import edgruberman.bukkit.messageformatter.commands.Local;
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
    private boolean firstEnable = true;

    private static String senderPlayer = null;
    private static String senderOther = null;
    private static String senderConsole = null;

    @Override
    public void onLoad() {
        Main.configurationFile = new ConfigurationFile(this);
        Main.configurationFile.load();
        this.setLoggingLevel();
        Main.messageManager = new MessageManager(this);
    }

    private void setLoggingLevel() {
        final String name = Main.configurationFile.getConfig().getString("logLevel", "INFO");
        Level level = MessageLevel.parse(name);
        if (level == null) level = Level.INFO;
        this.getLogger().setLevel(level);
    }

    @Override
    public void onEnable() {
        this.loadConfiguration();
        this.firstEnable = false;

        new Messager(this);
        new Formatter(this);

        new Say(this);
        new Me(this);
        new Tell(this);
        new Reply(this);
        new Local(this);
        new Broadcast(this);
        new Send(this);
    }

    public void loadConfiguration() {
        if (!this.firstEnable) Main.configurationFile.load();
        final FileConfiguration config = Main.configurationFile.getConfig();

        Main.senderPlayer = config.getString("senders.player");
        Main.senderOther = config.getString("senders.other");
        Main.senderConsole = config.getString("senders.console");

        Formatter.cancelQuitAfterKick = config.getBoolean("PlayerKickEvent.cancelQuit", Formatter.cancelQuitAfterKick);

        Local.distance = config.getInt("local.distance", Local.distance);
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