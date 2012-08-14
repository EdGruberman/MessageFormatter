package edgruberman.bukkit.messageformatter.messaging.recipients;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messageformatter.messaging.Message;
import edgruberman.bukkit.messageformatter.messaging.Recipients;
import edgruberman.bukkit.messageformatter.messaging.messages.Confirmation;

public class LocalPlayers implements Recipients {

    protected static double range = -1;
    protected static double rangeSquared = -1;

    public static void setRange(final double range) {
        LocalPlayers.range = range;
        LocalPlayers.rangeSquared = Math.pow(range, 2);
    }



    protected Location origin;

    public LocalPlayers(final Location origin) {
        this.origin = origin;
    }

    @Override
    public Confirmation deliver(final Message message) {
        int count = 0;
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            if (this.origin.distanceSquared(player.getLocation()) <= LocalPlayers.rangeSquared) {
                player.sendMessage(message.format(player).toString());
                count++;
            }

        return new Confirmation(Level.FINER, count, "[LOCAL:{2}:[{3}]x={4},y={5},z={6}({1})] {0}", message, count, LocalPlayers.range, this.origin.getWorld().getName(), this.origin.getBlockX(), this.origin.getBlockY(), this.origin.getBlockZ());
    }

}
