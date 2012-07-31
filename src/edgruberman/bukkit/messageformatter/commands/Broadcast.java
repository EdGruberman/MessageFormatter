package edgruberman.bukkit.messageformatter.commands;

import java.util.GregorianCalendar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;

public final class Broadcast implements CommandExecutor {

    private final Plugin plugin;

    public Broadcast(final Plugin plugin) {
        this.plugin = plugin;
    }

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            Main.messenger.tell(sender, "requiresParameter", "<Message>");
            return false;
        }

        final String format = Main.formatColors(sender, args);
        final int count = Main.messenger.broadcastMessage(format);
        final String message = Main.messenger.format(format, new GregorianCalendar(Main.messenger.getZone(null)));
        this.plugin.getLogger().finest("#BROADCAST(" + count + ")# " + message);
        return true;
    }

}
