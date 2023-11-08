package org.thwiecz.otrcut;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Helper {

    public String convertSecondsToTime(double iTotalSecs) {
        int iHours = (int) iTotalSecs / 3600;
        int iMinutes = (int) ((iTotalSecs % 3600) / 60);
        double iSeconds = iTotalSecs % 60;

        return (iHours < 10 ? "0" : "") + iHours + ":" + (iMinutes < 10 ? "0" : "") + iMinutes + ":"
                + (iSeconds < 10 ? "0" : "") + iSeconds;
    }

    public void appendToFile(String fileName, String input) {

        try {
            File file = new File(fileName);
            FileUtils.writeStringToFile(
                    file, input + "\r\n", StandardCharsets.UTF_8, true);
        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }
    }

}
