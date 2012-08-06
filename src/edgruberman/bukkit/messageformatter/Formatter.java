package edgruberman.bukkit.messageformatter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/** format displayed messages according to plugin configuration */
final class Formatter implements Listener {

    private final Plugin plugin;
    private boolean hideNextQuit = false;

    Formatter(final Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent login) {
        if (login.getResult() == Result.ALLOWED) return;

        final String reason = String.format(Main.messenger.getFormat("login." + login.getResult().name() + ".+reason"), login.getKickMessage());
        Main.messenger.broadcast("login." + login.getResult().name() + ".broadcast", Main.formatSender(login.getPlayer()), reason);
        login.setKickMessage(reason);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent join) {
        if (join.getJoinMessage() == null) return;

        Main.messenger.broadcast("join", Main.formatSender(join.getPlayer()));
        join.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent chat) {
        if (chat instanceof AsyncFormatChat) return;

        final AsyncFormatChat custom = new AsyncFormatChat(chat.isAsynchronous(), chat.getPlayer(), chat.getMessage(), chat.getRecipients());
        Bukkit.getServer().getPluginManager().callEvent(custom);
        if (custom.isCancelled()) return;

        Main.messenger.broadcast("chat", Main.formatSender(custom.getPlayer()), Main.formatColors(custom.getPlayer(), custom.getMessage()));
        chat.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(final PlayerDeathEvent death) {
        if (death.getDeathMessage() == null) return;

        Main.messenger.broadcast("death", Main.formatSender(death.getEntity()), death.getDeathMessage());
        death.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerKick(final PlayerKickEvent kick) {
        if (!this.plugin.getConfig().getBoolean("quitAfterKick")) this.hideNextQuit = true;
        if (kick.getLeaveMessage() == null) return;

        final String reason = String.format(Main.messenger.getFormat("kick.+reason"), kick.getReason());
        Main.messenger.broadcast("kick.broadcast", Main.formatSender(kick.getPlayer()), reason);
        kick.setReason(reason);
        kick.setLeaveMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKickMonitor(final PlayerKickEvent kick) {
        // if kick occurred, leave next quit hidden as configured
        if (!kick.isCancelled()) return;

        // kick did not occur, do not cancel next quit message
        this.hideNextQuit = false;
        return;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(final PlayerQuitEvent quit) {
        if (this.hideNextQuit) {
            quit.setQuitMessage(null);
            this.hideNextQuit = false;
            return;
        }

        if (quit.getQuitMessage() == null) return;

        Main.messenger.broadcast("quit", Main.formatSender(quit.getPlayer()));
        quit.setQuitMessage(null);
    }

}
