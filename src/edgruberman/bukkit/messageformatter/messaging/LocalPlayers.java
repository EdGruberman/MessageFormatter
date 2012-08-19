package edgruberman.bukkit.messageformatter.messaging;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocalPlayers extends Recipients {

    protected final Location origin;
    protected final double rangeSquared;

    public LocalPlayers(final Location origin, final double range) {
        this.origin = origin;
        this.rangeSquared = Math.pow(range, 2);
    }

    @Override
    public Confirmation deliver(final Message message) {
        int count = 0;
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            if (this.origin.distanceSquared(player.getLocation()) <= this.rangeSquared) {
                player.sendMessage(message.format(player).toString());
                count++;
            }

        return new Confirmation(Level.FINER, count, "[LOCAL:{2}:[{3}]x={4},y={5},z={6}({1})] {0}"
                , message, count, Math.sqrt(this.rangeSquared), this.origin.getWorld().getName()
                , this.origin.getBlockX(), this.origin.getBlockY(), this.origin.getBlockZ());
    }

}
