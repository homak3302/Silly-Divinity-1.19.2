package net.homak.sillydivinity.common.custom.item.weapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class PoleaxeItem extends AxeItem {
    public PoleaxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        target.setVelocity(0, 0, 0);

        attacker.setHealth(attacker.getHealth() + 2);

        return super.postHit(stack, target, attacker);
    }
}
