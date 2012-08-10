package edgruberman.bukkit.messageformatter.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.messaging.Message;
import edgruberman.bukkit.messageformatter.messaging.messages.TimestampedConfigurationMessage;
import edgruberman.bukkit.messageformatter.messaging.recipients.LocalPlayers;

public final class Local implements CommandExecutor {

    private final Plugin plugin;

    public Local(final Plugin plugin) {
        this.plugin = plugin;
    }

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Main.courier.send(sender, "requiresPlayer");
            return true;
        }

        if (args.length < 1) {
            Main.courier.send(sender, "requiresParameter", "<Message>");
            return false;
        }

        LocalPlayers.setRange(this.plugin.getConfig().getDouble("localRange", 100.0));
        final List<? extends Message> messages = TimestampedConfigurationMessage.create(Main.courier.getBase(), "local", Main.formatSender(sender), Main.translateColors(sender, args));
        for (final Message message : messages)
            Main.courier.deliver(new LocalPlayers(((Player) sender).getLocation()), message);

        return true;
    }

}