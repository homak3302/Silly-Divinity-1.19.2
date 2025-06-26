package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DashRuneArtifact extends Item {
    private static final int DASH_DURATION = 30;
    private static final float DASH_SPEED = 1.2f;
    private static final String DASH_TIME_KEY = "DashTime";
    private static final String DASH_YAW_KEY = "DashYaw";
    private static final String DASH_PITCH_KEY = "DashPitch";

    public DashRuneArtifact(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Start dashing
            user.getItemCooldownManager().set(this, 10 * 20);
            user.incrementStat(Stats.USED.getOrCreateStat(this));

            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt(DASH_TIME_KEY, DASH_DURATION);
            nbt.putFloat(DASH_YAW_KEY, user.getYaw());
            nbt.putFloat(DASH_PITCH_KEY, user.getPitch());

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS,
                    0.5F, 1.5F);

            spawnDashParticles(user, true);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    public static void handleDashMovement(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof DashRuneArtifact)) return;

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(DASH_TIME_KEY)) return;

        int dashTime = nbt.getInt(DASH_TIME_KEY);
        if (dashTime > 0) {
            float yaw = nbt.getFloat(DASH_YAW_KEY);
            float pitch = nbt.getFloat(DASH_PITCH_KEY);

            float f = -yaw * ((float)Math.PI / 180);
            float g = pitch * ((float)Math.PI / 180);
            float h = MathHelper.cos(f);
            float k = MathHelper.sin(f);
            float l = MathHelper.cos(g);
            float m = MathHelper.sin(g);
            Vec3d dashDirection = new Vec3d(k * l, -m, h * l).normalize();

            player.setVelocity(dashDirection.multiply(DASH_SPEED));
            player.velocityModified = true;

            spawnDashParticles(player, false);

            nbt.putInt(DASH_TIME_KEY, dashTime - 1);
        } else {
            nbt.remove(DASH_TIME_KEY);
            nbt.remove(DASH_YAW_KEY);
            nbt.remove(DASH_PITCH_KEY);
        }
    }

    private static void spawnDashParticles(PlayerEntity player, boolean isBurst) {
        if (!(player.world instanceof ServerWorld serverWorld)) return;

        Vec3d pos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F);
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        if (isBurst) {
            int particles = 400;
            float coneAngle = 30f;
            float speed = 0.8f;

            for (int i = 0; i < particles; i++) {
                float angleYaw = yaw + (player.getRandom().nextFloat() - 0.5f) * coneAngle;
                float anglePitch = pitch + (player.getRandom().nextFloat() - 0.5f) * coneAngle;

                float f = -angleYaw * ((float)Math.PI / 180);
                float g = anglePitch * ((float)Math.PI / 180);
                float h = MathHelper.cos(f);
                float k = MathHelper.sin(f);
                float l = MathHelper.cos(g);
                float m = MathHelper.sin(g);
                Vec3d particleDir = new Vec3d(k * l, -m, h * l).normalize();

                serverWorld.spawnParticles(ParticleTypes.GLOW,
                        pos.x, pos.y + player.getHeight()/2, pos.z,
                        1,
                        particleDir.x * speed,
                        particleDir.y * speed,
                        particleDir.z * speed,
                        0.1);
            }
        } else {
            int swirls = 30;
            float radius = 1.5f;
            float height = player.getHeight();
            float progress = (DASH_DURATION - player.getStackInHand(Hand.MAIN_HAND).getOrCreateNbt().getInt(DASH_TIME_KEY)) / 5f;

            for (int i = 0; i < swirls; i++) {
                float angle = progress * 2 * (float)Math.PI + (i * 2 * (float)Math.PI / swirls);
                float xOffset = MathHelper.cos(angle) * radius;
                float zOffset = MathHelper.sin(angle) * radius;

                serverWorld.spawnParticles(ParticleTypes.CLOUD,
                        pos.x + xOffset, pos.y + height * 0.2f, pos.z + zOffset,
                        3,
                        lookVec.x * 0.1,
                        lookVec.y * 0.1,
                        lookVec.z * 0.1,
                        0.05);

                serverWorld.spawnParticles(ParticleTypes.GLOW,
                        pos.x + xOffset * 0.7f, pos.y + height * 0.5f, pos.z + zOffset * 0.7f,
                        3,
                        lookVec.x * 0.1,
                        lookVec.y * 0.1,
                        lookVec.z * 0.1,
                        0.05);

            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Dashes forward for 1.5s"));
        tooltip.add(Text.literal("an infinite firework ?").formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));

        super.appendTooltip(stack, world, tooltip, context);
    }
}