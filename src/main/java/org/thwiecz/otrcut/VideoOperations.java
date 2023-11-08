package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class VideoOperations {

    private double dTotalSecs = 0.0;

    public void cutVideo(GlobalVariables globalVariables, CutList myCutlist) {
        // Parent cutting method
        // calls the method for cutting out the snippets, listed in the cutlist

        getKeyFrames(globalVariables);

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
                    double dEnd = Double.parseDouble(cut.getValue().get("Start")) + Double.parseDouble(cut.getValue().get("Duration"));
                    double dStartNextKeyFrame = globalVariables.getNextKeyFrame(dStart);
                    double dEndPreviousKeyFrame = globalVariables.getPreviousKeyFrame(dEnd);
                    Helper secondsToTime = new Helper();
                    String sStartHms;
                    String sDurationHms;

                    // Snippet Start: Encode, from: Start, to : until next key frame after Start
                    sStartHms = secondsToTime.convertSecondsToTime(Double.parseDouble(cut.getValue().get("Start")));
                    sDurationHms = secondsToTime.convertSecondsToTime(dStartNextKeyFrame - dStart);
                    extractSnippets(globalVariables, cut.getKey() + "s", sStartHms, sDurationHms, Double.parseDouble(cut.getValue().get("Start")), (dStartNextKeyFrame - dStart));

                    // Main Snippet: Copy, from: next key frame after Start, to : key frame previous to End
                    sStartHms = secondsToTime.convertSecondsToTime(dStartNextKeyFrame);
                    sDurationHms = secondsToTime.convertSecondsToTime(dEndPreviousKeyFrame - dStartNextKeyFrame);
                    extractSnippets(globalVariables, cut.getKey() + "m", sStartHms, sDurationHms, dStartNextKeyFrame, (dStartNextKeyFrame + dEndPreviousKeyFrame));

                    // Snippet End: Encode, from: key frame previous to End, to : End
                    sStartHms = secondsToTime.convertSecondsToTime(dEndPreviousKeyFrame);
                    sDurationHms = secondsToTime.convertSecondsToTime(dEnd - dEndPreviousKeyFrame);
                    extractSnippets(globalVariables, cut.getKey() + "e", sStartHms, sDurationHms, dEndPreviousKeyFrame, (dEnd - dEndPreviousKeyFrame));
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

    private void encodingWatcher(Process process, double dStartSecs, double dTotalSecs) {

        // watches the encoding process and provides status output.

        Scanner sc = new Scanner(process.getErrorStream());

        System.out.println("Snippet length: " + new Helper().convertSecondsToTime((int) dTotalSecs) + " seconds");

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
                    Double.parseDouble(sArrayMatchSplit[2])) / (dTotalSecs);
            if (true) { //if (iProgressOld != (int) (sProgress * 100)) {
                //System.out.println(sMatch);
                System.out.printf("Progress: %.2f%%%n", sProgress * 100);
                iProgressOld = (int) (sProgress * 100);
            }
        }
    }

    private void extractSnippets(GlobalVariables globalVariables, String sCut, String sStart, String sDuration, double dStartSecs, double dTotalSecs) {

        // method for extracting the video snippets out of the input video
        String sLine;

        try {
            String sOutputFile = globalVariables.getOutputDir() + FilenameUtils.getName(globalVariables.getMovieFile())
                    .replace("avi",sCut + ".avi");
            System.out.println("Writing " + sCut + " (from " + sStart + ", length " + sDuration + ") to " + sOutputFile);

            String sCommand = getFfmpegCommand(globalVariables, sCut, sStart, sDuration) + sOutputFile;
            //System.out.println("Command: " + sCommand);
            Process process = Runtime.getRuntime().exec(sCommand);

            // ensure, all ffmpeg processes are killed, when the java application ends.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {process.destroy();}));

            encodingWatcher(process, dStartSecs, dTotalSecs);
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

    private void getKeyFrames(GlobalVariables globalVariables) {

        System.out.println("Getting list of key frames");
        String sCommand = "ffprobe -loglevel error -select_streams v:0"
                + " -show_entries packet=pts_time,flags -of csv=print_section=0 "
                + globalVariables.getMovieFile();
        try {
            Process process = Runtime.getRuntime().exec(sCommand);
            Scanner sc = new Scanner(process.getInputStream());
            int i=0;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if(line.length() > 0 && line.contains("K")) {
                    //System.out.println(line.replace(",K__",""));
                    globalVariables.addKeyFrame(String.valueOf(i), Double.parseDouble(line.replace(",K__","")));
                    i++;
                } else {
                    //don't add empty Line
                }
            }
        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }
    }

    private String getFfmpegCommand(GlobalVariables globalVariables, String sCut, String sStart, String sDuration) {

        String sCommand = "ffmpeg -hide_banner -y";

        if (globalVariables.getExact()) {
            // exact with encoding
            if (sCut.contains("m")) {
                System.out.println("Cutting exact in copy mode");

                sCommand = sCommand +
                        " -ss " + sStart +
                        " -i " + globalVariables.getMovieFile() +
                        " -t " + sDuration +
                        " -c:v copy -c:a copy ";
            } else {
                System.out.println("Cutting exact with encoding");
                sCommand = sCommand +
                        " -ss " + sStart +
                        " -i " + globalVariables.getMovieFile() +
                        " -t " + sDuration +
                        " -c:v libx264 -crf 30 ";
            }
        } else {
            // input seeking
            System.out.println("Doing quick-cut by input seeking");
            sCommand = sCommand +
                    " -ss " + sStart +
                    " -i " + globalVariables.getMovieFile() +
                    " -t " + sDuration +
                    " -c:v copy -c:a copy ";
        }
        return sCommand;
    }
}
