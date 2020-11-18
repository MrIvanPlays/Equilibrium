package com.mrivanplays.equilibrium.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mrivanplays.equilibrium.Equilibrium;
import com.mrivanplays.equilibrium.util.Utils;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder.Status;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandHub {

  public static void register(Equilibrium plugin) {
    if (plugin.getSettings().isHubCommandEnabled()) {
      plugin
          .getProxy()
          .getCommandManager()
          .register(
              plugin.getProxy().getCommandManager().metaBuilder("lobby").build(),
              new BrigadierCommand(
                  LiteralArgumentBuilder.<CommandSource>literal("hub")
                      .requires(
                          sender -> {
                            if (plugin.getSettings().getHubCommandPermission().isEmpty()) {
                              return true;
                            }
                            return sender.hasPermission(
                                plugin.getSettings().getHubCommandPermission());
                          })
                      .executes(
                          context -> {
                            if (!(context.getSource() instanceof Player)) {
                              context
                                  .getSource()
                                  .sendMessage(
                                      Identity.nil(),
                                      LegacyComponentSerializer.legacyAmpersand()
                                          .deserialize(
                                              plugin.getSettings().getMessage("not-player")));
                              return 1;
                            }
                            Player player = (Player) context.getSource();
                            RegisteredServer ideal =
                                Utils.getBestCandidate(
                                    plugin.getSettings(), plugin.getProxy(), player);
                            if (ideal != null) {
                              player
                                  .createConnectionRequest(ideal)
                                  .connect()
                                  .thenAccept(
                                      result -> {
                                        if (result.getStatus() == Status.SUCCESS) {
                                          player.sendMessage(
                                              Identity.nil(),
                                              LegacyComponentSerializer.legacyAmpersand()
                                                  .deserialize(
                                                      plugin
                                                          .getSettings()
                                                          .getMessage("connected")
                                                          .replace(
                                                              "%hub%",
                                                              ideal.getServerInfo().getName())));
                                        } else {
                                          if (result.getReasonComponent().isPresent()) {
                                            player.sendMessage(
                                                Identity.nil(), result.getReasonComponent().get());
                                          }
                                        }
                                      });
                            }
                            return 1;
                          })));
    }
  }
}
