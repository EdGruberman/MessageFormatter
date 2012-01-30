package edgruberman.bukkit.messageformatter.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messageformatter.Main;
import edgruberman.bukkit.messageformatter.commands.util.Action;
import edgruberman.bukkit.messageformatter.commands.util.Context;
import edgruberman.bukkit.messageformatter.commands.util.Parser;
import edgruberman.bukkit.messagemanager.MessageLevel;

public final class Broadcast extends Action {

    public Broadcast(final JavaPlugin plugin) {
        super(plugin, "broadcast");
    }

    @Override
    public boolean perform(final Context context) {
        if (context.arguments.size() < 1) return false;

        int messageStart = 0;
        boolean isTimestamped = Main.messageManager.useTimestampDefault;
        MessageLevel level = edgruberman.bukkit.messagemanager.Settings.DEFAULT_MESSAGE_LEVEL;
        if (context.arguments.size() >= 2) {
            if (context.arguments.get(messageStart).toLowerCase().endsWith("timestamp")) {
                isTimestamped = !context.arguments.get(messageStart).startsWith("-");
                messageStart++;
            }

            if (context.arguments.get(messageStart).matches("^%[^%]+%$")) {
                level = MessageLevel.parse(context.arguments.get(messageStart).substring(1, context.arguments.get(messageStart).length() - 1));
                if (level == null) {
                    Main.messageManager.respond(context.sender, "Unable to determine message level: " + context.arguments.get(messageStart), MessageLevel.WARNING, false);
                    return true;
                }

                messageStart++;
            }
        }

        String message = Parser.join(context.arguments.subList(messageStart, context.arguments.size()));
        message = Main.formatColors(context.sender, message);
        if (message.length() == 0) return false;

        Main.messageManager.broadcast(message, level, isTimestamped);
        return true;
    }

}