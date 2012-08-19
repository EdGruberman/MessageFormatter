package edgruberman.bukkit.messageformatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.commands.Broadcast;
import edgruberman.bukkit.messageformatter.commands.Local;
import edgruberman.bukkit.messageformatter.commands.Me;
import edgruberman.bukkit.messageformatter.commands.Nick;
import edgruberman.bukkit.messageformatter.commands.Reload;
import edgruberman.bukkit.messageformatter.commands.Reply;
import edgruberman.bukkit.messageformatter.commands.Say;
import edgruberman.bukkit.messageformatter.commands.Send;
import edgruberman.bukkit.messageformatter.commands.Tell;
import edgruberman.bukkit.messageformatter.messaging.ConfigurationCourier;
import edgruberman.bukkit.messageformatter.messaging.Courier;

public final class Main extends JavaPlugin {

    private static final Version MINIMUM_CONFIGURATION = new Version("5.2.0");

    public static Courier courier;

    @Override
    public void onEnable() {
        this.reloadConfig();
        Main.courier = ConfigurationCourier.Factory.create(this).setBase("messages").build();

        Bukkit.getPluginManager().registerEvents(new Formatter(this, this.getConfig().getLong("asyncPermissionCache") * 60 * 20), this);

        this.getCommand("messageformatter:say").setExecutor(new Say());
        this.getCommand("messageformatter:me").setExecutor(new Me());
        this.getCommand("messageformatter:local").setExecutor(new Local(this));
        final Reply reply = new Reply(this);
        this.getCommand("messageformatter:reply").setExecutor(reply);
        this.getCommand("messageformatter:tell").setExecutor(new Tell(reply));
        this.getCommand("messageformatter:broadcast").setExecutor(new Broadcast());
        this.getCommand("messageformatter:send").setExecutor(new Send());
        this.getCommand("messageformatter:nick").setExecutor(new Nick(this, new File(this.getDataFolder(), "nicks.yml")));
        this.getCommand("messageformatter:reload").setExecutor(new Reload(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Main.courier = null;
    }

    @Override
    public void reloadConfig() {
        this.loadConfig("config.yml", Main.MINIMUM_CONFIGURATION);
        super.reloadConfig();
        this.setLogLevel(this.getConfig().getString("logLevel"));
    }

    @Override
    public void saveDefaultConfig() {
        this.extractConfig("config.yml", false);
    }

    private Configuration loadConfig(final String resource, final Version required) {
        // extract default if not existing
        this.extractConfig(resource, false);

        final File existing = new File(this.getDataFolder(), resource);
        final Configuration config = YamlConfiguration.loadConfiguration(existing);
        if (required == null) return config;

        // verify required or later version
        final Version version = new Version(config.getString("version"));
        if (version.compareTo(required) >= 0) return config;

        this.archiveConfig(resource, version);

        // extract default and reload
        return this.loadConfig(resource, null);
    }

    private void extractConfig(final String resource, final boolean replace) {
        final Charset source = Charset.forName("UTF-8");
        final Charset target = Charset.defaultCharset();
        if (target.equals(source)) {
            super.saveResource(resource, replace);
            return;
        }

        final File config = new File(this.getDataFolder(), resource);
        if (config.exists()) return;

        final char[] cbuf = new char[1024]; int read;
        try {
            final Reader in = new BufferedReader(new InputStreamReader(this.getResource(resource), source));
            final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config), target));
            while((read = in.read(cbuf)) > 0) out.write(cbuf, 0, read);
            out.close(); in.close();

        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not extract configuration file \"" + resource + "\" to " + config.getPath() + "\";" + e);
        }
    }

    private void archiveConfig(final String resource, final Version version) {
        final String backupName = "{0} - Archive version {1} - {2,date,yyyyMMddHHmmss}.yml";
        final File backup = new File(this.getDataFolder(), MessageFormat.format(backupName, resource.replaceAll("(?i)\\.yml$", ""), version, new Date()));
        final File existing = new File(this.getDataFolder(), resource);

        if (!existing.renameTo(backup))
            throw new IllegalStateException("Unable to archive configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");

        this.getLogger().warning("Archived configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");
    }

    private void setLogLevel(final String name) {
        Level level;
        try { level = Level.parse(name); } catch (final Exception e) {
            level = Level.INFO;
            this.getLogger().warning("Log level defaulted to " + level.getName() + "; Unrecognized java.util.logging.Level: " + name + "; " + e);
        }

        // only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().config("Log level set to: " + this.getLogger().getLevel());
    }

    // TODO ensure thread-safe config
    public static String formatSender(final CommandSender sender) {
        if (sender instanceof Player)
            return Main.courier.format("names.+player", ((Player) sender).getDisplayName());

        if (sender instanceof ConsoleCommandSender)
            return Main.courier.format("names.+console", sender.getName());

        return Main.courier.format("names.+other", sender.getName());
    }

    public static String translateColors(final CommandSender sender, final String[] args) {
        final String message = Main.join(Arrays.asList(args), " ");
        if (!sender.hasPermission("messageformatter.colors")) return message;

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Combine all the elements of a list together with a delimiter between each
     *
     * @param list list of elements to join
     * @param delim delimiter to place between each element
     * @return string combined with all elements and delimiters
     */
    private static String join(final List<String> list, final String delim) {
        if (list == null || list.isEmpty()) return "";

        final StringBuilder sb = new StringBuilder();
        for (final String s : list) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());

        return sb.toString();
    }

}
