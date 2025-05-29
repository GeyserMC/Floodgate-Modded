package dev.barenton.alias;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class AliasManager {
    // Eventually we have to use Alias as a dependency or something...
    // We're fine for now.
    private static final Map<String, GameProfile> profileRedirects = new HashMap<>();
    private static final Path PROFILE_PATH = Paths.get("config", "alias_profiles.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Resolves a username to a fully remapped GameProfile (UUID + name) */
    public static GameProfile resolve(String username) {
        GameProfile mapped = profileRedirects.get(username);
        if (mapped != null) return mapped;
        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        return new GameProfile(offlineUUID, username);
    }

    /** Returns true if a redirect exists for the given username. */
    public static boolean hasRedirect(String username) {
        return profileRedirects.containsKey(username);
    }

    /** Loads the alias profiles from disk */
    public static void load() {
        profileRedirects.clear();
        if (Files.exists(PROFILE_PATH)) {
            try (Reader reader = Files.newBufferedReader(PROFILE_PATH)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    String originalName = entry.getKey();
                    JsonObject obj = entry.getValue().getAsJsonObject();
                    String uuidStr = obj.get("uuid").getAsString();
                    String name = obj.get("name").getAsString();
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        profileRedirects.put(originalName, new GameProfile(uuid, name));
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IOException e) {
                System.err.println("[Alias] Failed to load alias_profiles.json: " + e);
            }
        }
    }
}
