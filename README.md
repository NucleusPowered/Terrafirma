# Nucleus Terrafirma

A world generator modifier provider for creating flat worlds. This plugin does nothing
without a world management plugin. Of course, we recommend Nucleus, but anything else that
allows you to create a world and specify `WorldGeneratorModifier`s will work too.

## Specifying a flat world.

Terrafirma will create a config for you in the `config/terrafirma/terrafirma.conf` file, with
the `terrafirma:example` modifier. The default will look something like below. 

```hocon
# A list of generators. In game, they will be represented by the ID "terrafirma:<name>"
generators {
    example {
        # The biome for the generator.
        # 
        # NOTE: Setting this to something other than minecraft:void may cause unintended populator effects, such 
        # as turning stone to gravel between dirt and stone levels.
        biome-type="minecraft:void"
        # Sets the layers in the flat world. In order, from bottom up.
        layers=[
            {
                # The block for this layer.
                block="minecraft:bedrock"
                # The number of layers for this block.
                layers=2
            },
            {
                # The block for this layer.
                block="minecraft:obsidian"
                # The number of layers for this block.
                layers=1
            },
            {
                # The block for this layer.
                block="minecraft:stone"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:dirt"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:grass"
                # The number of layers for this block.
                layers=1
            }
        ]
    }
}
# Config version number. Do not alter.
version=1
```

You could add a modifier that creates 1 layer of bedrock, 10 layers of dirt, 3 layers of stone and one layer of gravel to a "test" modifier 
by altering the config file like this:

```hocon
# A list of generators. In game, they will be represented by the ID "terrafirma:<name>"
generators {
    example {
        # The biome for the generator.
        # 
        # NOTE: Setting this to something other than minecraft:void may cause unintended populator effects, such 
        # as turning stone to gravel between dirt and stone levels.
        biome-type="minecraft:void"
        # Sets the layers in the flat world. In order, from bottom up.
        layers=[
            {
                # The block for this layer.
                block="minecraft:bedrock"
                # The number of layers for this block.
                layers=2
            },
            {
                # The block for this layer.
                block="minecraft:obsidian"
                # The number of layers for this block.
                layers=1
            },
            {
                # The block for this layer.
                block="minecraft:stone"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:dirt"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:grass"
                # The number of layers for this block.
                layers=1
            }
        ]
    }
    test {
        # The biome for the generator.
        # 
        # NOTE: Setting this to something other than minecraft:void may cause unintended populator effects, such 
        # as turning stone to gravel between dirt and stone levels.
        biome-type="minecraft:void"
        # Sets the layers in the flat world. In order, from bottom up.
        layers=[
            {
                # The block for this layer.
                block="minecraft:bedrock"
                # The number of layers for this block.
                layers=1
            },
            {
                # The block for this layer.
                block="minecraft:dirt"
                # The number of layers for this block.
                layers=10
            },
            {
                # The block for this layer.
                block="minecraft:stone"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:gravel"
                # The number of layers for this block.
                layers=1
            }
        ]
    }
}
# Config version number. Do not alter.
version=1
```

You might want to add a sandstone double slab in the middle instead, and a layer of dark oak planks on top! The generator might
then look like:

```hocon
    test2 {
        # The biome for the generator.
        # 
        # NOTE: Setting this to something other than minecraft:void may cause unintended populator effects, suchas turning stone to gravel between dirt and stone levels.
        biome-type="minecraft:void"
        # Sets the layers in the flat world. In order, from bottom up.
        layers=[
            {
                # The block for this layer.
                block="minecraft:bedrock"
                # The number of layers for this block.
                layers=2
            },
            {
                # The block for this layer.
                block="minecraft:obsidian"
                # The number of layers for this block.
                layers=1
            },
            {
                # The block for this layer.
                block="minecraft:double_stone_slab[variant=sandstone]"
                # The number of layers for this block.
                layers=5
            },
            {
                # The block for this layer.
                block="minecraft:dirt"
                # The number of layers for this block.
                layers=3
            },
            {
                # The block for this layer.
                block="minecraft:grass"
                # The number of layers for this block.
                layers=1
            },
            # minecraft:double_wooden_slab[variant=dark_oak]
            {
                # The block for this layer.
                block="minecraft:double_wooden_slab[variant=dark_oak]"
                # The number of layers for this block.
                layers=1
            }
        ]
    }
```

Available block IDs can be discovered using the `/genblocksfile` command, which will print the block IDs to a file in the Terrafirma
config directory (requiring the `terrafirma.dumpblocks` permission). The block IDs accept both standard IDs and block states, particularly
with the variant state. When 1.14.x comes around, block states won't be necessary.

The server then needs to be restarted, which will then allow you to create a world with the name "testworld" using the following Nucleus 
command (other world management plugins will vary)

```
/world create -m terrafirma:test testworld
``` 

Note that the biome is of the "minecraft:void" type. If you set it to a different biome type, there _may_ be other side effects that
you may not be expecting.

Blocks currently require the `minecraft:` prefix.