package net.homak.sillydivinity.common.registry;

import net.homak.sillydivinity.SillyDivinity;
import net.homak.sillydivinity.common.custom.item.artifact.*;
import net.homak.sillydivinity.common.custom.item.weapon.DivineSillyswordSwordItem;
import net.homak.sillydivinity.common.custom.item.weapon.HammerOfDivinityItem;
import net.homak.sillydivinity.common.custom.item.weapon.PoleaxeItem;
import net.homak.sillydivinity.common.custom.item.weapon.StabbyStabItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item TUNGSTEN_INGOT = registerItem("tungsten_ingot",
            new Item(new Item.Settings().group(ItemGroup.MATERIALS)));

    // artifacts

    public static final Item COMBUSTION_CHARM = registerItem("combustion_charm",
            new CombustionCharmArtifact(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    public static final Item TANK_RESTORATOR = registerItem("tank_restorator",
            new TankRestoratorArtifact(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    public static final Item DASH_RUNE = registerItem("dash_rune",
            new DashRuneArtifact(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    public static final Item HOVERSTONE = registerItem("hoverstone",
            new HoverStoneArtifact(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    public static final Item FLORIST_CORE = registerItem("florist_core",
            new FloristCoreArtifact(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    public static final Item NULLCORE = registerItem("nullcore",
            new NullCoreItem(new Item.Settings().group(ItemGroup.TOOLS).group(ItemGroup.COMBAT).maxCount(1)));

    // weapons

    public static final Item DIVINE_SILLYSWORD = registerItem("divine_sillysword",
            new DivineSillyswordSwordItem(ToolMaterials.NETHERITE, 7, -3.1f,
                    new Item.Settings().fireproof().group(ItemGroup.COMBAT)));

    public static final Item HAMMER_OF_DIVINITY = registerItem("hammer_of_divinity",
             new HammerOfDivinityItem(ToolMaterials.DIAMOND, 2 , -3.4f,
        new Item.Settings().fireproof().group(ItemGroup.COMBAT)));

    public static final Item STABBY_STAB = registerItem("stabby_stab",
            new StabbyStabItem(ToolMaterials.DIAMOND, 4 , -2.5f,
                    new Item.Settings().fireproof().group(ItemGroup.COMBAT)));

    public static final Item POLEAXE = registerItem("poleaxe",
            new PoleaxeItem(ToolMaterials.DIAMOND, 5 , -2.3f,
                    new Item.Settings().fireproof().group(ItemGroup.COMBAT)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(SillyDivinity.MOD_ID, name), item);
    }

    public static void registerItems() {
        SillyDivinity.LOGGER.info("Silly Divinity loading: 1/2");
    }
}
