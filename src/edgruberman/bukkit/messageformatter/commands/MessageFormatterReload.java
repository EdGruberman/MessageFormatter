package edgruberman.bukkit.messageformatter.commands;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.commands.util.Action;
import edgruberman.bukkit.messageformatter.commands.util.Context;
import edgruberman.bukkit.messageformatter.commands.util.Handler;
import edgruberman.bukkit.messagemanager.MessageLevel;

final class MessageFormatterReload extends Action {

    MessageFormatterReload(final Handler handler) {
        super(handler, "reload");
    }

    @Override
    public boolean perform(final Context context) {
        ((Main) context.handler.command.getPlugin()).loadConfiguration();
        Main.messageManager.respond(context.sender, "[" + context.handler.command.getPlugin().getDescription().getName()  + "] Configuration reloaded", MessageLevel.STATUS);
        return true;
    }

}