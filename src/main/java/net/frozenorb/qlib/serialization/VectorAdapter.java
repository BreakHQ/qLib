/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonElement
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer
 *  org.bukkit.util.Vector
 */
package net.frozenorb.qlib.serialization;

import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.util.Vector;

public class VectorAdapter
implements JsonDeserializer<Vector>,
JsonSerializer<Vector> {
    public Vector deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return VectorAdapter.fromJson(src);
    }

    public JsonElement serialize(Vector src, Type type, JsonSerializationContext context) {
        return VectorAdapter.toJson(src);
    }

    public static JsonObject toJson(Vector src) {
        if (src == null) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("x", (Number)src.getX());
        object.addProperty("y", (Number)src.getY());
        object.addProperty("z", (Number)src.getZ());
        return object;
    }

    public static Vector fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        return new Vector(x, y, z);
    }
}

