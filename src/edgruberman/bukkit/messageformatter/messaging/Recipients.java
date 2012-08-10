package edgruberman.bukkit.messageformatter.messaging;

import edgruberman.bukkit.messageformatter.messaging.messages.Confirmation;

public interface Recipients {

    public abstract Confirmation send(Message message);

}
