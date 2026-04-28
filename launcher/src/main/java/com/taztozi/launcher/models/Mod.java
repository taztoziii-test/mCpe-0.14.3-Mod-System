// ============================================================
// Block Launcher - Mod.java
// Created by Taztozi
// Mod data model and metadata management
// ============================================================

package com.taztozi.launcher.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Represents a mod (.so file) in the launcher
 * Stores metadata, state, and provides loading/unloading functionality
 */
public class Mod {
    private String id;
    private String name;
    private String version;
    private String author;
    private String description;
    private String filePath;
    private long fileSize;
    private long lastModified;
    private boolean enabled;
    private ModState state;
    private List<String> dependencies;
    private String errorMessage;
    private int minAPIVersion;
    private long loadTimeMs;

    /**
     * @brief Mod state enumeration
     */
    public enum ModState {
        DISCOVERED,   // Found but not loaded
        LOADING,      // Load in progress
        ENABLED,      // Loaded and active
        DISABLED,     // Loaded but inactive
        ERROR,        // Failed to load
        UNLOADING     // Being removed
    }

    /**
     * @brief Constructor from file path
     * @param filePath Path to .so file
     */
    public Mod(String filePath) {
        this.filePath = filePath;
        this.state = ModState.DISCOVERED;
        this.enabled = false;
        this.dependencies = new ArrayList<>();
        this.errorMessage = "";
        this.minAPIVersion = 1;
        this.loadTimeMs = 0;
        
        File file = new File(filePath);
        this.fileSize = file.length();
        this.lastModified = file.lastModified();
        
        // Extract ID from filename (e.g., "libsupertools.so" -> "supertools")
        String filename = file.getName();
        if (filename.startsWith("lib") && filename.endsWith(".so")) {
            this.id = filename.substring(3, filename.length() - 3);
        } else {
            this.id = filename.replace(".so", "");
        }
        
        // Default metadata
        this.name = this.id;
        this.version = "1.0.0";
        this.author = "Unknown";
        this.description = "";
    }

    // ── Getters ──────────────────────────────────────────────────

    /**
     * @brief Get mod unique identifier
     * @return Mod ID string
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Get human-readable mod name
     * @return Display name
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Get mod version
     * @return Version string (e.g., "1.0.0")
     */
    public String getVersion() {
        return version;
    }

    /**
     * @brief Get mod author
     * @return Author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @brief Get mod description
     * @return Description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief Get file system path
     * @return Full path to .so file
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @brief Get file size in bytes
     * @return Size of .so file
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @brief Get last modification timestamp
     * @return Unix timestamp in milliseconds
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @brief Check if mod is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @brief Get current mod state
     * @return ModState enum value
     */
    public ModState getState() {
        return state;
    }

    /**
     * @brief Get list of required dependencies
     * @return List of mod IDs this mod depends on
     */
    public List<String> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    /**
     * @brief Get error message if load failed
     * @return Error description or empty string
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @brief Get minimum required API version
     * @return API version number
     */
    public int getMinAPIVersion() {
        return minAPIVersion;
    }

    /**
     * @brief Get load time in milliseconds
     * @return Time taken to load mod
     */
    public long getLoadTimeMs() {
        return loadTimeMs;
    }

    /**
     * @brief Check if file exists on disk
     * @return true if file exists
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }

    /**
     * @brief Check if file is a valid .so library
     * @return true if valid
     */
    public boolean isValidLibrary() {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            return false;
        }
        
        // Check ELF magic number for ARM64
        try {
            byte[] header = new byte[4];
            Files.read(file.toPath()).get(0, header);
            // ELF magic: 0x7F 'E' 'L' 'F'
            return header[0] == 0x7F && header[1] == 'E' && 
                   header[2] == 'L' && header[3] == 'F';
        } catch (Exception e) {
            return false;
        }
    }

    // ── Setters ──────────────────────────────────────────────────

    /**
     * @brief Set mod display name
     * @param name New name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Set mod version
     * @param version Version string
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @brief Set mod author
     * @param author Author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @brief Set mod description
     * @param description Description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @brief Set enabled state
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.state = enabled ? ModState.ENABLED : ModState.DISABLED;
    }

    /**
     * @brief Set current state
     * @param state New ModState
     */
    public void setState(ModState state) {
        this.state = state;
    }

    /**
     * @brief Set error message
     * @param message Error description
     */
    public void setErrorMessage(String message) {
        this.errorMessage = message;
        this.state = ModState.ERROR;
    }

    /**
     * @brief Set minimum required API version
     * @param version API version number
     */
    public void setMinAPIVersion(int version) {
        this.minAPIVersion = version;
    }

    /**
     * @brief Set load time duration
     * @param ms Time in milliseconds
     */
    public void setLoadTimeMs(long ms) {
        this.loadTimeMs = ms;
    }

    /**
     * @brief Add dependency
     * @param modId ID of required mod
     */
    public void addDependency(String modId) {
        if (!dependencies.contains(modId)) {
            dependencies.add(modId);
        }
    }

    /**
     * @brief Clear all dependencies
     */
    public void clearDependencies() {
        dependencies.clear();
    }

    // ── Utility Methods ──────────────────────────────────────────

    /**
     * @brief Get human-readable file size
     * @return Formatted size string (e.g., "2.5 MB")
     */
    public String getFormattedSize() {
        if (fileSize <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = fileSize;
        
        while (size >= 1024.0 && unitIndex < units.length - 1) {
            size /= 1024.0;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * @brief Check if mod has unmet dependencies
     * @param availableMods List of available mods
     * @return true if all dependencies are available
     */
    public boolean hasDependenciesMet(List<Mod> availableMods) {
        for (String depId : dependencies) {
            boolean found = false;
            for (Mod mod : availableMods) {
                if (mod.getId().equals(depId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * @brief Get string representation
     * @return Formatted mod info
     */
    @Override
    public String toString() {
        return String.format("%s v%s by %s [%s] (%s)",
            name, version, author, state.name(), getFormattedSize());
    }

    /**
     * @brief Compare mods by name
     * @param other Another mod
     * @return Comparison result
     */
    public int compareTo(Mod other) {
        if (other == null) return 1;
        return this.name.compareTo(other.name);
    }
}
