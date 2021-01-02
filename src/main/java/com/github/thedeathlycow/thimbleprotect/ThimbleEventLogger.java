package com.github.thedeathlycow.thimbleprotect;

import java.util.ArrayList;
import java.util.List;

public class ThimbleEventLogger {

    public static int MaxSavedEventID = 0;
    public static final int MAX_SAVED_EVENTS = 1024;
    static List<ThimbleEvent> EventList = new ArrayList<ThimbleEvent>(MAX_SAVED_EVENTS);

    public static void saveEventsToFile() {
        // TOOD: actually add this lol
        ThimbleEventLogger.EventList = new ArrayList<ThimbleEvent>();
    }

    public static void addEventToLog(ThimbleEvent event) {

        ThimbleEventLogger.EventList.add(event);

        if (ThimbleEventLogger.EventList.size() >= 1023) {
            ThimbleEventLogger.updateMaxSavedID();
            ThimbleEventLogger.saveEventsToFile();
        }
    }

    /**
     * Updates the thimble event max saved id, and returns
     * the new value.
     */
    public static int updateMaxSavedID() {
        ThimbleEventLogger.MaxSavedEventID = 0;

        return ThimbleEventLogger.MaxSavedEventID;
    }


}
