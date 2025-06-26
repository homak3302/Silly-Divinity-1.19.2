package net.homak.sillydivinity.common.custom.lib;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.homak.sillydivinity.common.custom.item.artifact.CombustionCharmArtifact;
import net.homak.sillydivinity.common.custom.item.artifact.DashRuneArtifact;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class ModEvents {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {

            if (entity instanceof PlayerEntity entity1) {
                for (int i = 0; i < entity1.getInventory().size(); i++) {
                    ItemStack stack = entity1.getInventory().getStack(i);
                    if (stack.getItem() instanceof CombustionCharmArtifact) {
                        // Create explosion
                        Vec3d pos = entity1.getPos();
                        entity.world.createExplosion(
                                entity1,
                                pos.x, pos.y, pos.z,
                                8f,
                                Explosion.DestructionType.DESTROY
                        );

                        stack.decrement(1);
                        break;
                    }
                }
            }

            return true;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                DashRuneArtifact.handleDashMovement(player, Hand.MAIN_HAND);
                DashRuneArtifact.handleDashMovement(player, Hand.OFF_HAND);
            }
        });
    }
}