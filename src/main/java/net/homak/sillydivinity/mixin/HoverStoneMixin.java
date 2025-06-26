package net.homak.sillydivinity.mixin;

import net.homak.sillydivinity.common.registry.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HoverStoneMixin {
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof PlayerEntity player) {
            if (player.getInventory().contains(new ItemStack(ModItems.HOVERSTONE))) {
                World world = player.getWorld();
                if  (fallDistance >= 4) {
                    // silly effects
                    world.playSound(player, player.getBlockPos(),
                            SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.BLOCKS,
                            0.5f, 1);
                    if (world instanceof ServerWorld serverWorld) {
                        Vec3d pos = player.getPos().subtract(0, -1, 0);

                        for (int i = 0; i < 5; i++) {
                            serverWorld.spawnParticles(ParticleTypes.CLOUD,
                                    pos.x + 0.5,
                                    pos.y,
                                    pos.z + 0.5,
                                    5,
                                    0.5, 0.0, 0.5,
                                    0.05);
                        }
                    }
                }
                cir.cancel(); // say "fall dmg no more"
            }
        }
    }
}