package edgruberman.bukkit.messageformatter.playeractivity;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.interpreters.Interpreter;

public class AsyncFormattedChat extends Interpreter {

    @EventHandler(ignoreCancelled = true)
    public void onEvent(final edgruberman.bukkit.messageformatter.AsyncFormattedChat event) {
        this.record(event.getPlayer(), event);
    }

}