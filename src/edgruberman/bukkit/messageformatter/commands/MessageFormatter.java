package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.commands.util.Handler;

public final class MessageFormatter extends Handler {

    public MessageFormatter(final JavaPlugin plugin) {
        super(plugin, "messageformatter");
        this.actions.add(new MessageFormatterReload(this));
    }

}
