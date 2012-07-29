package edgruberman.bukkit.messageformatter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.channels.Recipient;

public class Messenger {

    public static Messenger load(final Plugin plugin) {
        return Messenger.load(plugin, plugin.getConfig().getConfigurationSection("messages"));
    }

    public static Messenger load(final Plugin plugin, final ConfigurationSection formats) {
        if (Bukkit.getPluginManager().getPlugin("MessageManager") != null) {
            plugin.getLogger().config("Message timestamps will use MessageManager plugin for personalized player time zones");
            return new MessageManagerMessenger(plugin, formats);
        }

        final Messenger messenger = new Messenger(plugin, formats);
        plugin.getLogger().config("Message timestamps will use server time zone: " + messenger.zone.getDisplayName());
        return messenger;
    }

    public final ConfigurationSection formats;

    protected final Plugin plugin;
    protected final TimeZone zone = TimeZone.getDefault();

    protected Messenger(final Plugin plugin, final ConfigurationSection formats) {
        this.plugin = plugin;
        this.formats = formats;
    }

    public TimeZone getZone(final CommandSender target) {
        return this.zone;
    }

    public String getFormat(final String path) {
        return this.formats.getString(path);
    }

    /**
     * Send message to individual player
     * (Managed: Standard formatting and creates log entry)
     *
     * @param target where to send message
     * @param path standard message format path
     * @param args arguments to supply to message format
     * @return formatted message
     */
    public String tell(final CommandSender target, final String path, final Object... args) {
        final String message = this.tellMessage(target, this.getFormat(path), args);
        this.plugin.getLogger().log((target instanceof ConsoleCommandSender? Level.FINEST : Level.FINER)
                , "#TELL@" + target.getName() + "# " + message);
        return message;
    }

    /**
     * Send message to individual player
     * (Unmanaged: Direct format provided and no log entry)
     *
     * @param target where to send message
     * @param format message format
     * @param args arguments to supply to message format
     * @return formatted message
     */
    public String tellMessage(final CommandSender target, final String format, final Object... args) {
        if (format == null) return null;

        return this.send(target, format, new GregorianCalendar(), args);
    }

    public int publish(final String permission, final String path, final Object... args) {
        final String format = this.getFormat(path);
        final int count = this.publishMessage(permission, format, args);
        final String message = this.format(format, new GregorianCalendar(this.zone), args);
        this.plugin.getLogger().finer("#PUBLISH@" + permission + "(" + count + ")# " + message);
        return count;
    }

    /**
     * Broadcast a message to all players with the specific permission
     */
    public int publishMessage(final String permission, final String format, final Object... args) {
        if (format == null) return -1;

        final Calendar now = new GregorianCalendar();
        int count = 0;
        for (final Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(permission))
            if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                this.send((CommandSender) permissible, format, now, args);
                count++;
            }

        return count;
    }

    public int broadcast(final String path, final Object... args) {
        final String format = this.getFormat(path);
        final int count = this.broadcastMessage(format, args);
        final String message = this.format(format, new GregorianCalendar(this.zone), args);
        this.plugin.getLogger().finest("#BROADCAST(" + count + ")# " + message);
        return count;
    }

    /**
     * Send a message to all players with the Server.BROADCAST_CHANNEL_USERS permission
     */
    public int broadcastMessage(final String format, final Object... args) {
        return this.publishMessage(Server.BROADCAST_CHANNEL_USERS, format, args);
    }

    public int localize(final Entity origin, final int range, final String path, final Object... args) {
        final String format = this.getFormat(path);
        final int count = this.localizeMessage(origin, range, format, args);
        final String message = this.format(format, new GregorianCalendar(this.zone), args);
        this.plugin.getLogger().finer("#LOCALIZE(" + count + ")# " + message);

        return count;
    }

    public int localizeMessage(final Entity origin, final int range, final String format, final Object... args) {
        if (format == null) return -1;

        final Calendar now = new GregorianCalendar();
        int count = 0;

        if (origin instanceof Player) {
            this.send((Player) origin, format, now, args);
            count++;
        }

        for (final Entity e : origin.getNearbyEntities(range, range, range))
            if (e instanceof Player) {
                this.send((Player) e, format, now, args);
                count++;
            }

        return count;
    }

    public String format(final String format, final Calendar now, final Object... args) {
        // Prepend time argument
        Object[] argsAll = null;
        argsAll = new Object[args.length + 1];
        argsAll[0] = now;
        if (args.length >= 1) System.arraycopy(args, 0, argsAll, 1, args.length);

        // Format message
        return String.format(format, argsAll);
    }

    protected String send(final CommandSender target, final String format, final Calendar now, final Object... args) {
        now.setTimeZone(this.getZone(target));
        final String message = this.format(format, now, args);
        target.sendMessage(message);
        return message;
    }

    protected static class MessageManagerMessenger extends Messenger {

        protected MessageManagerMessenger(final Plugin plugin, final ConfigurationSection formats) {
            super(plugin, formats);
        }

        @Override
        public TimeZone getZone(final CommandSender target) {
            if (target == null) return this.zone;

            final ConfigurationSection section = Recipient.configurationFile.getConfig().getConfigurationSection("CraftPlayer." + target.getName());
            if (section == null) return this.zone;

            return TimeZone.getTimeZone(section.getString("timezone", this.zone.getID()));
        }

    }

}
