package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;

public class GlobalVariables {

    private String cutListFile;
    private boolean exact;
    private boolean cutlistOnline;
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

    public void setExact () {
        exact = true;
    }

    public boolean getCutlistOnline() {
        return cutlistOnline;
    }

    public void setCutlistOnline () {
        cutlistOnline = true;
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
