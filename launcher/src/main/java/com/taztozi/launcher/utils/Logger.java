// ============================================================
// Block Launcher - Logger.java
// Created by Taztozi
// Centralized logging utility with multiple levels
// ============================================================

package com.taztozi.launcher.utils;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @brief Centralized logging system with file and logcat output
 * Supports DEBUG, INFO, WARN, ERROR levels
 */
public class Logger {
    private static final String TAG = "TatozLauncher";
    private static final String LOG_DIR = "/sdcard/Taztozi/logs/";
    private static final boolean ENABLE_FILE_LOGGING = true;
    private static boolean debugMode = false;

    /**
     * @brief Set debug mode
     * @param enabled true to enable verbose debug logging
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }

    /**
     * @brief Log debug message
     * @param tag Tag/source identifier
     * @param message Message to log
     */
    public static void debug(String tag, String message) {
        if (debugMode) {
            String fullTag = TAG + "-" + tag;
            Log.d(fullTag, message);
            writeToFile("DEBUG", tag, message);
        }
    }

    /**
     * @brief Log info message
     * @param tag Tag/source identifier
     * @param message Message to log
     */
    public static void info(String tag, String message) {
        String fullTag = TAG + "-" + tag;
        Log.i(fullTag, message);
        writeToFile("INFO", tag, message);
    }

    /**
     * @brief Log warning message
     * @param tag Tag/source identifier
     * @param message Message to log
     */
    public static void warn(String tag, String message) {
        String fullTag = TAG + "-" + tag;
        Log.w(fullTag, message);
        writeToFile("WARN", tag, message);
    }

    /**
     * @brief Log error message
     * @param tag Tag/source identifier
     * @param message Message to log
     */
    public static void error(String tag, String message) {
        String fullTag = TAG + "-" + tag;
        Log.e(fullTag, message);
        writeToFile("ERROR", tag, message);
    }

    /**
     * @brief Log error with exception
     * @param tag Tag/source identifier
     * @param message Message to log
     * @param throwable Exception to log
     */
    public static void error(String tag, String message, Throwable throwable) {
        String fullTag = TAG + "-" + tag;
        Log.e(fullTag, message, throwable);
        writeToFile("ERROR", tag, message + " - " + throwable.toString());
        
        // Log stack trace
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            writeToFile("ERROR", tag, "  at " + element.toString());
        }
    }

    /**
     * @brief Write log message to file
     * @param level Log level
     * @param tag Source tag
     * @param message Message content
     */
    private static void writeToFile(String level, String tag, String message) {
        if (!ENABLE_FILE_LOGGING) return;
        
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
                    .format(new Date());
            String filename = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    .format(new Date()) + ".log";
            File logFile = new File(logDir, filename);
            
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(String.format("[%s] %s/%s: %s\n", timestamp, level, tag, message));
            writer.close();
            
        } catch (IOException e) {
            Log.e(TAG, "Failed to write log file: " + e.getMessage());
        }
    }

    /**
     * @brief Clear old log files (older than days)
     * @param days Number of days to retain
     */
    public static void clearOldLogs(int days) {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) return;
            
            long cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L);
            File[] files = logDir.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    if (file.lastModified() < cutoffTime) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear old logs: " + e.getMessage());
        }
    }
}
