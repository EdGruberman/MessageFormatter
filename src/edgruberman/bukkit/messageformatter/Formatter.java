package edgruberman.bukkit.messageformatter;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;
import edgruberman.bukkit.messagemanager.channels.Channel;

/**
 * Formats messages according to the plugin's configuration.
 */
final class Formatter implements Listener {

    static boolean cancelQuitAfterKick = false;
    static boolean cancelNextQuit = false;

    Formatter(final Plugin plugin) {
        // TODO Adjust event priorities according to configuration
        // for (Method method : this.getClass().getDeclaredMethods()) {
        //    if (method.getParameterTypes().length != 1 || !(method.getParameterTypes()[0].isInstance(Event.class))) continue;
        //    method.getAnnotation(EventHandler.class).
        //}

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (event.getResult().equals(Result.ALLOWED)) return;

        MessageLevel level = Main.getMessageLevel(event.getClass().getSimpleName() + "." + event.getResult().name());
        String message = Main.getMessageFormat(event.getClass().getSimpleName() + "." + event.getResult().name());
        ChatColor color = Main.messageManager.getColor(level, Channel.Type.PLAYER);

        message = String.format(message, event.getKickMessage());
        message = color.toString() + MessageManager.colorize(message, color);
        event.setKickMessage(message);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        String message = String.format(Main.getMessageFormat(event.getClass().getSimpleName()), Main.formatSender(event.getPlayer()));
        event.setJoinMessage(message);
    }

    @EventHandler
    public void onPlayerChat(final PlayerChatEvent event) {
        if (event.isCancelled()) return;

        String message = String.format(Main.getMessageFormat(event.getClass().getSimpleName()), event.getMessage(), Main.formatSender(event.getPlayer()));
        message = Main.formatColors(event.getPlayer(), message);
        event.setMessage(message);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        if (event.isCancelled()) return;

        if (Formatter.cancelQuitAfterKick) Formatter.cancelNextQuit = true;

        MessageLevel level = Main.getMessageLevel(event.getClass().getSimpleName());

        ChatColor base = Main.messageManager.getColor(level, Channel.Type.PLAYER);
        String reason = String.format(Main.getMessageFormat(event.getClass().getSimpleName() + ".reason"), event.getReason());
        reason = base + MessageManager.colorize(reason, base);
        event.setReason(reason);

        String message = String.format(Main.getMessageFormat(event.getClass().getSimpleName()), Main.formatSender(event.getPlayer()), reason);
        event.setLeaveMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (Formatter.cancelNextQuit) {
            event.setQuitMessage(null);
            Formatter.cancelNextQuit = false;
            return;
        }

        String message = String.format(Main.getMessageFormat(event.getClass().getSimpleName()), Main.formatSender(event.getPlayer()));
        event.setQuitMessage(message);
    }

}