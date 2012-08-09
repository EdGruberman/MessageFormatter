package edgruberman.bukkit.messageformatter.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messageformatter.Main;

public final class Tell implements CommandExecutor {

    private final Reply reply;

    public Tell(final Reply reply) {
        this.reply = reply;
    }

    // usage: /<command> <Player> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            Main.courier.send(sender, "requiresParameter", "<Message>");
            return false;
        }

        if (args.length < 1) {
            Main.courier.send(sender, "requiresParameter", "<Player>");
            return false;
        }

        final Player recipient = Bukkit.getServer().getPlayerExact(args[0]);
        if (recipient == null) {
            Main.courier.send(sender, "playerNotFound", args[0]);
            return true;
        }

        this.reply.send(recipient, sender, Main.translateColors(sender, Arrays.copyOfRange(args, 1, args.length)));
        return true;
    }

}
