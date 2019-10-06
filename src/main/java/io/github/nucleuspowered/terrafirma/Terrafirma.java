package io.github.nucleuspowered.terrafirma;

import static io.github.nucleuspowered.terrafirma.config.TerrafirmaConfig.VERSION_KEY;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.nucleuspowered.terrafirma.command.DumpBlockStatesCommand;
import io.github.nucleuspowered.terrafirma.config.BlockStateTranslator;
import io.github.nucleuspowered.terrafirma.config.Layer;
import io.github.nucleuspowered.terrafirma.config.LayerTranslator;
import io.github.nucleuspowered.terrafirma.config.TerrafirmaConfig;
import io.github.nucleuspowered.terrafirma.config.TerrafirmaGenerator;
import io.github.nucleuspowered.terrafirma.generator.TerrafirmaModifier;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import ninja.leaping.configurate.transformation.ConfigurationTransformation;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
@Plugin(
        id = "terrafirma",
        name = "Terrafirma",
        version = "1.0.0",
        description = "Generate flat worlds!"
)
public class Terrafirma {

    private final Logger logger;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final Path configPath;
    private final Path dir;

    @Inject
    public Terrafirma(Logger logger,
            @ConfigDir(sharedRoot = false) Path dir,
            @DefaultConfig(sharedRoot = false) Path path) {
        TypeSerializerCollection typeSerializers = TypeSerializers.newCollection();
        typeSerializers.registerType(LayerTranslator.TYPE_TOKEN, LayerTranslator.INSTANCE);
        typeSerializers.registerType(BlockStateTranslator.BLOCK_STATE_TYPE_TOKEN, BlockStateTranslator.INSTANCE);
        this.logger = logger;
        this.loader = HoconConfigurationLoader.builder()
                .setPath(path)
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(typeSerializers))
                .build();
        this.configPath = path;
        this.dir = dir;
    }

    @Listener
    public void onServerStart(GamePreInitializationEvent event) {
        this.logger.info("Starting Nucleus Terrafirma");
        Sponge.getCommandManager()
                .register(
                        this,
                        CommandSpec.builder()
                            .executor(new DumpBlockStatesCommand(this.dir))
                            .permission("terrafirma.dumpblocks")
                            .build(),
                        "genblocksfile"
                );
        Sponge.getServiceManager()
                .provideUnchecked(PermissionService.class)
                .newDescriptionBuilder(this)
                .assign(PermissionDescription.ROLE_ADMIN, true)
                .id("terrafirma.dumpblocks")
                .description(Text.of("Allows a user to run /genblocksfile"))
                .register();
    }

    @Listener
    public void onGameRegisterEvent(GameRegistryEvent.Register<WorldGeneratorModifier> event) {
        try {
            TerrafirmaConfig defaultConfig = new TerrafirmaConfig();
            if (Files.notExists(this.configPath)) {
                defaultConfig.getLayers().put(
                        "example", new TerrafirmaGenerator()
                                .setLayers(
                                        Lists.newArrayList(
                                                new Layer(2, BlockTypes.BEDROCK.getDefaultState()),
                                                new Layer(1, BlockTypes.OBSIDIAN.getDefaultState()),
                                                new Layer(3, BlockTypes.STONE.getDefaultState()),
                                                new Layer(3, BlockTypes.DIRT.getDefaultState()),
                                                new Layer(1, BlockTypes.GRASS.getDefaultState())
                                        )
                                )
                );
            }

            CommentedConfigurationNode defaultNode = this.loader.createEmptyNode();
            defaultNode.setValue(TypeToken.of(TerrafirmaConfig.class), defaultConfig);

            // Load the config.
            CommentedConfigurationNode terrafirmaNode = this.loader.load();
            transform(terrafirmaNode);
            terrafirmaNode.mergeValuesFrom(defaultNode);
            this.loader.save(terrafirmaNode);
            TerrafirmaConfig config = terrafirmaNode.getValue(TypeToken.of(TerrafirmaConfig.class), (Supplier<TerrafirmaConfig>) TerrafirmaConfig::new);

            // Create the generators.
            for (Map.Entry<String, TerrafirmaGenerator> entry : config.getLayers().entrySet()) {
                TerrafirmaModifier modifier = new TerrafirmaModifier(
                        entry.getKey().toLowerCase(),
                        entry.getValue().getBiomeType(),
                        entry.getValue().getLayers()
                );
                this.logger.info("Registering generator {}", entry.getKey());
                event.register(modifier);
            }

            this.logger.info("Generators registered");
        } catch (IOException | ObjectMappingException e) {
            this.logger.error("COULD NOT LOAD CONFIG. Will not continue to load Terrafirma.", e);
        }
    }

    private void transform(CommentedConfigurationNode commentedConfigurationNode) {
        ConfigurationTransformation transformation =
                ConfigurationTransformation.versionedBuilder()
                        .setVersionKey(VERSION_KEY)
                        .addVersion(1, ConfigurationTransformation.builder().build())
                        .build();
        transformation.apply(commentedConfigurationNode);
    }

}
