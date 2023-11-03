package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;

public class GlobalVariables {

    private String cutListFile;
    private boolean exact;
    private String movieFile;
    private String outputDir;
    private String snippetList;

    public String getCutListFile () {
        return cutListFile;
    }

    public void setCutListFile (String fileName) {
        cutListFile = fileName;
    }

    public boolean getExact () {
        return exact;
    }
    public void setExact (String value) {
        switch (value) {
            case "y":
                exact = false;
                break;
            case "n":
                exact = false;
                break;
            default:
                System.out.println("Invalid value for Output Directory");
                System.exit(1);
                break;
        }
    }

    public String getMovieFile () {
        return movieFile;
    }

    public void setMovieFile (String fileName) {
        movieFile = fileName;
    }

    public String getOutputDir () {
        return outputDir;
    }

    public void setOutputDir (String directory) {
        outputDir = directory;
    }

    public void setSnippetList () {
        snippetList = outputDir + FilenameUtils.getName(movieFile) + ".list";
    }

    public String getSnippetList () {
        return snippetList;
    }
}
