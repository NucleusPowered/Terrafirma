package io.github.nucleuspowered.terrafirma.config;

import org.spongepowered.api.block.BlockType;

public class Layer {

    private final int layers;
    private final BlockType blockType;

    public Layer(int layers, BlockType blockType) {
        this.layers = layers;
        this.blockType = blockType;
    }

    public int getLayers() {
        return layers;
    }

    public BlockType getBlockType() {
        return blockType;
    }
}
