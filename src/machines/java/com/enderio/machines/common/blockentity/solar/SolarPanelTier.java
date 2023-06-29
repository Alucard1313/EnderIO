package com.enderio.machines.common.blockentity.solar;

import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.config.common.MachinesCommonConfig;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraftforge.common.ForgeConfigSpec;

public enum SolarPanelTier implements ISolarPanelTier {

    SIMPLE(MachinesConfig.COMMON.SIMPLE_SOLAR_PANEL_MAX_PRODUCTION),
    BASIC(MachinesConfig.COMMON.BASIC_SOLAR_PANEL_MAX_PRODUCTION),
    ADVANCED(MachinesConfig.COMMON.ADVANCED_SOLAR_PANEL_MAX_PRODUCTION),
    VIBRANT(MachinesConfig.COMMON.VIBRANT_SOLAR_PANEL_MAX_PRODUCTION);

    private final ForgeConfigSpec.ConfigValue<Integer> productionRate;

    SolarPanelTier(ForgeConfigSpec.ConfigValue<Integer> productionRate) {
        this.productionRate = productionRate;
    }

    @Override
    public int getProductionRate() {
        return productionRate.get();
    }

    @Override
    public int getStorageCapacity() {
        return getProductionRate() * 1000;
    }
}

