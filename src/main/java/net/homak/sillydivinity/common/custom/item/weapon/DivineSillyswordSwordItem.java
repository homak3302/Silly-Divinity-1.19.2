package net.homak.sillydivinity.common.custom.item.weapon;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DivineSillyswordSwordItem extends SwordItem {
    private static final int MAX_CHARGE_TIME = 80;
    private static final float KNOCKBACK_STRENGTH = 3.5f;
    private static final float MAX_SHOCKWAVE_RADIUS = 15f;
    private static final int PARTICLE_COUNT = 20;
    private static final int SHOCKWAVE_DURATION = 1000 * 20;
    private static final float SHOCKWAVE_SPEED = 0.25f;
    private static final int FULL_CHARGE_COOLDOWN = 10 * 20;

    public DivineSillyswordSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
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

        startExpandingShockwave((ServerWorld) world, user.getPos(), chargeRatio, (PlayerEntity) user);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                1.0f, 0.8f + world.random.nextFloat() * 0.4f);

        ((PlayerEntity) user).getItemCooldownManager().set(this, (int) (FULL_CHARGE_COOLDOWN * chargeRatio));
    }

    private void startExpandingShockwave(ServerWorld world, Vec3d center, float strength, PlayerEntity user) {
        Set<LivingEntity> hitEntities = new HashSet<>();

        for (int tick = 0; tick < SHOCKWAVE_DURATION; tick++) {
            final int currentTick = tick;
            world.getServer().execute(() -> {
                if (world.isClient) return;

                double currentRadius = SHOCKWAVE_SPEED * currentTick;
                if (currentRadius > MAX_SHOCKWAVE_RADIUS * strength) return;

                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double angle = 2 * Math.PI * i / PARTICLE_COUNT;
                    double x = center.x + Math.cos(angle) * currentRadius;
                    double z = center.z + Math.sin(angle) * currentRadius;

                    world.spawnParticles(ParticleTypes.END_ROD, x, center.y, z, 1, 0, 0, 0, 0.05);
                }

                if (currentTick > 0) {
                    double innerRadius = Math.max(0, currentRadius - SHOCKWAVE_SPEED);
                    Box area = new Box(
                            center.x - currentRadius, center.y - 2, center.z - currentRadius,
                            center.x + currentRadius, center.y + 2, center.z + currentRadius
                    );

                    for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e ->
                            e != user && !hitEntities.contains(e) &&
                                    distanceSquared(center, e.getPos()) >= innerRadius * innerRadius &&
                                    distanceSquared(center, e.getPos()) <= currentRadius * currentRadius)) {

                        Vec3d direction = entity.getPos().subtract(center).normalize();
                        entity.setVelocity(
                                direction.x * KNOCKBACK_STRENGTH * strength,
                                0.5 * strength,
                                direction.z * KNOCKBACK_STRENGTH * strength
                        );

                        entity.damage(DamageSource.CRAMMING, 6);
                        hitEntities.add(entity);
                    }
                }
            });
        }
    }

    private double distanceSquared(Vec3d a, Vec3d b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return dx * dx + dz * dz;
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
        // Show the bar only if charging
        return stack.hasNbt() && stack.getNbt().contains("UsingStart");
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("UsingStart")) {
            int usingStart = stack.getNbt().getInt("UsingStart");
            int playerAge = 0;

            // Try to get the current player age if available (client side)
            if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                playerAge = net.minecraft.client.MinecraftClient.getInstance().player.age;
            }

            int chargeTime = playerAge - usingStart;
            float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1f);
            Entity user1 = stack.getHolder();
            if (user1 instanceof PlayerEntity user) {
                if (chargeRatio == 1f) {
                    ServerWorld world = (ServerWorld) user.getWorld();
                    world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS, 1f, 1);
                }
            }

            return MathHelper.clamp(Math.round(13 * chargeRatio), 1, 13);
        }

        return 1;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("UsingStart")) {
            int usingStart = stack.getNbt().getInt("UsingStart");
            int playerAge = 0;

            // Try to get the current player age if available (client side)
            if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                playerAge = net.minecraft.client.MinecraftClient.getInstance().player.age;
            }

            int chargeTime = playerAge - usingStart;
            float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1f);

            if (chargeRatio < 1f) {
                return 0xd2e2f3; // White while charging
            } else {
                long worldTime = net.minecraft.client.MinecraftClient.getInstance().world != null
                        ? net.minecraft.client.MinecraftClient.getInstance().world.getTime()
                        : 0;
                return (worldTime % 20 < 10) ? 0xFFFFFF : 0x66A9C7; // Flashing blue
            }
        }

        return 0xFFFFFF;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Guys its silly trust"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
