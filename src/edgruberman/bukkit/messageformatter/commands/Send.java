package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messageformatter.Main;

public final class Send implements CommandExecutor {

    private final Plugin plugin;

    public Send(final Plugin plugin) {
        this.plugin = plugin;
    }

    // usage: /<command> <Player> <Message>
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            Main.messenger.tell(sender, "requiresParameter", "<Message>");
            return false;
        }

        if (args.length < 1) {
            Main.messenger.tell(sender, "requiresParameter", "<Player>");
            return false;
        }

        final Player target = Bukkit.getServer().getPlayerExact(args[0]);
        if (target == null) {
            Main.messenger.tell(sender, "playerNotFound", args[0]);
            return true;
        }

        final String message = Main.messenger.tellMessage(target, Main.formatColors(sender, args));
        this.plugin.getLogger().finer("#SEND@" + target.getName() + "# " + message);
        return true;
    }

}
