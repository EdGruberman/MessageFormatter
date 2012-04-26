package edgruberman.bukkit.messageformatter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerChat extends PlayerEvent implements Cancellable {

    protected String message;

    protected PlayerChat(final Player player, final String message) {
        super(player);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    // ---- Cancellable Event ----

    protected boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    // ---- Custom Event Handlers ----

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return PlayerChat.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerChat.handlers;
    }

}
