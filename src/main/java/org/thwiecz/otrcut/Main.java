package org.thwiecz.otrcut;

public class Main {

    public static void main(String[] args) {

        GlobalVariables globalVariables = new GlobalVariables();

        if (new CommandLineParsing().run(args, globalVariables)) {
            CutList myCutlist = new FileOperations().readCutListContent(globalVariables);
            new VideoOperations().cutVideo(globalVariables, myCutlist);
            new VideoOperations().mergeSnippets(globalVariables);
            new FileOperations().cleanupTempfiles(globalVariables);
        }
    }
}