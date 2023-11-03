package org.thwiecz.otrcut;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileOperations {

    public void cleanupTempfiles(GlobalVariables globalVariables) {

        // method for cleaning up the temporarily created files

        try {
            // deleting the video snippets
            BufferedReader br = new BufferedReader(new FileReader(globalVariables.getSnippetList()));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.replace("file '","").replace("'","");
                System.out.println("Deleting " + line);
                new File(line).delete();
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }

        // deleting the snippet list
        System.out.println("Deleting " + globalVariables.getSnippetList());
        new File(globalVariables.getSnippetList()).delete();
    }

    public CutList readCutListContent(GlobalVariables globalVariables) {

        // reading the given cutlist file (ini syntax) int a Map for later usage

        CutList myCutlist = new CutList();

        INIConfiguration iniConfiguration = new INIConfiguration();
        try (FileReader fileReader = new FileReader(globalVariables.getCutListFile())) {
            iniConfiguration.read(fileReader);
            // looping through the cutlist file content
            for (String sSection : iniConfiguration.getSections()) {
                Map<String, String> subSectionMap = new HashMap<>();
                SubnodeConfiguration confSection = iniConfiguration.getSection(sSection);
                Iterator<String> keyIterator = confSection.getKeys();
                while (keyIterator.hasNext()) {
                    String sKey = keyIterator.next();
                    String sValue = confSection.getProperty(sKey).toString();
                    subSectionMap.put(sKey, sValue);
                }
                // add entry to Map
                myCutlist.addCutlistItem(sSection, subSectionMap);
            }

        } catch (Exception e) {
            System.out.println(e + "\n" + e.getMessage());
        }
        return myCutlist;
    }

    public void putToSnippetList(GlobalVariables globalVariables, String sLine) {

        new Helper().appendToFile(globalVariables.getSnippetList(), sLine);

    }

}
