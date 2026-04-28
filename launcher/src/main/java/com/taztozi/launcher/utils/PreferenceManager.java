// ============================================================
// Block Launcher - PreferenceManager.java
// Created by Taztozi
// Persistent storage for launcher settings and mod/pack state
// ============================================================

package com.taztozi.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Manages persistent application preferences and state
 * Handles mod enabled/disabled state, active texture pack, and settings
 */
public class PreferenceManager {
    private static final String PREFS_NAME = "taztozi_launcher_prefs";
    private static final String KEY_ENABLED_MODS = "enabled_mods";
    private static final String KEY_ACTIVE_TEXTUREPACK = "active_texturepack";
    private static final String KEY_TEXTUREPACK_STACK = "texturepack_stack";
    private static final String KEY_LAST_MOD_SCAN = "last_mod_scan";
    private static final String KEY_LAST_PACK_SCAN = "last_pack_scan";
    private static final String KEY_MCPE_VERSION = "mcpe_version";
    private static final String KEY_LAUNCH_COUNT = "launch_count";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_THEME = "theme";
    private static final String KEY_AUTO_UPDATE = "auto_update";
    private static final String KEY_MOD_CONFIG = "mod_config_";
    private static final String KEY_TEXTUREPACK_CONFIG = "pack_config_";

    private static PreferenceManager instance;
    private SharedPreferences prefs;
    private Context context;

    /**
     * @brief Private constructor for singleton pattern
     * @param context Android context
     */
    private PreferenceManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @brief Get singleton instance
     * @param context Android context
     * @return PreferenceManager instance
     */
    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    // ── MOD STATE ─────────────────────────────────────────────────

    /**
     * @brief Set mod as enabled
     * @param modId ID of mod to enable
     */
    public void enableMod(String modId) {
        Set<String> enabled = getEnabledMods();
        enabled.add(modId);
        prefs.edit().putStringSet(KEY_ENABLED_MODS, enabled).apply();
    }

    /**
     * @brief Set mod as disabled
     * @param modId ID of mod to disable
     */
    public void disableMod(String modId) {
        Set<String> enabled = getEnabledMods();
        enabled.remove(modId);
        prefs.edit().putStringSet(KEY_ENABLED_MODS, enabled).apply();
    }

    /**
     * @brief Check if mod is enabled
     * @param modId ID of mod to check
     * @return true if enabled
     */
    public boolean isModEnabled(String modId) {
        return getEnabledMods().contains(modId);
    }

    /**
     * @brief Get all enabled mod IDs
     * @return Set of enabled mod IDs
     */
    public Set<String> getEnabledMods() {
        return new HashSet<>(prefs.getStringSet(KEY_ENABLED_MODS, new HashSet<>()));
    }

    /**
     * @brief Set all enabled mods
     * @param modIds Set of mod IDs
     */
    public void setEnabledMods(Set<String> modIds) {
        prefs.edit().putStringSet(KEY_ENABLED_MODS, modIds).apply();
    }

    /**
     * @brief Clear all enabled mods
     */
    public void clearEnabledMods() {
        prefs.edit().putStringSet(KEY_ENABLED_MODS, new HashSet<>()).apply();
    }

    // ── TEXTURE PACK STATE ────────────────────────────────────────

    /**
     * @brief Set active texture pack
     * @param packId ID of texture pack
     */
    public void setActiveTexturePack(String packId) {
        prefs.edit().putString(KEY_ACTIVE_TEXTUREPACK, packId).apply();
    }

    /**
     * @brief Get active texture pack ID
     * @return Pack ID or empty string if none active
     */
    public String getActiveTexturePack() {
        return prefs.getString(KEY_ACTIVE_TEXTUREPACK, "");
    }

    /**
     * @brief Check if texture pack is active
     * @param packId ID of pack to check
     * @return true if pack is active
     */
    public boolean isTexturePackActive(String packId) {
        return getActiveTexturePack().equals(packId);
    }

    /**
     * @brief Set layered texture pack stack
     * Priority: index 0 = highest priority
     * @param packIds List of pack IDs
     */
    public void setTexturePackStack(List<String> packIds) {
        try {
            JSONArray array = new JSONArray();
            for (String id : packIds) {
                array.put(id);
            }
            prefs.edit().putString(KEY_TEXTUREPACK_STACK, array.toString()).apply();
        } catch (Exception e) {
            Logger.error("PreferenceManager", "Failed to set pack stack: " + e.getMessage());
        }
    }

    /**
     * @brief Get layered texture pack stack
     * @return List of pack IDs in priority order
     */
    public List<String> getTexturePackStack() {
        List<String> stack = new ArrayList<>();
        try {
            String json = prefs.getString(KEY_TEXTUREPACK_STACK, "[]");
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                stack.add(array.getString(i));
            }
        } catch (JSONException e) {
            Logger.error("PreferenceManager", "Failed to parse pack stack: " + e.getMessage());
        }
        return stack;
    }

    /**
     * @brief Clear active texture pack
     */
    public void clearActiveTexturePack() {
        prefs.edit().putString(KEY_ACTIVE_TEXTUREPACK, "").apply();
    }

    // ── SCAN TIMESTAMPS ──────────────────────────────────────────

    /**
     * @brief Update last mod directory scan timestamp
     */
    public void updateLastModScan() {
        prefs.edit().putLong(KEY_LAST_MOD_SCAN, System.currentTimeMillis()).apply();
    }

    /**
     * @brief Get last mod scan timestamp
     * @return Unix timestamp in milliseconds
     */
    public long getLastModScan() {
        return prefs.getLong(KEY_LAST_MOD_SCAN, 0);
    }

    /**
     * @brief Update last texture pack directory scan timestamp
     */
    public void updateLastPackScan() {
        prefs.edit().putLong(KEY_LAST_PACK_SCAN, System.currentTimeMillis()).apply();
    }

    /**
     * @brief Get last pack scan timestamp
     * @return Unix timestamp in milliseconds
     */
    public long getLastPackScan() {
        return prefs.getLong(KEY_LAST_PACK_SCAN, 0);
    }

    /**
     * @brief Check if scan is stale (older than threshold)
     * @param lastScan Timestamp to check
     * @param maxAgeMs Maximum age in milliseconds
     * @return true if older than maxAge
     */
    public boolean isScanStale(long lastScan, long maxAgeMs) {
        return (System.currentTimeMillis() - lastScan) > maxAgeMs;
    }

    // ── GAME STATE ────────────────────────────────────────────────

    /**
     * @brief Set detected MCPE version
     * @param version Version string (e.g., "0.14.3")
     */
    public void setMCPEVersion(String version) {
        prefs.edit().putString(KEY_MCPE_VERSION, version).apply();
    }

    /**
     * @brief Get detected MCPE version
     * @return Version string or empty if not detected
     */
    public String getMCPEVersion() {
        return prefs.getString(KEY_MCPE_VERSION, "");
    }

    /**
     * @brief Increment game launch counter
     */
    public void incrementLaunchCount() {
        int count = prefs.getInt(KEY_LAUNCH_COUNT, 0);
        prefs.edit().putInt(KEY_LAUNCH_COUNT, count + 1).apply();
    }

    /**
     * @brief Get total number of game launches
     * @return Launch count
     */
    public int getLaunchCount() {
        return prefs.getInt(KEY_LAUNCH_COUNT, 0);
    }

    /**
     * @brief Check if first launch
     * @return true on first launch
     */
    public boolean isFirstLaunch() {
        return !prefs.getBoolean(KEY_FIRST_LAUNCH, false);
    }

    /**
     * @brief Mark first launch as completed
     */
    public void markFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply();
    }

    // ── SETTINGS ──────────────────────────────────────────────────

    /**
     * @brief Set UI theme
     * @param theme Theme name ("light", "dark", "auto")
     */
    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    /**
     * @brief Get UI theme preference
     * @return Theme name
     */
    public String getTheme() {
        return prefs.getString(KEY_THEME, "auto");
    }

    /**
     * @brief Set auto-update preference
     * @param enabled true to enable auto-update
     */
    public void setAutoUpdate(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_UPDATE, enabled).apply();
    }

    /**
     * @brief Get auto-update preference
     * @return true if auto-update enabled
     */
    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(KEY_AUTO_UPDATE, true);
    }

    // ── MOD CONFIGURATION ─────────────────────────────────────────

    /**
     * @brief Save mod-specific configuration
     * @param modId Mod identifier
     * @param config JSON configuration object
     */
    public void setModConfig(String modId, JSONObject config) {
        try {
            String key = KEY_MOD_CONFIG + modId;
            prefs.edit().putString(key, config.toString()).apply();
        } catch (Exception e) {
            Logger.error("PreferenceManager", 
                "Failed to save config for mod " + modId + ": " + e.getMessage());
        }
    }

    /**
     * @brief Load mod-specific configuration
     * @param modId Mod identifier
     * @return JSON configuration object or empty object if not found
     */
    public JSONObject getModConfig(String modId) {
        try {
            String key = KEY_MOD_CONFIG + modId;
            String configStr = prefs.getString(key, "{}");
            return new JSONObject(configStr);
        } catch (JSONException e) {
            Logger.error("PreferenceManager", 
                "Failed to load config for mod " + modId + ": " + e.getMessage());
            return new JSONObject();
        }
    }

    /**
     * @brief Clear mod-specific configuration
     * @param modId Mod identifier
     */
    public void clearModConfig(String modId) {
        String key = KEY_MOD_CONFIG + modId;
        prefs.edit().remove(key).apply();
    }

    /**
     * @brief Save texture pack configuration
     * @param packId Pack identifier
     * @param config JSON configuration object
     */
    public void setPackConfig(String packId, JSONObject config) {
        try {
            String key = KEY_TEXTUREPACK_CONFIG + packId;
            prefs.edit().putString(key, config.toString()).apply();
        } catch (Exception e) {
            Logger.error("PreferenceManager", 
                "Failed to save config for pack " + packId + ": " + e.getMessage());
        }
    }

    /**
     * @brief Load texture pack configuration
     * @param packId Pack identifier
     * @return JSON configuration object
     */
    public JSONObject getPackConfig(String packId) {
        try {
            String key = KEY_TEXTUREPACK_CONFIG + packId;
            String configStr = prefs.getString(key, "{}");
            return new JSONObject(configStr);
        } catch (JSONException e) {
            Logger.error("PreferenceManager", 
                "Failed to load config for pack " + packId + ": " + e.getMessage());
            return new JSONObject();
        }
    }

    // ── ADVANCED ──────────────────────────────────────────────────

    /**
     * @brief Clear all preferences
     * WARNING: Resets entire launcher state
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    /**
     * @brief Export preferences as JSON
     * @return JSONObject containing all preferences
     */
    public JSONObject exportAsJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put("enabledMods", new JSONArray(new ArrayList<>(getEnabledMods())));
            json.put("activeTexturePack", getActiveTexturePack());
            json.put("texturePackStack", new JSONArray(getTexturePackStack()));
            json.put("mcpeVersion", getMCPEVersion());
            json.put("launchCount", getLaunchCount());
            json.put("theme", getTheme());
            json.put("autoUpdate", isAutoUpdateEnabled());
            return json;
        } catch (JSONException e) {
            Logger.error("PreferenceManager", "Failed to export preferences: " + e.getMessage());
            return new JSONObject();
        }
    }

    /**
     * @brief Get SharedPreferences directly for advanced use
     * @return SharedPreferences instance
     */
    public SharedPreferences getSharedPreferences() {
        return prefs;
    }
}
