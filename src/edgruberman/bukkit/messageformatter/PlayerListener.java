package edgruberman.bukkit.messageformatter;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import edgruberman.bukkit.messagemanager.MessageLevel;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    
    private Main plugin;
    
    protected PlayerListener(Main plugin) {
        this.plugin = plugin;
        
        org.bukkit.plugin.PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, this, this.getEventPriority("broadcast.player.join"), this.plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_CHAT, this, this.getEventPriority("broadcast.player.chat"), this.plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT, this, this.getEventPriority("broadcast.player.quit"), this.plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_KICK, this, this.getEventPriority("broadcast.player.kick"), this.plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_LOGIN, this, this.getEventPriority("playerLogin"), this.plugin);
    }
    
    private Event.Priority getEventPriority(String path) {
        return Event.Priority.valueOf(Main.getConfigurationFile().getConfiguration().getString(path + ".priority"));
    }
    
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(Result.ALLOWED)) return;
        
        MessageLevel level = this.plugin.getMessageLevel("playerLogin.other");
        String message = this.plugin.getConfiguration().getString("playerLogon.other.reason");
        switch (event.getResult()) {
            case KICK_BANNED:
                level = this.plugin.getMessageLevel("playerLogin.banned");
                message = String.format(this.plugin.getMessageFormat("playerLogin")
                        , this.plugin.getConfiguration().getString("playerLogon.banned.reason"));
            case KICK_FULL:
                level = this.plugin.getMessageLevel("playerLogin.full");
                message = String.format(this.plugin.getMessageFormat("playerLogin")
                        , this.plugin.getConfiguration().getString("playerLogon.full.reason"));
            case KICK_WHITELIST:
                level = this.plugin.getMessageLevel("playerLogin.whitelist");
                message = String.format(this.plugin.getMessageFormat("playerLogin")
                        , this.plugin.getConfiguration().getString("playerLogon.whitelist.reason"));
        }
        
        event.setKickMessage(message);
        Main.getMessageManager().log(message, level);
     }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Main.getMessageManager().broadcast(
                String.format(this.plugin.getMessageFormat("broadcast.player.join"), event.getPlayer().getDisplayName())
                , this.plugin.getMessageLevel("broadcast.player.join")
        );
        
        event.setJoinMessage(null);
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;
        
        Main.getMessageManager().broadcast(
                String.format(this.plugin.getMessageFormat("broadcast.player.chat"), event.getMessage(), event.getPlayer().getDisplayName())
                , this.plugin.getMessageLevel("broadcast.player.chat")
        );
        
        //event.setFormat("");
        event.setCancelled(true);
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Main.getMessageManager().broadcast(
                String.format(this.plugin.getMessageFormat("broadcast.player.quit"), event.getPlayer().getDisplayName())
                , this.plugin.getMessageLevel("broadcast.player.quit")
        );
        
        event.setQuitMessage(null);
    }
    
    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) return;
        
        Main.getMessageManager().broadcast(
                String.format(this.plugin.getMessageFormat("broadcast.player.kick"), event.getPlayer().getDisplayName())
                , this.plugin.getMessageLevel("broadcast.player.kick")
        );
        
        event.setLeaveMessage(null);
    }
}