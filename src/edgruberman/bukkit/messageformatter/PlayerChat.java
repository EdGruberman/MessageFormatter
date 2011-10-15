package edgruberman.bukkit.messageformatter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerChat extends Event implements Cancellable {
    
    public final static String NAME = Main.eventPrefix + ".PLAYER_CHAT";
    
    protected Player player;
    protected String message;
    protected boolean cancelled = false;
    
    protected PlayerChat(final Player player, final String message) {
        super(PlayerChat.NAME);
        this.player = player;
        this.message = message;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
