package com.github.thedeathlycow.thimbleprotect;

import java.util.ArrayList;
import java.util.List;

public class ThimbleEventLogger {

    static List<ThimbleEvent> EventList = new ArrayList<ThimbleEvent>(1024);

    public static void saveEventsToFile() {
        // TOOD: actually add this lol
        ThimbleEventLogger.EventList = new ArrayList<ThimbleEvent>();
    }

    public static void addEventToLog(ThimbleEvent event) {

        ThimbleEventLogger.EventList.add(event);

        if (ThimbleEventLogger.EventList.size() >= 1023) {
            ThimbleEventLogger.saveEventsToFile();
        }
    }

}
