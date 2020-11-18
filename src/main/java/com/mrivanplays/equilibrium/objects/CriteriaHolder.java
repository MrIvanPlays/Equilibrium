package com.mrivanplays.equilibrium.objects;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Map;

public class CriteriaHolder {

  public static CriteriaHolder of(Map<String, Object> values) {
    return new CriteriaHolder(values);
  }

  private Map<String, Object> values;

  private CriteriaHolder(Map<String, Object> values) {
    this.values = values;
  }

  public boolean meetsCriteria(Criteria criteria, Player player, RegisteredServer server) {
    if (!hasCriteria(criteria)) {
      return false;
    }
    try {
      return criteria.getCriteriaCheck().meets(player, server, values.get(criteria.getName()));
    } catch (Exception e) {
      throw new RuntimeException("An unexpected error while checking if criteria meets", e);
    }
  }

  public boolean hasCriteria(Criteria criteria) {
    return values.containsKey(criteria.getName());
  }
}
