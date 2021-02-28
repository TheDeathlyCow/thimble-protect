package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent.ThimbleSubType;

public class ThimbleBlockUpdateEventSerializer implements JsonSerializer<ThimbleBlockUpdateEvent>, JsonDeserializer<ThimbleBlockUpdateEvent> {

    final Type objectType = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static BlockState getBlockStateFromString(String stateString) {

        int IDstart = stateString.indexOf('{') + 1;
        int IDend = stateString.indexOf('}');
        Block block = Registry.BLOCK.get(Identifier.tryParse(stateString.substring(IDstart, IDend)));

        int stateStart = stateString.indexOf('[') + 1;
        int stateEnd = stateString.indexOf(']');


        return block.getDefaultState();
    }

    private static Map<String, String> getMapFromBlockState(BlockState state) {
        String stateString = state.toString();
        int startIndex = stateString.indexOf('[');
        int stopIndex = stateString.indexOf(']');

        if (startIndex == -1) {
            return new LinkedHashMap<>();
        }

        String[] properties = stateString.substring(startIndex + 1, stopIndex).split(",");

        Map<String, String> stateMap = new LinkedHashMap<>();

        for (String property : properties) {
            String[] propertyArray = property.split("=");
            stateMap.put(propertyArray[0], propertyArray[1]);
        }

        return stateMap;
    }

    @Override
    public JsonElement serialize(ThimbleBlockUpdateEvent event, Type typeOfSrc, JsonSerializationContext context) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("causingEntity", event.getCausingEntity());
        map.put("time", event.getTime());
        map.put("type", event.getType());

        map.put("subType", event.getSubType());

        Map<String, Object> preStateMap = new LinkedHashMap<>();
        preStateMap.put("block", Registry.BLOCK.getId(event.getPreState().getBlock()).toString());
        preStateMap.put("properties", getMapFromBlockState(event.getPreState()));

        Map<String, Object> postStateMap = new LinkedHashMap<>();
        postStateMap.put("block", Registry.BLOCK.getId(event.getPostState().getBlock()).toString());
        postStateMap.put("properties", getMapFromBlockState(event.getPostState()));

        map.put("preState", preStateMap);
        map.put("postState", postStateMap);

        map.put("dimension", event.getDimension());

        map.put("position", event.getPos());

        return context.serialize(map, objectType);
    }

    @Override
    public ThimbleBlockUpdateEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String uuid = object.get("causingEntity").getAsString();
        long time = object.get("time").getAsLong();
        ThimbleSubType subType = ThimbleSubType.BLOCK_BREAK;
        try {
            subType = ThimbleSubType.valueOf(object.get("subType").getAsString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        BlockPos pos = new BlockPos(object.get("position").getAsJsonObject().get("x").getAsInt(),
                object.get("position").getAsJsonObject().get("y").getAsInt(),
                object.get("position").getAsJsonObject().get("z").getAsInt());

        String dimensionName = object.get("dimension").getAsString();

        ThimbleBlockUpdateEvent newEvent = new ThimbleBlockUpdateEvent(uuid, pos, dimensionName, time, subType);

        BlockStatePropertyLootCondition.Serializer serializer = new BlockStatePropertyLootCondition.Serializer();

//        BlockState tst = getBlockStateFromString(object.get("postState").toString());
        new BlockStatePropertyLootCondition.Serializer().fromJson(object.get("postState").getAsJsonObject(), context);

        Block postBlock = Registry.BLOCK.get(new Identifier(object.get("postState").getAsJsonObject().get("block").getAsString()));
        StatePredicate postBlockState = StatePredicate.fromJson(object.get("postState").getAsJsonObject().get("properties"));

//        newEvent.setPostState( );
        newEvent.setPreState(getBlockStateFromString(object.get("preState").toString()));
        return newEvent;
    }
}
