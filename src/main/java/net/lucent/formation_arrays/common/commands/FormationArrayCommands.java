package net.lucent.formation_arrays.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.node_handling.DimensionNodeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;
@EventBusSubscriber
public class FormationArrayCommands {
    public static void registerNodeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nodes")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))

                .then(Commands.literal("view")
                        .executes(
                                FormationArrayCommands::viewNodes
                        )
                ));
    }

    private static int viewNodes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        DimensionNodeManager nodeManager = DimensionNodeManager.getNodeManger(level);
        Collection< BlockPos> nodeLocations = nodeManager.getAllNodeLocations();
        MutableComponent output = Component.literal("=== Nodes ===");
        for(BlockPos pos : nodeLocations){
            for(FormationNodeType type : nodeManager.getTypes(pos)){
                String state = nodeManager.isNodeTypeLoaded(pos,type) ? "Loaded" : "Unloaded";
                output.append(Component.literal("\n("+pos.toShortString()+")"+state));
            }
        }
        context.getSource().sendSuccess(()->output,false);
        return 1;
    }


    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        FormationArrayCommands.registerNodeCommands(event.getDispatcher());
    }

}
