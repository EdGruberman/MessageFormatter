package edgruberman.bukkit.messageformatter.commands;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.util.BufferedYamlConfiguration;

public final class Nick implements CommandExecutor, Listener {

    private static final long MIN_SAVE_NICKS = 5000; // milliseconds

    private final BufferedYamlConfiguration nicks;
    private final Map<String, String> displays = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    public Nick(final Plugin plugin, final File nicks) {
        try { this.nicks = new BufferedYamlConfiguration(plugin, nicks, Nick.MIN_SAVE_NICKS);
        } catch (final Exception e) { throw new IllegalStateException("Unable to load nicks configuration; " + e); }
        for (final String name : this.nicks.getKeys(false)) this.displays.put(name, this.nicks.getString(name));
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // usage: /<command> <Player>[ <Display>]
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            Main.courier.send(sender, "requiresArgument", "<Player>");
            return false;
        }

        final String player = Bukkit.getOfflinePlayer(args[0]).getName();
        String display = (args.length >= 2 ? args[1] : null);
        if (display != null) {
            if (sender.hasPermission("messageformatter.colors")) display = ChatColor.translateAlternateColorCodes('&', display);
            this.displays.put(player, display);
        } else {
            this.displays.remove(player);
        }

        final Player online = Bukkit.getPlayer(player);
        if (online != null && online.isOnline()) online.setDisplayName(( display != null ? display : player ));

        this.nicks.set(player, display);
        this.nicks.queueSave();

        Main.courier.send(sender, "nick", player, ( display != null ? display : player ));
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(final PlayerLoginEvent login) {
        if (login.getResult() != Result.ALLOWED) return;

        final String display = this.displays.get(login.getPlayer().getName());
        if (display == null) return;

        login.getPlayer().setDisplayName(display);
    }

}
