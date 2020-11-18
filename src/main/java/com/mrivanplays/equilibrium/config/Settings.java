package com.mrivanplays.equilibrium.config;

import com.mrivanplays.equilibrium.objects.Criteria;
import com.mrivanplays.equilibrium.objects.CriteriaHolder;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;

public class Settings {

  public static Settings load(
      Path dataFolder,
      Function<String, InputStream> resourceAsStreamRetriever,
      Logger logger,
      ProxyServer proxy) {
    try {
      Map<String, CriteriaHolder> hubsMap = new ConcurrentHashMap<>();
      ConfigurationNode node =
          SettingsGenerator.obtainConfigurationNode(dataFolder, resourceAsStreamRetriever);
      List<String> hubs = node.getNode("hubs").getList(String::valueOf);
      for (String hub : hubs) {
        if (!proxy.getServer(hub).isPresent()) {
          logger.warn(
              "A hub ( lobby ) going by the name of \""
                  + hub
                  + "\" is not registered within Velocity. Skipping handling for it.");
          continue;
        }
        Map<String, Object> criteria = new ConcurrentHashMap<>();
        Map<Object, ? extends ConfigurationNode> unparsedCriteria =
            node.getNode("criteria").getNode(hub).getChildrenMap();
        if (unparsedCriteria.isEmpty()) {
          hubsMap.put(hub, CriteriaHolder.of(criteria));
          continue;
        }
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : unparsedCriteria.entrySet()) {
          String key = String.valueOf(entry.getKey());
          Criteria criteriaObject = Criteria.fromName(key);
          if (criteriaObject == null) {
            logger.warn("Hub ( lobby ) \"" + hub + "\" specified an unknown criteria: " + key);
            continue;
          }
          Object val = entry.getValue().getValue();
          if (!criteriaObject.isValidValue(val)) {
            logger.warn(
                "Hub ( lobby ) \"" + hub + "\" has specified a invalid value for criteria: " + key);
            continue;
          }
          criteria.put(key, val);
        }
        hubsMap.put(hub, CriteriaHolder.of(criteria));
      }
      return new Settings(node, hubsMap);
    } catch (IOException e) {
      throw new RuntimeException(
          "An unexpected IO happened while trying to read/create (default) configuration", e);
    }
  }

  private final ConfigurationNode configNode;
  private Map<String, CriteriaHolder> hubs;
  private List<String> hubsByPriority;

  private Settings(ConfigurationNode configNode, Map<String, CriteriaHolder> hubs) {
    this.configNode = configNode;
    this.hubs = hubs;
    this.hubsByPriority = configNode.getNode("hubs").getList(String::valueOf);
  }

  public Map<String, CriteriaHolder> getHubs() {
    return hubs;
  }

  public List<String> getHubsByPriority() {
    return hubsByPriority;
  }

  public void removeHub(String hub) {
    hubs.remove(hub);
  }

  public String getMessage(String key) {
    return configNode.getNode("messages").getNode(key).getString();
  }

  public boolean isOnlyInitialJoin() {
    return configNode.getNode("only-initial-join").getBoolean();
  }

  public boolean isHubCommandEnabled() {
    return configNode.getNode("enable-hub-command").getBoolean();
  }

  public String getHubCommandPermission() {
    return configNode.getNode("hub-command-permission").getString();
  }
}
