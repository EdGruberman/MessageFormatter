package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.commands.util.Action;
import edgruberman.bukkit.messageformatter.commands.util.Context;
import edgruberman.bukkit.messageformatter.commands.util.Parser;

public final class Me extends Action {

    public Me(final JavaPlugin plugin) {
        super(plugin, "me");
    }

    @Override
    public boolean perform(final Context context) {
        String message = Parser.join(context.arguments);
        message = Main.formatColors(context.sender, message);
        if (message.length() < 1) return false;

        Main.messageManager.broadcast(
                String.format(Main.getMessageFormat("me"), message.trim(), Main.formatSender(context.sender))
                , Main.getMessageLevel("me")
        );
        return true;
    }

}