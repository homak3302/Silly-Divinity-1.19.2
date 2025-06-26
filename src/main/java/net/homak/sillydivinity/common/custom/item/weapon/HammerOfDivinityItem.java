package net.homak.sillydivinity.common.custom.item.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HammerOfDivinityItem extends SwordItem {

    private static final int MAX_CHARGE_TIME = 70;

    private static final int COOLDOWN_TIME = 20 * 5;
    private boolean didFallingAttack = false;

    public HammerOfDivinityItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient) {
            itemStack.getOrCreateNbt().putInt("UsingStart", user.age);
        }

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity) || world.isClient) return;

        int chargeTime = this.getMaxUseTime(stack) - remainingUseTicks;
        float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1f);

        stack.removeSubNbt("UsingStart");

        double launchPower = 0.5 + (chargeRatio * 1.5); // scales from 0.5 to 2.0 upward
        user.setVelocity(user.getVelocity().add(0, launchPower, 0));
        user.velocityModified = true;

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                1.0f, 0.8f + world.random.nextFloat() * 0.4f);

        ((PlayerEntity) user).getItemCooldownManager().set(this, COOLDOWN_TIME);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            double verticalVelocity = player.getVelocity().y;

            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 254));

            if (verticalVelocity < -0.2) { // falling downward
                float fallDistance = player.fallDistance;
                float bonusDamage = calculateBonusDamage(fallDistance);
                target.damage(DamageSource.player(player), bonusDamage);
                if (!attacker.getWorld().isClient) {

                    ServerWorld serverWorld = (ServerWorld) attacker.getWorld();
                    SoundEvent soundEvent = fallDistance > 5.0F ?
                            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN :
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
                    serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                            soundEvent, attacker.getSoundCategory(), 1.0F, 1.0F);
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity player && player.isOnGround()) {
            if (this.didFallingAttack) {
                player.fallDistance = 0;
                this.didFallingAttack = false;
            }
        }
    }

    private float calculateBonusDamage(float fallDistance) {
        if (fallDistance <= 3.0F) {
            return 4.0F * fallDistance;
        } else if (fallDistance <= 8.0F) {
            return 12.0F + 2.0F * (fallDistance - 3.0F);
        } else {
            return 22.0F + fallDistance - 8.0F;
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_CHARGE_TIME * 20;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains("UsingStart");
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("UsingStart")) {
            int usingStart = stack.getNbt().getInt("UsingStart");
            int playerAge = MinecraftClient.getInstance().player != null
                    ? MinecraftClient.getInstance().player.age
                    : 0;

            int chargeTime = playerAge - usingStart;
            float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1f);
            Entity user1 = stack.getHolder();
            if (user1 instanceof PlayerEntity user && chargeRatio == 1f) {
                ServerWorld world = (ServerWorld) user.getWorld();
                world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.PLAYERS, 1f, 1);
            }

            return MathHelper.clamp(Math.round(13 * chargeRatio), 1, 13);
        }

        return 1;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("UsingStart")) {
            int usingStart = stack.getNbt().getInt("UsingStart");
            int playerAge = MinecraftClient.getInstance().player != null
                    ? MinecraftClient.getInstance().player.age
                    : 0;

            int chargeTime = playerAge - usingStart;
            float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1f);

            if (chargeRatio < 1f) {
                return 0xd2e2f3;
            } else {
                long worldTime = MinecraftClient.getInstance().world != null
                        ? MinecraftClient.getInstance().world.getTime()
                        : 0;
                return (worldTime % 20 < 10) ? 0xFFFFFF : 0x66A9C7; // Flashing when fully charged
            }
        }

        return 0xd2e2f3;
    }
}