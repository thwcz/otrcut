package org.thwiecz.otrcut;

import org.apache.commons.cli.*;

public class CommandLineParsing {

    public boolean run(String[] args, GlobalVariables globalVariables) {

        CommandLine commandLine;
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();

        try {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption(options.getOption("i"))) {
                globalVariables.setMovieFile(commandLine.getOptionValue(options.getOption("i")));
                globalVariables.setCutListFile(commandLine.getOptionValue(options.getOption("i")) + ".cutlist");
            }
            if (commandLine.hasOption(options.getOption("o"))) {
                globalVariables.setOutputDir(commandLine.getOptionValue(options.getOption("o")));
            }
            if (commandLine.hasOption(options.getOption("g"))) {
                globalVariables.setCutlistOnline();
            }
            if (commandLine.hasOption(options.getOption("e"))) {
                globalVariables.setExact();
            }
            globalVariables.setSnippetList();
            return true;

        } catch (Exception e) {
            System.out.println(e + "\n");
            new HelpFormatter().printHelp("otrcut.jar [options]", options);
            return false;
        }
    }

    private Options getOptions() {

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
        Option getListOnline = Option.builder("g")
                .required(false)
                .hasArg(false)
                .desc("Get Cutlist Online (optional)")
                .longOpt("getonline")
                .build();
        Option exactCutting = Option.builder("e")
                .required(false)
                .hasArg(false)
                .desc("Frame Exact Cutting (optional)")
                .longOpt("exact")
                .build();
        Options options = new Options();

        options.addOption(inputFile);
        options.addOption(outputDirectory);
        options.addOption(getListOnline);
        options.addOption(exactCutting);

        return options;
    }

}
