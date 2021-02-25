package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent.ThimbleSubType;

public class ThimbleBlockUpdateEventSerializer implements JsonSerializer<ThimbleBlockUpdateEvent>, JsonDeserializer<ThimbleBlockUpdateEvent> {

    final Type objectType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public JsonElement serialize(ThimbleBlockUpdateEvent event, Type typeOfSrc, JsonSerializationContext context) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("causingEntity", event.getCausingEntity());
        map.put("time", event.getTime());
        map.put("type", event.getType());

        map.put("subType", event.getSubType());
        map.put("preState", event.getPreState().toString());
        map.put("postState", event.getPostState().toString());

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
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

        BlockPos pos = new BlockPos(object.get("position").getAsJsonObject().get("x").getAsInt(),
                object.get("position").getAsJsonObject().get("y").getAsInt(),
                object.get("position").getAsJsonObject().get("z").getAsInt());

        String dimensionName = object.get("dimension").getAsString();


        ThimbleBlockUpdateEvent newEvent = new ThimbleBlockUpdateEvent(uuid, pos, dimensionName, time, subType);

//        newEvent.setPostState();

        BlockState tst = getBlockStateFromString(object.get("postState").toString());

        newEvent.setPostState(getBlockStateFromString(object.get("postState").toString()));
        newEvent.setPreState(getBlockStateFromString(object.get("preState").toString()));
        return newEvent;
    }

    private static BlockState getBlockStateFromString(String stateString) {

        int IDstart = stateString.indexOf('{') + 1;
        int IDend = stateString.indexOf('}');
        Block block = Registry.BLOCK.get(Identifier.tryParse(stateString.substring(IDstart, IDend)));

        int stateStart = stateString.indexOf('[') + 1;
        int stateEnd = stateString.indexOf(']');


        return block.getDefaultState();
    }
}
