package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messageformatter.Main;

public final class Local implements CommandExecutor {

    private final int range;

    public Local(final int range) {
        this.range = range;
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

        Main.messenger.localize((Player) sender, this.range, "local", Main.formatSender(sender), Main.formatColors(sender, args));
        return true;
    }

}