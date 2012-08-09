package edgruberman.bukkit.messageformatter.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messaging.Message;
import edgruberman.bukkit.messaging.messages.TimestampedConfigurationMessage;
import edgruberman.bukkit.messaging.recipients.Private;

public final class Reply implements CommandExecutor, Listener {

    private final Map<CommandSender, CommandSender> fromTo = new HashMap<CommandSender, CommandSender>();

    public Reply(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            Main.courier.send(sender, "requiresParameter", "<Message>");
            return false;
        }

        CommandSender recipient = this.fromTo.get(sender);
        if (recipient == null) {
            Main.courier.send(sender, "replyNotAvailable");
            return true;
        }

        final String name = recipient.getName();
        if (recipient instanceof Player) {
            // Ensure latest instance of CommandSender is used for players after they reconnect
            recipient = Bukkit.getPlayerExact(name);
            if (recipient != null && !((Player) recipient).isOnline()) recipient = null;
        }

        if (recipient == null) {
            Main.courier.send(sender, "playerNotFound", name);
            return true;
        }

        this.send(recipient, sender, Main.translateColors(sender, args));
        return true;
    }

    void send(final CommandSender recipient, final CommandSender sender, final String message) {
        final String senderFormatted = Main.formatSender(sender);
        final String recipientFormatted = Main.formatSender(recipient);

        final List<? extends Message> messages = TimestampedConfigurationMessage.create(Main.courier.getBase(), "tell", senderFormatted, recipientFormatted, message);
        for (final Message m : messages)
            Main.courier.deliver(new Private(sender, recipient), m);

        this.fromTo.put(sender, recipient);
        this.fromTo.put(recipient, sender);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent quit) {
        this.fromTo.remove(quit.getPlayer());
    }

}
