package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.commands.util.Action;
import edgruberman.bukkit.messageformatter.commands.util.Context;
import edgruberman.bukkit.messageformatter.commands.util.Parser;

public final class Say extends Action {

    public Say(final JavaPlugin plugin) {
        super(plugin, "say");
    }

    @Override
    public boolean perform(final Context context) {
        String message = Parser.join(context.arguments);
        message = Main.formatColors(context.sender, message);
        if (message.length() < 1) return false;

        Main.messageManager.broadcast(
                String.format(Main.getMessageFormat("say"), message.trim(), Main.formatSender(context.sender))
                , Main.getMessageLevel("say")
        );
        return true;
    }

}