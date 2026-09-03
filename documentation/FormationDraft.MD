This is just a draft. will be formatted later

Plan, first do creating your own formation type. then how to create formations
using the type created as an example.

also include an overview of how the system works under the hood



At its core Formations are built from 3 main things, a Formation, a formation instance and Formation runtime data

The formation is what is created by the datapack, and contains all the logic+configuration details about the formation runtime data is any data you want stored while a formation is not destroyed, and formation instance is a wrapper
around formation runtime data

if you read my files you will notice my implementations has some extra things, namely FormationActivationRecipe and FormationStateHandler,
I will get into those later.

The formation controls the creation of both runtime data and runtime instance. it also requires each as a generic,
so for most methods it is strongly typed, the only exception being loadFormationInstance(Bytebuf), this is used to create the client
version of the formation instance and does not use your generic type, for this reason you can have completely different client
instances.

each tick the formation instance is run, if it returns true it is considered dirty and synced, if false it is not dirty.
when a dirty formation instance is synced, if it does not already exist loadFormationInstance is called. otherwise decode is directly called
on the instance.

the formation instance also has an isValid check. this is NOT checking if it is active, but if it should be destroyed.

I will now go through some examples of setting them up. I will start with making your own formation type
if you only want to understand how the datapack works go [Here](#datapack) just understand other mods might now do it the exact same

## Creating your own 
First I will introduce some extra classes I will be using for this example
### FormationActivationRecipe
as its name suggests this is a class that handles if the formation should be created or not. it just takes in
a list of Nodes, with a node being a block pos relative to formation centre and a list of formation types.
### FormationStateHandler
this handles the state of our formation instance, and determines if it is valid/active or deactivated
### SimpleFormationStateHandler
this is the implementation we will use, it takes in a list of Nodes it should listen to and a control node. even if the control
node is destroyed the formation won't be destroyed, but will be deactivated
### TickableFormation
a formation that can receive tick,activated,destroyed,deactivate
### StateHandledFormationInstance
a wrapper around TickableFormations that use a FormationStateHandler
### TickableFormationClientInstance
a wrapper around TickableFormations that is run on the client

ok now lets get onto the formation itself, this will be a Tick Counter formation.
it will count how many ticks it has been active for and save them when deactivated

so the runtime data will look like
```java
public class TickCounterRuntimeData implements FormationRuntimeData {
    int ticks = 0;

    public void read(ValueInput input){
        ticks = input.getIntOr("ticks",0);
    }
    public void write(ValueOutput output){
        output.putInt("ticks",ticks);
    }
    public void decode(ByteBuf buf){this.ticks = buf.readInt();}
    public void encode(ByteBuf buf){buf.writeInt(ticks);}
}
```
and now for the formation
```java

public record DebugTestFormation(FormationActivationRecipe recipe,Optional<BlockPos> controlNode) implements StateHandledFormation<TickCounterRuntimeData, SimpleFormationStateHandler> {

    @Override
    public void activate(TickCounterRuntimeData runtimeData,  Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION ACTIVATED");
    }

    @Override
    public void deactivate(TickCounterRuntimeData runtimeData, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DEACTIVATED");
    }

    @Override
    public void destroy(TickCounterRuntimeData runtimeData,  Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DESTROYED");
    }

    @Override
    public boolean tick(TickCounterRuntimeData runtimeData, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION TICKED ON ({}) ({})",(level.isClientSide() ? "Client" :"Server"), runtimeData.ticks);
        runtimeData.ticks ++;

        return false;
    }

    @Override
    public FormationType getType() {
        return TestRegistries.DEBUG_FORMATION_TYPE.get();
    }

    @Override
    public Set<FormationNodeType> activationNodes() {
        return recipe.getUniqueTypes();
    }

    @Override
    public boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        return recipe().tryActive(nodeManager,pos,type);
    }

    @Override
    public Set<BlockPos> getRequiredActivationNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        return recipe.getGlobalNodePositions(nodeManager,pos,type);
    }

    @Override
    public StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {

        int controlIndex = -1;
        if(controlNode.isPresent()) controlIndex = recipe.getIndexOf(controlNode.get());
        SimpleFormationStateHandler stateHandler = SimpleFormationStateHandler.of(recipe.getGlobalNodes(nodeManager,pos,type),controlIndex);

        return new StateHandledFormationInstance<>(this,createRuntimeData(),stateHandler);
    }

    @Override
    public StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> loadFormationInstance(ValueInput input,RegistryAccess access) {
        Optional<SimpleFormationStateHandler> optionalStateHandler = input.read("state",SimpleFormationStateHandler.CODEC);
        return optionalStateHandler.map(simpleFormationStateHandler -> new StateHandledFormationInstance<>(this, loadRuntimeData(input.childOrEmpty("data"),access), simpleFormationStateHandler)).orElse(null);

    }

    @Override
    public FormationInstance loadFormationInstance(ByteBuf buf, RegistryAccess access) {
        boolean active = buf.readBoolean();
        TickableFormationClientInstance<TickCounterRuntimeData> instance = new TickableFormationClientInstance<>(this,loadRuntimeData(buf,access));
        instance.setActive(active);
        return instance;
    }

    @Override
    public void writeFormationInstance(ValueOutput output, StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> instance, RegistryAccess access) {
       output.store("state",SimpleFormationStateHandler.CODEC,instance.handler);
       writeRuntimeData(output.child("data"),instance.runtimeData,access);
    }

    @Override
    public void encodeFormationInstance(ByteBuf buf, RegistryAccess access, StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> instance) {
        buf.writeBoolean(instance.wasActive());
        encodeRuntimeData(buf,access,instance.runtimeData);
    }

    @Override
    public TickCounterRuntimeData createRuntimeData() {
        return new TickCounterRuntimeData();
    }

    @Override
    public TickCounterRuntimeData loadRuntimeData(ValueInput input,RegistryAccess access) {
        TickCounterRuntimeData data = createRuntimeData();
        data.read(input);
        return data;
    }

    @Override
    public TickCounterRuntimeData loadRuntimeData(ByteBuf buf, RegistryAccess access) {
        TickCounterRuntimeData data = createRuntimeData();
        data.decode(buf);
        return data;
    }

    @Override
    public void writeRuntimeData(ValueOutput output, TickCounterRuntimeData runtimeData, RegistryAccess access) {
        runtimeData.write(output);
    }

    @Override
    public void encodeRuntimeData(ByteBuf buf, RegistryAccess access, TickCounterRuntimeData runtimeData) {
        runtimeData.encode(buf);
    }
}
```

it might look a bit overwhelming, but most of the methods just handled encoding/decoding and writing/reading
instances and run time data
```java
 activationNodes();
 ``` 
is where our recipe comes in. it returns a unique set of node types, and if one of these types is
seen being placed it will then check if this formation can be made by calling
```java
    tryActivate();
```
when the formation is created we need some way to translate the local coordinates to global ones
and this is when
```java
getRequiredActivationNodes();
```
is called

Ok and now we need to set up a FormationType, this is what will be used to decode our formation 
```java
public class DebugTestFormationType extends FormationType {
    @Override
    public MapCodec<? extends Formation<?, ?>> definitionCodec() {
        return RecordCodecBuilder.<DebugTestFormation>mapCodec(instance->instance.group(
                FormationActivationRecipe.CODEC.fieldOf("activation_nodes").forGetter(DebugTestFormation::recipe),
                BlockPos.CODEC.optionalFieldOf("control_node").forGetter(DebugTestFormation::controlNode)
            ).apply(instance,DebugTestFormation::new)
        );
    }
}
```
all type codecs work as dispatch codecs (more info [here](https://docs.neoforged.net/docs/datastorage/codecs/#dispatch))

in the above codec we use the FormationActivationRecipe built in codec to define how activation nodes should be formatted.
then the Block pos built in codec for an optional control node

once you have this we need to register the type
```java
public class TestRegistries {
    public static final DeferredRegister<FormationType> FORMATION_TYPES =
            DeferredRegister.create(CoreRegistries.FORMATION_TYPES, FormationArrays.MOD_ID);

    public static final DeferredHolder<FormationType,FormationType> DEBUG_FORMATION_TYPE = FORMATION_TYPES.register(
            "debug",
            DebugTestFormationType::new
    );

    public static void register(IEventBus eventBus){

        FORMATION_TYPES.register(eventBus);
    }
}
```
> *Note* remember to call register in your Mod constructor


## Datapack
for this example we will be using `formation_arrays:debug` this does not exist in the actual mod, but is an example.<br>
to learn how to use your mods specific formations read their documentation

for the basics of data packs read [here](https://minecraft.wiki/w/Data_pack)

### File Location
the file will be stored at
`data/<namespace>/formation_arrays/formations/<formation_id>.json`

### The File

every file must include a type
```json
{
  "type": "formation_arrays:debug"
}
```
this tells the game how to parse the file and what formation to make

now debug formation only has 2 extra fields, activation nodes and control node.<br>
activation nodes takes in a list of nodes (relative block pos + list of node types) that are Required for it to created<br>
control node takes in a relative position and ensures that if that node is destroyed, instead of destroying the formation it will deactivate

and that will look something like
```json
{
  "type": "formation_arrays:debug",
  "activation_nodes": {
    "nodes": [
      {
        "pos" : [2,0,0],
        "types": ["formation_arrays:magic_node"]
      },
      {
        "pos" : [0,0,2],
        "types":[ "formation_arrays:diamond_node"]
      },
      {
        "pos" : [0,0,-2],
        "types": ["formation_arrays:gold_node"]
      },
      {
        "pos" : [-2,0,0],
        "types": ["formation_arrays:iron_node"]
      }
    ],
    "can_rotate": false
  },
  "control_node": [-2,0,0]
}
```