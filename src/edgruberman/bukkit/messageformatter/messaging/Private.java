package edgruberman.bukkit.messageformatter.messaging;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Private extends Recipients {

    protected CommandSender sender;
    protected CommandSender recipient;

    public Private(final CommandSender sender, final CommandSender recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public Confirmation deliver(final Message message) {
        this.sender.sendMessage(message.format(this.sender).toString());
        this.recipient.sendMessage(message.format(this.recipient).toString());
        return new Confirmation(this.level(), 2, "[TELL|{2}@{1}] {0}", message, this.recipient.getName(), this.sender.getName());
    }

    private Level level() {
        return (this.sender instanceof Player && this.recipient instanceof Player ? Level.FINER : Level.FINEST);
    }

}
