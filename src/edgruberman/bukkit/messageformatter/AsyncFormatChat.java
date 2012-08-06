package edgruberman.bukkit.messageformatter;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/** generated to distinguish between regular chat allowed to display normally and the MessageFormatter plugin formatting chat instead */
public class AsyncFormatChat extends AsyncPlayerChatEvent {

    public AsyncFormatChat(final boolean async, final Player who, final String message, final Set<Player> players) {
        super(async, who, message, players);
    }

}
