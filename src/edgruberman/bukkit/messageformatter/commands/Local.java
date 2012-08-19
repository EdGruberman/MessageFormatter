package edgruberman.bukkit.messageformatter.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.messaging.LocalPlayers;
import edgruberman.bukkit.messageformatter.messaging.Message;

public final class Local implements CommandExecutor {

    private final Plugin plugin;

    public Local(final Plugin plugin) {
        this.plugin = plugin;
    }

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Main.courier.send(sender, "requiresPlayer", label);
            return true;
        }

        if (args.length < 1) {
            Main.courier.send(sender, "requiresArgument", "<Message>");
            return false;
        }

        final Location origin = ((Player) sender).getLocation();
        final double range = this.plugin.getConfig().getDouble("localRange", 100.0);
        final List<Message> messages = Main.courier.draft("local", Main.formatSender(sender), Main.translateColors(sender, args));
        Main.courier.submit(new LocalPlayers(origin, range), messages);
        return true;
    }

}