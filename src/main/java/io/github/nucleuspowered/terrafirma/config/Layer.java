package io.github.nucleuspowered.terrafirma.config;

import org.spongepowered.api.block.BlockState;

public class Layer {

    private final int layers;
    private final BlockState blockType;

    public Layer(int layers, BlockState blockType) {
        this.layers = layers;
        this.blockType = blockType;
    }

    public int getLayers() {
        return layers;
    }

    public BlockState getBlockType() {
        return blockType;
    }
}
