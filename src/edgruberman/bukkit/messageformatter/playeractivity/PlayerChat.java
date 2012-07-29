package edgruberman.bukkit.messageformatter.playeractivity;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerChat extends Interpreter {

    @EventHandler
    public void onEvent(final edgruberman.bukkit.messageformatter.PlayerChat event) {
        this.player = event.getPlayer();
    }

}
