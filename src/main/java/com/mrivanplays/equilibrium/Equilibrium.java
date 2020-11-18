package com.mrivanplays.equilibrium;

import com.google.inject.Inject;
import com.mrivanplays.equilibrium.commands.CommandEquilibriumReload;
import com.mrivanplays.equilibrium.commands.CommandHub;
import com.mrivanplays.equilibrium.config.Settings;
import com.mrivanplays.equilibrium.listeners.ServerPreConnectListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;

public class Equilibrium {

  private final ProxyServer proxy;
  private final Logger logger;

  private final Path dataFolder;

  private Settings settings;

  @Inject
  public Equilibrium(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataFolder = dataFolder;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    reloadConfig();
    new ServerPreConnectListener(this).register();
    CommandEquilibriumReload.register(this);
    CommandHub.register(this);
    logger.info("Enabled");
  }

  @Subscribe
  public void onProxyShutdown(ProxyShutdownEvent event) {
    logger.info("Thank you for using Equilibrium.");
  }

  @Subscribe
  public void onProxyReload(ProxyReloadEvent event) {
    logger.info("Configuration reload triggered ( Velocity has just reloaded )");
    reloadConfig();
    logger.info("Configuration has been reloaded successfully");
  }

  public void reloadConfig() {
    settings =
        Settings.load(
            dataFolder, (s) -> getClass().getClassLoader().getResourceAsStream(s), logger, proxy);
    if (!settings.isHubCommandEnabled()) {
      proxy.getCommandManager().unregister("lobby");
      proxy.getCommandManager().unregister("hub");
    } else {
      CommandHub.register(this);
    }
  }

  public ProxyServer getProxy() {
    return proxy;
  }

  public Settings getSettings() {
    return settings;
  }
}
