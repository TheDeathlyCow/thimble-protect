package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.math.BlockPos;

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
        ThimbleSubType subType = null;
        try {
            subType = ThimbleSubType.valueOf(object.get("subType").getAsString());
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

        return new ThimbleBlockUpdateEvent(uuid, null, null, time, subType);
    }
}
