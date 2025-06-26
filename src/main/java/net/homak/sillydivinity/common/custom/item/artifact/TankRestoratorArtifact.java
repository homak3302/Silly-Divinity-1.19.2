package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TankRestoratorArtifact extends Item {
    public TankRestoratorArtifact(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {


        if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getHealth() <= 10) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 1));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 1));
                }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Gives regeneration and resistance when below 5 hearts, does not stack"));
        tooltip.add(Text.literal("i can almost feel the healing energy of it")
                .formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
