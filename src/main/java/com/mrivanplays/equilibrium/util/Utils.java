package com.mrivanplays.equilibrium.util;

import com.mrivanplays.equilibrium.config.Settings;
import com.mrivanplays.equilibrium.objects.Criteria;
import com.mrivanplays.equilibrium.objects.CriteriaHolder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;

public class Utils {

  public static RegisteredServer getBestCandidate(
      Settings settings, ProxyServer proxy, Player player) {
    RegisteredServer currentCandidate = null;
    OUT:
    for (String hub : settings.getHubsByPriority()) {
      // some safety checks _yet again_
      Optional<RegisteredServer> registeredServerOpt = proxy.getServer(hub);
      if (!registeredServerOpt.isPresent()) {
        settings.removeHub(hub);
        continue;
      }
      if (!settings.getHubs().containsKey(hub)) {
        continue;
      }
      RegisteredServer server = registeredServerOpt.get();
      CriteriaHolder criteriaHolder = settings.getHubs().get(hub);
      for (Criteria criteria : Criteria.values()) {
        if (criteriaHolder.hasCriteria(criteria)
            && !criteriaHolder.meetsCriteria(criteria, player, server)) {
          continue OUT;
        }
      }
      currentCandidate = server;
    }
    return currentCandidate;
  }
}
