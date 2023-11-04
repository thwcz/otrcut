package org.thwiecz.otrcut;

public class Main {

    public static void main(String[] args) {

        // CLI example parameters -i c:\tmp\Videos\Running_Man_23.05.10_22-50_4plus_125_TVOON_DE.mpg.HD.avi -o c:\tmp\Videos\Cut\ -g

        GlobalVariables globalVariables = new GlobalVariables();

        if (new CommandLineParsing().run(args, globalVariables)) {
            if (globalVariables.getCutlistOnline()) {
                new OnlineCutlistOperations().getCutList(globalVariables);
            }
            CutList myCutlist = new FileOperations().readCutListContent(globalVariables);
            new VideoOperations().cutVideo(globalVariables, myCutlist);
            new VideoOperations().mergeSnippets(globalVariables);
            new FileOperations().cleanupTempfiles(globalVariables);
        }
    }
}