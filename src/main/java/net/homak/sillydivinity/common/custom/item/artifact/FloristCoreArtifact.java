package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FloristCoreArtifact extends Item {
    public FloristCoreArtifact(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof LivingEntity livingEntity) {

            BlockPos standing = livingEntity.getBlockPos().down();
            BlockState stateBelow = world.getBlockState(standing);

            boolean isOnNaturalBlock = stateBelow.isOf(Blocks.GRASS_BLOCK) ||
                    stateBelow.isOf(Blocks.DIRT) ||
                    stateBelow.isOf(Blocks.PODZOL) ||
                    stateBelow.isOf(Blocks.COARSE_DIRT) ||
                    stateBelow.isOf(Blocks.MOSS_BLOCK) ||
                    stateBelow.getMaterial() == Material.LEAVES;

            if (isOnNaturalBlock) {
                livingEntity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 20, 1));

                if (world instanceof ServerWorld sworld) {
                    if (livingEntity.age % 10 == 1) {
                        sworld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                standing.getX() + 0.5, standing.getY() + 1, standing.getZ() + 0.5,
                                10, 0.3, 0, 0.3, 0.1);
                    }
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Grants regeneration if standing on natural blocks"));
        tooltip.add(Text.literal("Natural blocks list: foliage, grass, moss, podzol, dirt"));
        tooltip.add(Text.literal("sometimes herbal medicine is actually good").formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
