package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NullCoreItem extends Item {

    public NullCoreItem(Settings settings) {
        super(settings);
    }

    private static final StatusEffect[] NEGATIVE_EFFECTS = new StatusEffect[] {
            StatusEffects.POISON,
            StatusEffects.WITHER,
            StatusEffects.BLINDNESS,
            StatusEffects.SLOWNESS,
            StatusEffects.WEAKNESS,
            StatusEffects.MINING_FATIGUE
    };

    private static final String EFFECT_KEY = "NullcoreEffect";

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof LivingEntity living)) return;

        StatusEffect targetEffect = getStoredEffect(stack);

        // Видалення обраного негативного ефекту
        if (targetEffect != null && living.hasStatusEffect(targetEffect)) {
            living.removeStatusEffect(targetEffect);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) return TypedActionResult.success(stack);

        int index = getStoredEffectIndex(stack);
        int nextIndex = (index + 1) % NEGATIVE_EFFECTS.length;
        StatusEffect nextEffect = NEGATIVE_EFFECTS[nextIndex];

        setStoredEffect(stack, nextEffect);

        user.sendMessage(Text.literal("Nullcore tuned to: ").append(
                Text.translatable(nextEffect.getTranslationKey())).formatted(Formatting.AQUA), true);

        return TypedActionResult.success(stack);
    }

    private int getStoredEffectIndex(ItemStack stack) {
        NbtCompound tag = stack.getOrCreateNbt();
        return tag.getInt(EFFECT_KEY);
    }

    private StatusEffect getStoredEffect(ItemStack stack) {
        int index = getStoredEffectIndex(stack);
        if (index < 0 || index >= NEGATIVE_EFFECTS.length) return null;
        return NEGATIVE_EFFECTS[index];
    }

    private void setStoredEffect(ItemStack stack, StatusEffect effect) {
        NbtCompound tag = stack.getOrCreateNbt();
        for (int i = 0; i < NEGATIVE_EFFECTS.length; i++) {
            if (NEGATIVE_EFFECTS[i] == effect) {
                tag.putInt(EFFECT_KEY, i);
                return;
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        StatusEffect current = getStoredEffect(stack);
        tooltip.add(Text.literal("Makes you immune to a negative effect"));
        if (current != null) {
            tooltip.add(Text.literal("Immune to: ").append(
                    Text.translatable(current.getTranslationKey())).formatted(Formatting.DARK_GREEN));
        } else {
            tooltip.add(Text.literal("No effect selected").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal("Right-click to cycle").formatted(Formatting.GRAY, Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
