package io.github.nucleuspowered.terrafirma.generator;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.terrafirma.config.Layer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;
import java.util.Map;

@NonnullByDefault
public class TerrafirmaModifier implements WorldGeneratorModifier {

    private final String id;
    private final String name;
    private final BiomeType biomeType;
    private final BiomeGenerator biomeGenerator;
    private final GenerationPopulator generationPopulator;
    private final Map<Integer, BlockState> layers;

    private static Map<Integer, BlockState> createMapping(List<Layer> layers) {
        ImmutableMap.Builder<Integer, BlockState> builder = ImmutableMap.builder();
        int y = 0;
        for (Layer layer : layers) {
            for (int x = 1; x <= layer.getLayers(); ++x) {
                builder.put(y, layer.getBlockType());
                ++y;
            }
        }

        return builder.build();
    }

    public TerrafirmaModifier(String id, BiomeType biomeType, List<Layer> layers) {
        this.id = "terrafirma:" + id.toLowerCase();
        this.name = "Terrafirma - " + id;
        this.biomeType = biomeType;
        this.layers = createMapping(layers);
        this.biomeGenerator = buffer -> buffer.getBiomeWorker().fill((x, y, z) -> this.biomeType);
        this.generationPopulator = (world, buffer, biomes) ->
                buffer.getBlockWorker().fill((x, y, z) -> this.layers.getOrDefault(y, BlockTypes.AIR.getDefaultState()));
    }

    @Override
    public void modifyWorldGenerator(WorldProperties worldProperties, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.getGenerationPopulators().clear();
        worldGenerator.getPopulators().clear();

        worldGenerator.setBiomeGenerator(this.biomeGenerator);
        worldGenerator.setBaseGenerationPopulator(this.generationPopulator);
    }

    @Override public String getId() {
        return this.id;
    }

    @Override public String getName() {
        return this.name;
    }

}
