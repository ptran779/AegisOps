//package com.github.ptran779.aegisops.command;
//
//import com.github.ptran779.aegisops.Config.Squad;
//import com.github.ptran779.aegisops.Config.SquadManager;
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.StringArgumentType;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.List;
//
//public class SquadCommand {
//  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//    dispatcher.register(Commands.literal("squad")
//        .then(Commands.literal("list")
//            .executes(ctx -> {
//              if (SquadManager.getAllSquads().isEmpty()) {
//                ctx.getSource().sendSuccess(() -> Component.literal("No squads found."), false);
//              } else {
//                ctx.getSource().sendSuccess(() -> Component.literal("Squads: " +
//                    String.join(", ", SquadManager.getAllSquads().keySet())), false);
//              }
//              return 1;
//            })
//        )
//        .then(Commands.literal("create")
//            .then(Commands.argument("name", StringArgumentType.word())
//                .executes(ctx -> {
//                  String name = StringArgumentType.getString(ctx, "name");
//                  Player player = ctx.getSource().getPlayerOrException();
//
//                  if (SquadManager.getSquad(name) != null) {
//                    ctx.getSource().sendFailure(Component.literal("Squad already exists."));
//                    return 0;
//                  }
//
//                  SquadManager.addSquad(name);
//                  SquadManager.getSquad(name).squadName = name;
//                  SquadManager.getSquad(name).playerSet.add(player);
//                  ctx.getSource().sendSuccess(() -> Component.literal("Squad " + name + " created."), false);
//                  return 1;
//                })
//            )
//        )
//        .then(Commands.literal("members")
//            .then(Commands.argument("name", StringArgumentType.word())
//                .executes(ctx -> {
//                  String name = StringArgumentType.getString(ctx, "name");
//                  Squad squad = SquadManager.getSquad(name);
//                  if (squad == null) {
//                    ctx.getSource().sendFailure(Component.literal("Squad not found."));
//                    return 0;
//                  }
//
//                  List<String> players = squad.playerSet.stream()
//                      .map(Entity::getName)
//                      .map(Component::getString)
//                      .toList();
//
//                  List<String> agents = squad.agentSet.stream()
//                      .map(Entity::getName)
//                      .toList();
//
//                  ctx.getSource().sendSuccess(() ->
//                          Component.literal("Squad " + name + " members:\nPlayers: " + players + "\nAgents: " + agents),
//                      false
//                  );
//                  return 1;
//                })
//            )
//        )
//    );
//  }
//}
