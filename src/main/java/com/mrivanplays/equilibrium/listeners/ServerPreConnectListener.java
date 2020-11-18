package com.mrivanplays.equilibrium.listeners;

import com.mrivanplays.equilibrium.Equilibrium;
import com.mrivanplays.equilibrium.util.Utils;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class ServerPreConnectListener implements EventHandler<ServerPreConnectEvent> {

  private final Equilibrium plugin;

  public ServerPreConnectListener(Equilibrium plugin) {
    this.plugin = plugin;
  }

  public void register() {
    plugin.getProxy().getEventManager().register(plugin, ServerPreConnectEvent.class, this);
  }

  @Override
  public void execute(ServerPreConnectEvent event) {
    Player player = event.getPlayer();
    if (!player.getCurrentServer().isPresent()) {
      RegisteredServer candidate =
          Utils.getBestCandidate(plugin.getSettings(), plugin.getProxy(), player);
      if (candidate != null) {
        event.setResult(ServerResult.allowed(candidate));
      }
      return;
    }
    if (plugin.getSettings().isOnlyInitialJoin()) {
      return;
    }
    RegisteredServer triedServer = event.getOriginalServer();
    if (plugin.getSettings().getHubsByPriority().contains(triedServer.getServerInfo().getName())) {
      // is a hub!
      RegisteredServer idealServer =
          Utils.getBestCandidate(plugin.getSettings(), plugin.getProxy(), player);
      if (!triedServer.getServerInfo().equals(idealServer.getServerInfo())) {
        event.setResult(ServerResult.allowed(idealServer));
      }
    }
  }
}
