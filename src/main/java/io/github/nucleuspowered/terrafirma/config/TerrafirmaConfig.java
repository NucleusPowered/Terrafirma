package io.github.nucleuspowered.terrafirma.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

@ConfigSerializable
public class TerrafirmaConfig {

    public static final String VERSION_KEY = "version";

    @Setting(value = VERSION_KEY, comment = "Config version number. Do not alter.")
    private int version = 1;

    @Setting(value = "generators",
            comment = "A list of generators. In game, they will be represented by the ID \"terrafirma:<name>\"")
    private Map<String, TerrafirmaGenerator> generators = new HashMap<>();

    @Nonnull
    public Map<String, TerrafirmaGenerator> getLayers() {
        return this.generators;
    }

}
