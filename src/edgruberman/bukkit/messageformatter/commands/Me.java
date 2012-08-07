package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import edgruberman.bukkit.messageformatter.Main;

public final class Me implements CommandExecutor {

    // usage: /<command> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            Main.messenger.tell(sender, "requiresParameter", "<Message>");
            return false;
        }

        Main.messenger.broadcast("me", Main.formatSender(sender), Main.translateColors(sender, args));
        return true;
    }

}