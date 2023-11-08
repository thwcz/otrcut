package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class GlobalVariables {

    private String cutListFile;
    private boolean exact;
    private boolean cutlistOnline;
    private String movieFile;
    private String outputDir;
    private String snippetList;
    private Map<String, Double> keyFrames = new HashMap<>();

    public String getCutListFile () {
        return cutListFile;
    }

    public void setCutListFile (String fileName) {
        cutListFile = fileName;
    }

    public boolean getCutlistOnline() {
        return cutlistOnline;
    }

    public void setCutlistOnline () {
        cutlistOnline = true;
    }

    public boolean getExact () {
        return exact;
    }

    public void setExact () {
        exact = true;
    }

    public void addKeyFrame(String sIndex, Double dPosition) {
        keyFrames.put(sIndex, dPosition);
    }

    public Map<String, Double> getKeyFrames() {
        return keyFrames;
    }

    public double getNextKeyFrame(double frame) {
        double nextKeyFrame = 0.0;
        for (int iIndex = 0; iIndex < getKeyFrames().size(); iIndex++) {
            if ( getKeyFrames().get(String.valueOf(iIndex)) > frame ) {
                //System.out.println("Current Frame: " + frame.toString()
                //        + "Next Key Frame: " + getKeyFrames().get(String.valueOf(iIndex)).toString());
                nextKeyFrame = getKeyFrames().get(String.valueOf(iIndex));
                break;
            }
        }
        return nextKeyFrame;
    }

    public double getPreviousKeyFrame(double frame) {
        double nextKeyFrame = 0.0;
        for (int iIndex = 0; iIndex < getKeyFrames().size(); iIndex++) {
            if ( getKeyFrames().get(String.valueOf(iIndex + 1)) > frame ) {
                //System.out.println("Current Frame: " + frame.toString()
                //        + "Previous Key Frame: " + getKeyFrames().get(String.valueOf(iIndex)).toString());
                nextKeyFrame = getKeyFrames().get(String.valueOf(iIndex));
                break;
            }
        }
        return nextKeyFrame;
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
