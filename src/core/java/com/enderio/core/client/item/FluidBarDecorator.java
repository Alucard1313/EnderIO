package com.enderio.core.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    @Override
    public boolean render(PoseStack poseStack, Font font, ItemStack stack, int xOffset, int yOffset) {
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            if (handler.getFluidInTank(0).getAmount() <= 0)
                return;

            float fillRatio = 1.0F - (float) handler.getFluidInTank(0).getAmount() / (float) handler.getTankCapacity(0);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(handler.getFluidInTank(0).getFluid());

            ItemBarRenderer.renderBar(poseStack, fillRatio, xOffset, yOffset, 0, props.getTintColor());
        });
        return false;
    }
}
