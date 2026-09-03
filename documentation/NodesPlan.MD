This is just a draft. will be formatted later


Before discussing Formations I must first explain the system that supports it. Nodes.

Every block position in the world is a node, Blocks and entities in that node act as node type providers.
defining what types that node holds.

these types are then used by the formation system to create, destroy or update existing formations.

when a node is unloaded, we store its last known types and treat it as if it still exists, 
This is to prevent partially loaded formations spanning chunks to turn off if a single chunk is unloaded.

> *Note* Since formations themselves control activeness this can be disabled
And that's it... kinda. That is the Core abstract off how the node system functions, and now for specifics


Blocks

to define a blocks node type provider we use [Data Maps](https://docs.neoforged.net/docs/resources/server/datamaps/)
Specifically ```node_type_providers```
the full path is ```data/formation_arrays/data_maps/block/node_type_providers.json```

once you have that and the block you want to define a provider for, you need to decide what "type" you want it to be,
this type also decides how the json is formatted.

For example, Built in we have the `formation_arrays:fixed` and `formation_arrays:stateful` types.

`formation_arrays:fixed` looks like this
```json
{
  "type": "formation_arrays:fixed",
  "types": ["formation_arrays:magic_node"]
}
```
what this does is creates a FixedBlockNodeTypeProvider on that block that always returns a `magic_node` type

for `formation_arrays:stateful` it would look something like
```json
{
  "type": "formation_arrays:stateful",
  "state_types":{
    "0":  [
      "formation_arrays:magic_node"
    ],
    "1": [
      "formation_arrays:iron_node"
    ]
  }
}
```
here we instead map a state to a list of nodes, this state is gotten by using the StatefulBlock Capability to get its current state,
if it does not have that capability it defaults to state 0, and if state 0 is not defined it assumes no types.

for information on creating your own type read [here]() TODO 

