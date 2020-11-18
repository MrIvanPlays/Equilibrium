package com.mrivanplays.equilibrium.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

class SettingsGenerator {

  static ConfigurationNode obtainConfigurationNode(
      Path dataFolder, Function<String, InputStream> resourceAsStreamRetriever) throws IOException {
    if (!Files.exists(dataFolder)) {
      Files.createDirectories(dataFolder);
    }
    Path configFile = dataFolder.resolve("config.yml");
    if (!Files.exists(configFile)) {
      Files.createFile(configFile);
      try (InputStream input = resourceAsStreamRetriever.apply("config.yml")) {
        Files.copy(input, configFile, StandardCopyOption.REPLACE_EXISTING);
      }
    }
    return YAMLConfigurationLoader.builder().setPath(configFile).build().load();
  }
}
