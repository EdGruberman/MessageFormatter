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
        context.handler.command.getPlugin().onDisable();
        context.handler.command.getPlugin().onEnable();
        Main.messageManager.send(context.sender, "Configuration reloaded", MessageLevel.STATUS, false);
        return true;
    }

}