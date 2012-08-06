package edgruberman.bukkit.messageformatter.commands;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;

public final class Local implements CommandExecutor {

    private final Plugin plugin;

    public Local(final Plugin plugin) {
        this.plugin = plugin;
    }

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Main.messenger.tell(sender, "requiresPlayer");
            return true;
        }

        if (args.length < 1) {
            Main.messenger.tell(sender, "requiresParameter", "<Message>");
            return false;
        }

        final double rangeSquared = Math.pow(this.plugin.getConfig().getDouble("localRange", 100.0), 2);
        this.localize((Player) sender, rangeSquared, "local", Main.formatSender(sender), Main.formatColors(sender, args));
        return true;
    }

    private void localize(final Entity sender, final double rangeSquared, final String path, final Object... args) {
        final Calendar now = new GregorianCalendar(Main.messenger.getZone(null));
        for (final String format : Main.messenger.getFormatList(path)) {
            final int count = this.localizeMessage(sender, rangeSquared, format, args);
            this.plugin.getLogger().finer("#LOCALIZE(" + count + ")# " + Main.messenger.format(format, now, args));
        }
    }

    private int localizeMessage(final Entity sender, final double rangeSquared, final String format, final Object... args) {
        if (format == null) return -1;

        final Calendar now = new GregorianCalendar();
        int count = 0;

        final Location origin = sender.getLocation();
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            if (origin.distanceSquared(player.getLocation()) <= rangeSquared) {
                Main.messenger.send(player, format, now, args);
                count++;
            }

        return count;
    }

}