package net.homak.sillydivinity.common.custom.item.artifact;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CombustionCharmArtifact extends Item {
    public CombustionCharmArtifact(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        tooltip.add(Text.literal("You combust on death, does not stack"));
        tooltip.add(Text.literal("a small yet powerful piece of tnt").formatted(Formatting.DARK_GRAY)
                .formatted(Formatting.ITALIC));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
