package net.blay09.mods.cookingbook.addon;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.blay09.mods.cookingbook.api.CookingAPI;
import net.blay09.mods.cookingbook.api.event.FoodRegistryInitEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class HarvestCraftAddon {

    private static final String[] ADDITIONAL_RECIPES = new String[] {
            "flourItem",
            "doughItem",
            "cornmealItem",
            "freshwaterItem",
            "pastaItem",
            "vanillaItem",
            "butterItem",
            "heavycreamItem",
            "saltItem",
            "freshmilkItem",
            "mayoItem",
            "cocoapowderItem",
            "ketchupItem",
            "vinegarItem",
            "mustardItem",
            "blackpepperItem",
            "groundcinnamonItem",
            "groundnutmegItem",
            "saladdressingItem",
            "batterItem",
            "oliveoilItem"
    };

    private static final String[] OVEN_RECIPES = new String[] {
            "turkeyrawItem", "turkeycookedItem",
            "rabbitrawItem", "rabbitcookedItem",
            "venisonrawItem", "venisoncookedItem"
    };

    private static final String[] TOOLS = new String[] {
            "cuttingboardItem",
            "potItem",
            "skilletItem",
            "saucepanItem",
            "bakewareItem",
            "mortarandpestleItem",
            "mixingbowlItem",
            "juicerItem"
    };

    public HarvestCraftAddon() {
        CookingAPI.addOvenFuel(GameRegistry.findItemStack("harvestcraft", "oliveoilItem", 1), 1600);

        for(int i = 0; i < OVEN_RECIPES.length; i += 2) {
            ItemStack source = GameRegistry.findItemStack("harvestcraft", OVEN_RECIPES[i], 1);
            ItemStack result = GameRegistry.findItemStack("harvestcraft", OVEN_RECIPES[i + 1], 1);
            if(source != null && result != null) {
                CookingAPI.addOvenRecipe(source, result);
            }
        }

        for(String toolName : TOOLS) {
            ItemStack toolItem = GameRegistry.findItemStack("harvestcraft", toolName, 1);
            if(toolItem != null) {
                CookingAPI.addToolItem(toolItem);
            }
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onFoodRegistryInit(FoodRegistryInitEvent event) {
        event.registerNonFoodRecipe(new ItemStack(Items.cake));
        event.registerNonFoodRecipe(new ItemStack(Items.sugar));
        for(String s : ADDITIONAL_RECIPES) {
            ItemStack itemStack = GameRegistry.findItemStack("harvestcraft", s, 1);
            if(itemStack != null) {
                event.registerNonFoodRecipe(itemStack);
            }
        }
    }

    public static boolean isWeirdBrokenRecipe(IRecipe recipe) {
        if(recipe.getRecipeSize() == 2 && recipe instanceof ShapelessOreRecipe) {
            ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) recipe;
            Object first = oreRecipe.getInput().get(0);
            Object second = oreRecipe.getInput().get(1);
            ItemStack firstItem = null;
            ItemStack secondItem = null;
            if (first instanceof ItemStack) {
                firstItem = (ItemStack) first;
            } else if (first instanceof ArrayList) {
                List list = (List) first;
                if (list.size() == 1) {
                    firstItem = (ItemStack) list.get(0);
                }
            }
            if (second instanceof ItemStack) {
                secondItem = (ItemStack) second;
            } else if (second instanceof ArrayList) {
                List list = (List) second;
                if (list.size() == 1) {
                    secondItem = (ItemStack) list.get(0);
                }
            }
            if (firstItem != null && secondItem != null && ItemStack.areItemStacksEqual(firstItem, secondItem) && oreRecipe.getRecipeOutput().isItemEqual(firstItem)) {
                return true;
            }
        }
        return false;
    }

}