package edgruberman.bukkit.messageformatter.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.messaging.Message;
import edgruberman.bukkit.messageformatter.messaging.Individual;

public final class Send implements CommandExecutor {

    // usage: /<command> <Player> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            Main.courier.send(sender, "requiresArgument", "<Message>");
            return false;
        }

        if (args.length < 1) {
            Main.courier.send(sender, "requiresArgument", "<Player>");
            return false;
        }

        final Player target = Bukkit.getServer().getPlayerExact(args[0]);
        if (target == null) {
            Main.courier.send(sender, "playerNotFound", args[0]);
            return true;
        }

        final String format = Main.translateColors(sender, Arrays.copyOfRange(args, 1, args.length));
        Main.courier.submit(new Individual(target), Message.Factory.create(format).timestamp().build());
        return true;
    }

}
