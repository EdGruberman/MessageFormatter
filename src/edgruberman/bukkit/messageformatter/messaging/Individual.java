package edgruberman.bukkit.messageformatter.messaging;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * single {@link org.bukkit.command.CommandSender CommandSender}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 3.0.0
 */
public class Individual extends Recipients {

    protected CommandSender target;

    public Individual(final CommandSender target) {
        this.target = target;
    }

    @Override
    public Confirmation deliver(final Message message) {
        final String formatted = message.format(this.target).toString();
        this.target.sendMessage(formatted);
        return new Confirmation(this.level(), 1, "[SEND>{1}] {0}", message, Individual.this.target.getName());
    }

    /** console messages will be FINEST to allow for easier filtering of messages that will already appear in console */
    private Level level() {
        return (this.target instanceof Player ? Level.FINER : Level.FINEST);
    }

}
