package net.homak.sillydivinity.mixin;

import net.homak.sillydivinity.SillyDivinity;
import net.homak.sillydivinity.common.registry.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useItemModel(BakedModel value, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.DIVINE_SILLYSWORD) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.GROUND) {
            return ((net.homak.sillydivinity.mixin.ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(SillyDivinity.MOD_ID, "divine_sillysword_3d", "inventory"));
        }
        if (stack.isOf(ModItems.HAMMER_OF_DIVINITY) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.GROUND) {
            return ((net.homak.sillydivinity.mixin.ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(SillyDivinity.MOD_ID, "hammer_of_divinity_3d", "inventory"));
        }
        if (stack.isOf(ModItems.STABBY_STAB) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.GROUND) {
            return ((net.homak.sillydivinity.mixin.ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(SillyDivinity.MOD_ID, "stabby_stab_3d", "inventory"));
        }
        if (stack.isOf(ModItems.POLEAXE) && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.GROUND) {
            return ((net.homak.sillydivinity.mixin.ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(SillyDivinity.MOD_ID, "poleaxe_3d", "inventory"));
        }
        return value;
    }
}