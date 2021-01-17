package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThimbleBlockUpdateEventSerializer implements JsonSerializer<ThimbleBlockUpdateEvent> {

    final Type objectType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public JsonElement serialize(ThimbleBlockUpdateEvent event, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("causingEntity", event.getCausingEntity().getUuidAsString());
        map.put("time", event.getTime());
        map.put("type", event.getType());

        map.put("subType", event.getSubType());
        map.put("preState", event.getPreState().toString());
        map.put("postState", event.getPostState().toString());

        return context.serialize(map, objectType);
    }
}
