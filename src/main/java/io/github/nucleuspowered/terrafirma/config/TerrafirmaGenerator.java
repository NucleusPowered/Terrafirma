package io.github.nucleuspowered.terrafirma.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class TerrafirmaGenerator {

    @Setting(value = "biome-type", comment = "The biome for the generator."
            + "\n\nNOTE: Setting this to something other than minecraft:void may cause unintended populator effects, such"
            + "as turning stone to gravel between dirt and stone levels.")
    private BiomeType biomeType = BiomeTypes.VOID;

    @Setting(value = "layers", comment = "Sets the layers in the flat world. In order, from bottom up.")
    private List<Layer> layers = new ArrayList<>();

    public BiomeType getBiomeType() {
        return this.biomeType;
    }

    public List<Layer> getLayers() {
        return this.layers;
    }

    public TerrafirmaGenerator setLayers(List<Layer> layers) {
        this.layers = layers;
        return this;
    }
}
