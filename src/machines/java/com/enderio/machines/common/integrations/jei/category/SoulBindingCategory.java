package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.machines.client.gui.screen.SoulBinderScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import com.enderio.machines.common.souldata.ISoulData;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class SoulBindingCategory extends MachineRecipeCategory<SoulBindingRecipe> {
    public static final RecipeType<SoulBindingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "soul_binding", SoulBindingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SoulBindingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SoulBinderScreen.BG_TEXTURE, 35, 30, 118, 44);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SOUL_BINDER.get()));
    }

    @Override
    public RecipeType<SoulBindingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_SOUL_BINDING;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulBindingRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> vials;
        Optional<IFocus<ItemStack>> output = focuses.getItemStackFocuses(OUTPUT).findFirst();
        Optional<IFocus<ItemStack>> input = focuses.getItemStackFocuses(INPUT).filter(f -> f.getTypedValue().getItemStack().get().is(EIOItems.FILLED_SOUL_VIAL.asItem())).findFirst();

        if (output.isPresent()) {
            var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL);
            output.get().getTypedValue().getItemStack().get().getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(cap -> {
                SoulVialItem.setEntityType(item, cap.getStoredEntityData().getEntityType().get());
            });

            vials = List.of(item);
        } else if (input.isPresent()) {
            vials = List.of(input.get().getTypedValue().getIngredient());
        } else if (recipe.getEntityType() != null) {
            var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL);
            SoulVialItem.setEntityType(item, recipe.getEntityType());

            vials = List.of(item);
        } else if (recipe.getMobCategory() != null) {
            vials = new ArrayList<>();

            var allEntitiesOfCategory = ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(e -> e.getCategory().equals(recipe.getMobCategory()))
                .map(ForgeRegistries.ENTITY_TYPES::getKey)
                .toList();

            for (ResourceLocation entity : allEntitiesOfCategory) {
                var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL);
                SoulVialItem.setEntityType(item, entity);
                vials.add(item);
            }

        } else if (recipe.getSouldata() != null){
            vials = new ArrayList<>();
            SoulDataReloadListener<? extends ISoulData> soulDataReloadListener = SoulDataReloadListener.fromString(recipe.getSouldata());

            var allEntitiesOfSoulData = ForgeRegistries.ENTITY_TYPES.getKeys().stream()
                .filter(r -> soulDataReloadListener.map.containsKey(r))
                .toList();

            for (ResourceLocation entity : allEntitiesOfSoulData) {
                var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL);
                SoulVialItem.setEntityType(item, entity);
                vials.add(item);
            }

        } else {
            vials = SoulVialItem.getAllFilled();
        }

        builder.addSlot(INPUT, 3, 4)
            .addItemStacks(vials);

        builder.addSlot(INPUT, 24, 4)
            .addIngredients(recipe.getInput());

        builder.addSlot(OUTPUT, 77, 4)
            .addItemStack(new ItemStack(EIOItems.EMPTY_SOUL_VIAL));

        var resultStack = RecipeUtil.getResultStacks(recipe).get(0).getItem();
        var results = new ArrayList<ItemStack>();

        // If the output can take an entity type, then we add it
        if (resultStack.getCapability(EIOCapabilities.ENTITY_STORAGE).isPresent()) {
            for (ItemStack vial : vials) {
                SoulVialItem.getEntityData(vial).flatMap(StoredEntityData::getEntityType).ifPresent(entityType -> {
                    var result = resultStack.copy();
                    result.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> {
                        storage.setStoredEntityData(StoredEntityData.of(entityType));
                        results.add(result);
                    });
                });
            }
        }

        // Fallback :(
        if (results.size() == 0) {
            results.add(resultStack);
        }

        builder.addSlot(OUTPUT, 99, 4)
            .addItemStacks(results);
    }

    @Override
    public void draw(SoulBindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        int cost = recipe.getExpCost();
        String costText = cost < 0 ? "err" : Integer.toString(cost);
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        // Show red if the player doesn't have enough levels
        int mainColor = playerHasEnoughLevels(player, cost) ? 0xFF80FF20 : 0xFFFF6060;
        guiGraphics.drawString(minecraft.font, text, 5, 24, mainColor);

        guiGraphics.drawString(Minecraft.getInstance().font, getBasicEnergyString(recipe), 5, 34, 0xff808080, false);
    }

    @Override
    public List<Component> getTooltipStrings(SoulBindingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 5 && mouseY > 34 && mouseX < 5 + mc.font.width(getBasicEnergyString(recipe)) && mouseY < 34 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }
}
