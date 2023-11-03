package org.thwiecz.otrcut;

import org.apache.commons.cli.*;

public class CommandLineParsing {

    public boolean run(String[] args, GlobalVariables globalVariables) {

        CommandLine commandLine;

        Option inputFile = Option.builder("i")
                .required(true)
                .hasArg(true)
                .desc("Input File Name")
                .longOpt("infile")
                .build();
        Option outputDirectory = Option.builder("o")
                .required(true)
                .hasArg(true)
                .desc("Output Directory")
                .longOpt("outdir")
                .build();
        Option exactCutting = Option.builder("e")
                .required(false)
                .hasArg(true)
                .desc("Frame Exact Cutting")
                .longOpt("exact")
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(inputFile);
        options.addOption(outputDirectory);
        options.addOption(exactCutting);

        try {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption(inputFile)) {
                globalVariables.setMovieFile(commandLine.getOptionValue(inputFile));
                globalVariables.setCutListFile(commandLine.getOptionValue(inputFile) + ".cutlist");
            }
            if (commandLine.hasOption(inputFile)) {
                globalVariables.setOutputDir(commandLine.getOptionValue(outputDirectory));
            }
            if (commandLine.hasOption(exactCutting)) {
                globalVariables.setExact(commandLine.getOptionValue(exactCutting).toLowerCase());
            } else {
                globalVariables.setExact("y");
            }
            globalVariables.setSnippetList();
            return true;

        } catch (Exception e) {
            System.out.println(e + "\n\n");
            new HelpFormatter().printHelp("otrcut.jar --infile=<file> --outdir=<directory> [--exact=<y/n>]", options);
            return false;
        }
    }

}
