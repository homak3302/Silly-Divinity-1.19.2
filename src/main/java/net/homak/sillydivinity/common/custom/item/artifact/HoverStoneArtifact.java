package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoverStoneArtifact extends Item {
    public HoverStoneArtifact(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Cancels fall damage"));
        tooltip.add(Text.literal("a little pebble that can float... ?").formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
