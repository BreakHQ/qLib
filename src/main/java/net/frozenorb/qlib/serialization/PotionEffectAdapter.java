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
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAdapter
implements JsonDeserializer<PotionEffect>,
JsonSerializer<PotionEffect> {
    public JsonElement serialize(PotionEffect src, Type typeOfSrc, JsonSerializationContext context) {
        return PotionEffectAdapter.toJson(src);
    }

    public PotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return PotionEffectAdapter.fromJson(json);
    }

    public static JsonObject toJson(PotionEffect potionEffect) {
        if (potionEffect == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", (Number)potionEffect.getType().getId());
        jsonObject.addProperty("duration", (Number)potionEffect.getDuration());
        jsonObject.addProperty("amplifier", (Number)potionEffect.getAmplifier());
        jsonObject.addProperty("ambient", Boolean.valueOf(potionEffect.isAmbient()));
        return jsonObject;
    }

    public static PotionEffect fromJson(JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        PotionEffectType effectType = PotionEffectType.getById((int)jsonObject.get("id").getAsInt());
        int duration = jsonObject.get("duration").getAsInt();
        int amplifier = jsonObject.get("amplifier").getAsInt();
        boolean ambient = jsonObject.get("ambient").getAsBoolean();
        return new PotionEffect(effectType, duration, amplifier, ambient);
    }
}

