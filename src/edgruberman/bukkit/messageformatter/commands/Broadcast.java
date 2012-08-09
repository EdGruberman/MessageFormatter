package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messaging.messages.TimestampedMessage;
import edgruberman.bukkit.messaging.recipients.ServerPlayers;

public final class Broadcast implements CommandExecutor {

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            Main.courier.send(sender, "requiresParameter", "<Message>");
            return false;
        }

        final String format = Main.translateColors(sender, args);
        Main.courier.deliver(new ServerPlayers(), new TimestampedMessage(format));
        return true;
    }

}
