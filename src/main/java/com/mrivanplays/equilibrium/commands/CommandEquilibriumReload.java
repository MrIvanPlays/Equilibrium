package com.mrivanplays.equilibrium.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mrivanplays.equilibrium.Equilibrium;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandEquilibriumReload {

  public static void register(Equilibrium plugin) {
    plugin
        .getProxy()
        .getCommandManager()
        .register(
            new BrigadierCommand(
                LiteralArgumentBuilder.<CommandSource>literal("equilibrium")
                    .requires(source -> source.hasPermission("equilibrium.reload"))
                    .executes(
                        (context) -> {
                          plugin.reloadConfig();
                          context
                              .getSource()
                              .sendMessage(
                                  Identity.nil(),
                                  LegacyComponentSerializer.legacyAmpersand()
                                      .deserialize(
                                          plugin.getSettings().getMessage("reload-successful")));
                          return 1;
                        })));
  }
}
