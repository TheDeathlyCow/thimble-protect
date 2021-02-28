package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent.ThimbleSubType;

public class ThimbleBlockUpdateEventSerializer implements JsonSerializer<ThimbleBlockUpdateEvent>, JsonDeserializer<ThimbleBlockUpdateEvent> {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ThimbleBlockUpdateEvent.class, new ThimbleBlockUpdateEventSerializer())
            .disableHtmlEscaping()
            .create();
    final Type objectType = new TypeToken<Map<String, Object>>() {
    }.getType();

     // * SERIALIZER AND DESERIALIZER * //

    @Override
    public JsonElement serialize(ThimbleBlockUpdateEvent event, Type typeOfSrc, JsonSerializationContext context) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("causingEntity", event.getCausingEntity());
        map.put("time", event.getTime());

        map.put("type", event.getType());
        map.put("subType", event.getSubType());

        Map<String, Object> preStateMap = new LinkedHashMap<>();
        preStateMap.put("block", Registry.BLOCK.getId(event.getPreState().getBlock()).toString());
        preStateMap.put("properties", getPropertiesMap(event.getPreState()));

        Map<String, Object> postStateMap = new LinkedHashMap<>();
        postStateMap.put("block", Registry.BLOCK.getId(event.getPostState().getBlock()).toString());
        postStateMap.put("properties", getPropertiesMap(event.getPostState()));

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

        newEvent.setPreState(getStateFromJson(object.get("preState").getAsJsonObject()));
        newEvent.setPostState(getStateFromJson(object.get("postState").getAsJsonObject()));

        return newEvent;
    }

    // * HELPER METHODS * //

    private static Map<String, String> getPropertiesMap(BlockState state) {
        Map<String, String> stateMap = new LinkedHashMap<>();

        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
            Pair<String, String> value = Pair.of(entry.getKey().getName(), Util.getValueAsString(entry.getKey(), entry.getValue()));
            stateMap.put(value.getLeft(), value.getRight());
        }

        return stateMap;
    }

    /**
     * A mini blockState deserializer. Accepts a JSON object parameter
     * and returns a block state with the correct ID and properties.
     * <p>
     * Based on a deserializer written by kegare for Forge 1.15,
     * and adapted to work with Fabric 1.16.
     *
     * @author kegare, TheDeathlyCow
     * @param stateObject A JSON object of a block state. must have elements
     *                    "block" which is a string and "properties" which is
     *                    another JSON object containing valid properties
     *                    for that block.
     * @return BlockState
     */
    private BlockState getStateFromJson(JsonObject stateObject) {

        String blockID = stateObject.get("block").getAsString();
        JsonObject propertiesObject = stateObject.get("properties").getAsJsonObject();
        BlockState state = Registry.BLOCK.get(new Identifier(blockID)).getDefaultState();

        if (state == null || state == Blocks.AIR.getDefaultState()) {
            return state;
        }

        StateManager<Block, BlockState> manager = state.getBlock().getStateManager();

        for (Map.Entry<String, JsonElement> entry : propertiesObject.entrySet()) {
            String key = entry.getKey();
            Property<?> property = manager.getProperty(key);

            if (property != null) {
                String value = entry.getValue().getAsString();
                state = modifyState(state, property, value);
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState modifyState(BlockState state, Property<T> property, String name) {
        return property.parse(name).map((value) -> state.with(property, value)).orElse(state);
    }

}
