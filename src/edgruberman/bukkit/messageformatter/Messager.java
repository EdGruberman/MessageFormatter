package edgruberman.bukkit.messageformatter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Broadcasts formatted messages through the MessageManager interface.
 */
final class Messager implements Listener {

    Messager(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getJoinMessage() == null) return;

        Main.messageManager.broadcast(event.getJoinMessage(), Main.getMessageLevel(event.getClass().getSimpleName()));
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(final PlayerChatEvent event) {
        if (event.isCancelled() || event.getMessage() == null) return;

        PlayerChat custom = new PlayerChat(event.getPlayer(), event.getMessage());
        Bukkit.getServer().getPluginManager().callEvent(custom);
        if (custom.isCancelled()) return;

        event.setCancelled(true);

        Main.messageManager.broadcast(event.getMessage(), Main.getMessageLevel(event.getClass().getSimpleName()));
        event.setMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(final PlayerKickEvent event) {
        if (event.isCancelled() || event.getLeaveMessage() == null) return;

        Main.messageManager.broadcast(event.getLeaveMessage(), Main.getMessageLevel(event.getClass().getSimpleName()));
        event.setLeaveMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKickMonitor(final PlayerKickEvent event) {
        if (!event.isCancelled() && event.getLeaveMessage() != null) return;

        // Do not cancel next quit message since kick message was never displayed
        Formatter.cancelNextQuit = false;
        return;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (event.getQuitMessage() == null) return;

        Main.messageManager.broadcast(event.getQuitMessage(), Main.getMessageLevel(event.getClass().getSimpleName()));
        event.setQuitMessage(null);
    }

}