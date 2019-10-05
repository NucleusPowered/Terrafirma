package io.github.nucleuspowered.terrafirma.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.block.BlockType;

@SuppressWarnings("UnstableApiUsage")
public class LayerTranslator implements TypeSerializer<Layer> {

    public static final TypeToken<Layer> TYPE_TOKEN = TypeToken.of(Layer.class);
    public static final LayerTranslator INSTANCE = new LayerTranslator();

    private static final String LAYERS_KEY = "layers";
    private static final String BLOCK_TYPE_KEY = "block";

    private static final TypeToken<BlockType> BLOCK_TYPE_TYPE_TOKEN = TypeToken.of(BlockType.class);

    private LayerTranslator() {}

    @Nullable
    @Override
    public Layer deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        return new Layer(
                value.getNode(LAYERS_KEY).getInt(),
                value.getNode(BLOCK_TYPE_KEY).getValue(BLOCK_TYPE_TYPE_TOKEN)
        );
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Layer obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj != null) {
            setComment(value.getNode(LAYERS_KEY), "The number of layers for this block.")
                    .setValue(obj.getLayers());
            setComment(value.getNode(BLOCK_TYPE_KEY), "The block for this layer.")
                    .setValue(BLOCK_TYPE_TYPE_TOKEN, obj.getBlockType());
        }
    }

    private ConfigurationNode setComment(ConfigurationNode node, String comment) {
        if (node instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) node).setComment(comment);
        }
        return node;
    }

}
