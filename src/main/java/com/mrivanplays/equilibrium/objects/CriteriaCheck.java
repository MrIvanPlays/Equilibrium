package com.mrivanplays.equilibrium.objects;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

@FunctionalInterface
public interface CriteriaCheck {

  boolean meets(Player player, RegisteredServer server, Object value) throws Exception;
}
