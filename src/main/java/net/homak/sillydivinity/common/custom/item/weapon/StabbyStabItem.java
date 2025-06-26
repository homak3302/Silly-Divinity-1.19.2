package net.homak.sillydivinity.common.custom.item.weapon;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StabbyStabItem extends SwordItem {

    private static final int EFFECT_DURATION = 20 * 20;
    public StabbyStabItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        user.setVelocity(user.getRotationVec(1f).multiply(2.5).add(user.getVelocity()));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, EFFECT_DURATION, 1));

        user.getItemCooldownManager().set(this, 20 * 10);

        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("WIP, not obtainable").formatted(Formatting.BOLD).formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
