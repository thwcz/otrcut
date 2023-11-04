package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class VideoOperations {

    private double dTotalSecs = 0.0;

    public void cutVideo(GlobalVariables globalVariables, CutList myCutlist) {
        // Parent cutting method
        // calls the method for cutting out the snippets, listed in the cutlist

        File snippetListFile = new File(globalVariables.getSnippetList());
        if (snippetListFile.exists()) {
            snippetListFile.delete();
        }

        Map<String, Map<String, String>> sortedCutListFileContents = new TreeMap<>(myCutlist.getCutlistItems());

        for (Map.Entry<String, Map<String, String>> cut: sortedCutListFileContents.entrySet()) {
            // we are only interested in the "Cut" entries of the Map
            if (cut.getKey().contains("Cut")) {

                if (Double.parseDouble(cut.getValue().get("Duration")) != 0.0) {
                    double dStart = Double.parseDouble(cut.getValue().get("Start"));
                    Helper secondsToTime = new Helper();
                    String sStartHms = secondsToTime.convertSecondsToTime((int) Double.parseDouble(cut.getValue().get("Start")));
                    String sDurationHms = secondsToTime.convertSecondsToTime((int) Double.parseDouble(cut.getValue().get("Duration")));
                    extractSnippets(globalVariables, cut.getKey(), sStartHms, sDurationHms);
                }
            }
        }
    }

    private void decode() {

        // planned: integrate OTRKEY decoder

        // -uotr email
        // -pwotr passwort
        // -f Datei
        // -d Eingabe-Ordner
        // -o Ausgabe-Ordner
        // -del delete otrkey (true/false)
        // -no no-overwrite (true/false)
        // -toinput decode to inputfolder (true/false)
        // -c close after done (true/false)
        // -hide starte minimiert

    }

    private void encodingWatcher(Process process) {

        // watches the encoding process and provides status output.

        Scanner sc = new Scanner(process.getErrorStream());

        // Find duration
        Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
        String sDuration = sc.findWithinHorizon(durPattern, 0);
        if (sDuration == null)
            throw new RuntimeException("Could not parse duration.");
        String[] sHoursMinutesSeconds = sDuration.split(":");
        System.out.println("Total duration: " + sHoursMinutesSeconds[0] + ":" + sHoursMinutesSeconds[1] + ":" + sHoursMinutesSeconds[2]);
        // convert the total duration string to seconds for calculations
        dTotalSecs = Integer.parseInt(sHoursMinutesSeconds[0]) * 3600
                + Integer.parseInt(sHoursMinutesSeconds[1]) *   60
                + Double.parseDouble(sHoursMinutesSeconds[2]);

        // Find time as long as possible.
        Pattern timePattern = Pattern.compile("(?<=time=)[-\\d:.]*");
        String sMatch;
        String[] sArrayMatchSplit;
        int iProgressOld = 0;
        while (null != (sMatch = sc.findWithinHorizon(timePattern, 0))) {
            sMatch = sMatch.replace("-","");
            sArrayMatchSplit = sMatch.split(":");
            // build Progress in percent and print it
            double sProgress = (Integer.parseInt(sArrayMatchSplit[0]) * 3600 +
                    Integer.parseInt(sArrayMatchSplit[1]) * 60 +
                    Double.parseDouble(sArrayMatchSplit[2])) / dTotalSecs;
            if (iProgressOld != (int) (sProgress * 100)) {
                System.out.println(sMatch);
                System.out.printf("Progress: %.2f%%%n", sProgress * 100);
                iProgressOld = (int) (sProgress * 100);
            }
        }
    }

    private void extractSnippets(GlobalVariables globalVariables, String cut, String start, String duration) {

        // method for extracting the video snippets out of the input video
        String sLine;
        String sCommand = "ffmpeg -hide_banner -y";

        try {
            String sOutputFile = globalVariables.getOutputDir() + FilenameUtils.getName(globalVariables.getMovieFile())
                    .replace("avi",cut + ".avi");
            System.out.println("Writing " + cut + " (from " + start + ", duration " + duration + ") to " + sOutputFile);

            if (globalVariables.getExact()) {
                // exact with encoding
                System.out.println("Cutting exact with encoding");
                sCommand = sCommand +
                        " -i " + globalVariables.getMovieFile() +
                        " -ss " + start +
                        " -t " + duration +
                        " -c:v libx264 -crf 30 ";
            } else {
                // input seeking
                System.out.println("Doing quick-cut by input seeking");
                sCommand = sCommand +
                        " -ss " + start +
                        " -i " + globalVariables.getMovieFile() +
                        " -t " + duration +
                        " -c:v copy -c:a copy ";
            }
            sCommand = sCommand + sOutputFile;
            Process process = Runtime.getRuntime().exec(sCommand);

            // ensure, all ffmpeg processes are killed, when the java application ends.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {process.destroy();}));

            encodingWatcher(process);
            // write snippet file name to a file
            // this is required by ffmpeg for later merging to a single video
            new FileOperations().putToSnippetList(globalVariables, "file '" + sOutputFile + "'");
        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }
    }

    public void mergeSnippets(GlobalVariables globalVariables) {

        // method for merging the video snippets to the final video file
        String sLine;

        try {
            String sOutputFile = globalVariables.getOutputDir()
                    + FilenameUtils.getName(globalVariables.getMovieFile())
                    .replaceAll("_[0-9][0-9].[0-9][0-9].[0-9][0-9].*HD","");
            System.out.println("Writing final file " + sOutputFile);
            String sCommand = "ffmpeg -hide_banner -loglevel error -y -f concat -safe 0" +
                    " -i " + globalVariables.getSnippetList() +
                    " -c copy " + sOutputFile;
            Process process = Runtime.getRuntime().exec(sCommand);
            process.waitFor();

        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }
    }
}
