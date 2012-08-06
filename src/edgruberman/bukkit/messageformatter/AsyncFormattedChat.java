package edgruberman.bukkit.messageformatter;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncFormattedChat extends AsyncPlayerChatEvent {

    public AsyncFormattedChat(final boolean async, final Player who, final String message, final Set<Player> players) {
        super(async, who, message, players);
    }

}
