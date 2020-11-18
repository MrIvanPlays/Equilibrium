package com.mrivanplays.equilibrium.objects;

import com.velocitypowered.api.network.ProtocolVersion;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public enum Criteria {
  PERMISSION("permission", (player, server, value) -> player.hasPermission(String.valueOf(value))),
  MAX_PLAYERS(
      "max_players",
      (player, server, value) ->
          server.getPlayersConnected().size() < Integer.parseInt(String.valueOf(value)),
      (value) -> {
        try {
          Integer.parseInt(String.valueOf(value));
          return true;
        } catch (NumberFormatException e) {
          return false;
        }
      }),
  MINECRAFT_VERSION(
      "mc_ver",
      (player, server, value) -> {
        String ver = String.valueOf(value);
        for (ProtocolVersion protocol : ProtocolVersion.values()) {
          if (!protocol.isLegacy()
              && !protocol.isUnknown()
              && protocol.getName().equalsIgnoreCase(ver)) {
            return player.getProtocolVersion() == protocol;
          }
        }
        return false;
      },
      (value) -> {
        String ver = String.valueOf(value);
        for (ProtocolVersion protocol : ProtocolVersion.values()) {
          if (!protocol.isLegacy()
              && !protocol.isUnknown()
              && protocol.getName().equalsIgnoreCase(ver)) {
            return true;
          }
        }
        return false;
      });

  private final String name;
  private final CriteriaCheck criteriaCheck;
  private final Predicate<Object> ensureValidValue;

  public static Map<String, Criteria> BY_NAME = new HashMap<>();

  static {
    for (Criteria criteria : Criteria.values()) {
      BY_NAME.put(criteria.getName(), criteria);
    }
  }

  Criteria(String name, CriteriaCheck criteriaCheck) {
    this(name, criteriaCheck, o -> true);
  }

  Criteria(String name, CriteriaCheck criteriaCheck, Predicate<Object> ensureValidValue) {
    this.name = name;
    this.criteriaCheck = criteriaCheck;
    this.ensureValidValue = ensureValidValue;
  }

  public String getName() {
    return name;
  }

  public CriteriaCheck getCriteriaCheck() {
    return criteriaCheck;
  }

  public boolean isValidValue(Object object) {
    return ensureValidValue.test(object);
  }

  public static Criteria fromName(String name) {
    return BY_NAME.get(name);
  }
}
