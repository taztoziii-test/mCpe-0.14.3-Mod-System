// ============================================================
// Block Launcher - TexturePack.java
// Created by Taztozi
// Texture pack data model and metadata management
// ============================================================

package com.taztozi.launcher.models;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

/**
 * @brief Represents a texture pack (.zip file) in the launcher
 * Stores metadata, resolution, and pack information
 */
public class TexturePack {
    private String id;
    private String name;
    private String author;
    private String version;
    private String description;
    private String filePath;
    private long fileSize;
    private long lastModified;
    private int resolution;      // 16, 32, 64, 128, 256
    private String gameVersion;  // Target game version
    private boolean hasThumbnail;
    private byte[] thumbnailData; // PNG bytes
    private PackState state;
    private String errorMessage;

    /**
     * @brief Texture pack state enumeration
     */
    public enum PackState {
        VALID,        // Valid and ready to use
        LOADING,      // Currently loading metadata
        INVALID,      // Invalid zip or missing pack.json
        ERROR,        // Failed to load
        ACTIVE        // Currently applied
    }

    /**
     * @brief Constructor from file path
     * @param filePath Path to .zip file
     */
    public TexturePack(String filePath) {
        this.filePath = filePath;
        this.state = PackState.LOADING;
        this.errorMessage = "";
        this.hasThumbnail = false;
        this.thumbnailData = null;
        this.resolution = 16;
        this.gameVersion = "0.14.3";
        
        File file = new File(filePath);
        this.fileSize = file.length();
        this.lastModified = file.lastModified();
        
        // Extract ID from filename (without .zip)
        String filename = file.getName();
        this.id = filename.endsWith(".zip") ? 
                  filename.substring(0, filename.length() - 4) : filename;
        
        // Default metadata
        this.name = this.id;
        this.version = "1.0.0";
        this.author = "Unknown";
        this.description = "";
        
        // Load metadata from pack.json
        loadMetadata();
    }

    /**
     * @brief Load pack metadata from pack.json in zip
     * Reads pack name, author, version, resolution, etc.
     */
    private void loadMetadata() {
        try {
            ZipFile zipFile = new ZipFile(filePath);
            ZipEntry entry = zipFile.getEntry("pack.json");
            
            if (entry == null) {
                setErrorMessage("Missing pack.json");
                zipFile.close();
                return;
            }
            
            byte[] buffer = new byte[(int) entry.getSize()];
            zipFile.getInputStream(entry).read(buffer);
            String jsonStr = new String(buffer, "UTF-8");
            
            JSONObject json = new JSONObject(jsonStr);
            
            // Read metadata fields
            if (json.has("name")) {
                this.name = json.getString("name");
            }
            if (json.has("author")) {
                this.author = json.getString("author");
            }
            if (json.has("version")) {
                this.version = json.getString("version");
            }
            if (json.has("description")) {
                this.description = json.getString("description");
            }
            if (json.has("resolution")) {
                this.resolution = json.getInt("resolution");
            }
            if (json.has("gameVersion")) {
                this.gameVersion = json.getString("gameVersion");
            }
            
            // Check for thumbnail
            ZipEntry thumbEntry = zipFile.getEntry("pack_icon.png");
            if (thumbEntry != null) {
                byte[] thumbBuffer = new byte[(int) thumbEntry.getSize()];
                zipFile.getInputStream(thumbEntry).read(thumbBuffer);
                this.thumbnailData = thumbBuffer;
                this.hasThumbnail = true;
            }
            
            zipFile.close();
            this.state = PackState.VALID;
            
        } catch (JSONException e) {
            setErrorMessage("Invalid pack.json: " + e.getMessage());
        } catch (IOException e) {
            setErrorMessage("Failed to read zip: " + e.getMessage());
        } catch (Exception e) {
            setErrorMessage("Unexpected error: " + e.getMessage());
        }
    }

    // ── Getters ──────────────────────────────────────────────────

    /**
     * @brief Get pack unique identifier
     * @return Pack ID string
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Get pack display name
     * @return Pack name
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Get pack author
     * @return Author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @brief Get pack version
     * @return Version string
     */
    public String getVersion() {
        return version;
    }

    /**
     * @brief Get pack description
     * @return Description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief Get file system path
     * @return Full path to .zip file
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @brief Get file size in bytes
     * @return Size of .zip file
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
     * @brief Get texture resolution
     * @return Resolution in pixels (16, 32, 64, etc.)
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * @brief Get resolution as display string
     * @return Formatted string (e.g., "32x")
     */
    public String getResolutionString() {
        return resolution + "x";
    }

    /**
     * @brief Get target game version
     * @return Game version string
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * @brief Check if pack has thumbnail
     * @return true if pack_icon.png exists
     */
    public boolean hasThumbnail() {
        return hasThumbnail;
    }

    /**
     * @brief Get thumbnail PNG bytes
     * @return Raw PNG data or null
     */
    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    /**
     * @brief Get current pack state
     * @return PackState enum value
     */
    public PackState getState() {
        return state;
    }

    /**
     * @brief Get error message if load failed
     * @return Error description or empty string
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @brief Check if file exists on disk
     * @return true if file exists
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }

    /**
     * @brief Check if pack is valid
     * @return true if state is VALID or ACTIVE
     */
    public boolean isValid() {
        return state == PackState.VALID || state == PackState.ACTIVE;
    }

    // ── Setters ──────────────────────────────────────────────────

    /**
     * @brief Set pack display name
     * @param name New name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Set pack author
     * @param author Author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @brief Set pack version
     * @param version Version string
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @brief Set pack description
     * @param description Description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @brief Set texture resolution
     * @param resolution Resolution in pixels
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /**
     * @brief Set pack state
     * @param state New PackState
     */
    public void setState(PackState state) {
        this.state = state;
    }

    /**
     * @brief Set error message
     * @param message Error description
     */
    public void setErrorMessage(String message) {
        this.errorMessage = message;
        this.state = PackState.ERROR;
    }

    /**
     * @brief Set active state
     */
    public void setActive() {
        this.state = PackState.ACTIVE;
    }

    /**
     * @brief Set inactive state
     */
    public void setInactive() {
        this.state = PackState.VALID;
    }

    /**
     * @brief Set thumbnail data
     * @param data PNG bytes
     */
    public void setThumbnailData(byte[] data) {
        this.thumbnailData = data;
        this.hasThumbnail = (data != null && data.length > 0);
    }

    // ── Utility Methods ──────────────────────────────────────────

    /**
     * @brief Get human-readable file size
     * @return Formatted size string
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
     * @brief Get string representation
     * @return Formatted pack info
     */
    @Override
    public String toString() {
        return String.format("%s v%s by %s [%s] (%s)",
            name, version, author, resolution + "x", getFormattedSize());
    }

    /**
     * @brief Compare packs by name
     * @param other Another pack
     * @return Comparison result
     */
    public int compareTo(TexturePack other) {
        if (other == null) return 1;
        return this.name.compareTo(other.name);
    }
}
