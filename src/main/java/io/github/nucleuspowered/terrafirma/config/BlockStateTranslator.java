package io.github.nucleuspowered.terrafirma.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class BlockStateTranslator implements TypeSerializer<BlockState> {

    public static final TypeToken<BlockState> BLOCK_STATE_TYPE_TOKEN = TypeToken.of(BlockState.class);
    public static final BlockStateTranslator INSTANCE = new BlockStateTranslator();

    private BlockStateTranslator() { }

    @Nullable
    @Override
    public BlockState deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String id = value.getString();
        if (id != null) {
            if (id.matches("^.+\\[.+\\]$")) {
                // Block state.
                if (!id.matches("^.+:.+\\[.+\\]$")) {
                    id = "minecraft:" + id;
                }

                // Try to get the state
                Optional<BlockState> optionalBlockState = Sponge.getRegistry().getType(BlockState.class, id);
                if (optionalBlockState.isPresent()) {
                    return optionalBlockState.get();
                }
            } else {
                if (!id.matches("^.+:.+$")) {
                    id = "minecraft:" + id;
                }

                Optional<BlockState> optionalBlockState = Sponge.getRegistry().getType(BlockType.class, id)
                        .map(BlockType::getDefaultState);
                if (optionalBlockState.isPresent()) {
                    return optionalBlockState.get();
                }
            }

        }

        throw new ObjectMappingException("Could not deserialise " + id + " as a block state");
    }

    @Override public void serialize(@NonNull TypeToken<?> type, @Nullable BlockState obj, @NonNull ConfigurationNode value) {
        if (obj != null) {
            value.setValue(obj.getId());
        }
    }
}
