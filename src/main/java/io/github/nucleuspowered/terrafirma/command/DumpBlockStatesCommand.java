package io.github.nucleuspowered.terrafirma.command;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DumpBlockStatesCommand implements CommandExecutor {

    private static final Pattern VARIANT_PATTERN = Pattern.compile("variant=([a-z0-9_]+)");
    private static final Pattern COLOUR_PATTERN = Pattern.compile("color=([a-z0-9_]+)");
    private final Path configDirectory;

    public DumpBlockStatesCommand(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    @Override
    @NonNull
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {
        Path file = this.configDirectory.resolve("block_types.txt");
        if (Files.exists(file)) {
            throw new CommandException(Text.of(TextColors.RED, file.toString(), " already exists. Delete this file to generate a new file."));
        }

        Collection<BlockState> states = Sponge.getRegistry().getAllOf(BlockState.class);
        Map<String, String> typeMap = new HashMap<>();
        for (BlockState state : states) {
            // get the type.
            BlockType type = state.getType();

            String id = state.getId();
            Matcher variantMatcher = VARIANT_PATTERN.matcher(id);
            Matcher colourMatcher = COLOUR_PATTERN.matcher(id);
            String name;
            String i = state.getType().getId();
            StringBuilder extra = new StringBuilder();
            if (variantMatcher.find()) {
                extra.append("variant=").append(variantMatcher.group(1));
            }

            if (colourMatcher.find()) {
                if (extra.length() > 0) {
                    extra.append(",");
                }

                extra.append("color=").append(colourMatcher.group(1));
            }

            if (extra.length() > 0) {
                i += "[" + extra.toString() + "]";
            }

            if (typeMap.containsKey(i)) {
                continue;
            }

            name = state.getName();
            if (name.isEmpty() || name.startsWith(state.getType().getId())) {
                name = state.getType().getTranslation().get();
            }

            typeMap.put(i, name);
        }

        List<String> s = typeMap
                .entrySet()
                .stream()
                .map(entry -> entry.getValue() + " = " + entry.getKey())
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());


        try (PrintWriter stream = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)))) {
            stream.println("# A list of block types to their IDs.");
            stream.println("# Block name = block id.");
            stream.println("# ---------------------------------- #");
            s.forEach(stream::println);
            stream.println("# ---------------------------------- #");

            String mName = Sponge.getPlatform().getContainer(Platform.Component.GAME).getName();
            String mVersion = Sponge.getPlatform().getContainer(Platform.Component.GAME).getVersion().orElse("unknown");
            String aVersion = Sponge.getPlatform().getContainer(Platform.Component.API).getVersion().orElse("unknown");
            String fVersion = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();
            String iVersion = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("unknown");

            stream.println("Generated using:");
            stream.println("- SpongeAPI: " + aVersion);
            stream.println("- " + fVersion + ": " + iVersion);
            stream.println("- " + mName + ": " + mVersion);
        } catch (IOException e) {
            throw new CommandException(Text.of(TextColors.RED, file.toString(), " could not be generated."), e);
        }

        src.sendMessage(Text.of(TextColors.GREEN, file.toString(), " generated."));
        return CommandResult.success();
    }

}
