package org.thwiecz.otrcut;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class OnlineCutlistOperations {

    public void getCutList(GlobalVariables globalVariables) {
        // method for catching the online cutlist from cutlist.at for the given video file
        if (retrieveExistingCutlists(globalVariables).isEmpty()) {
            System.out.println("List of cutlists was empty!");
        } else {
            String cutListId = analyzeExistingCutlists(retrieveExistingCutlists(globalVariables).get());
            downloadCutlist(globalVariables, cutListId);
        }
    }

    public String analyzeExistingCutlists(StringBuilder existingCutLists) {
        // analyze XML response with cutlists, picking the first one
        String firstCutList = "";

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(existingCutLists.toString().getBytes("UTF-8"));
            Document xmlDocument = dBuilder.parse(inputStream);
            xmlDocument.normalize();
            Element files = xmlDocument.getDocumentElement();
            int numberOfCutlists = Integer.parseInt(files.getAttribute("count"));
            System.out.println(String.valueOf(numberOfCutlists) + " Cutlists found. Taking the first one.");
            NodeList nodeList = xmlDocument.getElementsByTagName("cutlist");
            Node cutList = nodeList.item(0);
            NodeList cutListChilds = cutList.getChildNodes();
            String cutListId = "";
            String cutListName = "";
            for (int i = 0; i < cutListChilds.getLength(); i++) {
                if (cutListChilds.item(i).getNodeName().equals("id")) {
                    cutListId = cutListChilds.item(i).getTextContent();
                }
                if (cutListChilds.item(i).getNodeName().equals("name")) {
                    cutListName = cutListChilds.item(i).getTextContent();
                }
            }
            System.out.println("Downloading " + cutListName + " (ID: " + cutListId + ")");
            firstCutList = cutListId;
        } catch (Exception e) {
            System.out.println(e);
        }
        return firstCutList;
    }
    private Optional<StringBuilder> retrieveExistingCutlists(GlobalVariables globalVariables) {
        // retrieve XML list of Cutlists
        StringBuilder xmlContent = new StringBuilder();

        try {
            URL url = new URL("http://cutlist.at/getxml.php?name=" + FilenameUtils.getName(globalVariables.getMovieFile()));
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            int status = httpConnection.getResponseCode();;

            if (status == 200) {
                BufferedReader inputReader = new BufferedReader(
                        new InputStreamReader(httpConnection.getInputStream()));
                String inputLine;
                while ((inputLine = inputReader.readLine()) != null) {
                    xmlContent.append(inputLine);
                    //System.out.println(inputLine);
                }
                inputReader.close();
            } else {
                System.out.println("Could not reach cutlist.at: HTTP " + String.valueOf(status));
                Reader streamReader = new InputStreamReader(httpConnection.getErrorStream());
            }
            httpConnection.disconnect();

            return Optional.of(xmlContent);

        } catch (Exception e) {
            System.out.println(e);
            return Optional.empty();
        }
    }

    private void downloadCutlist(GlobalVariables globalVariables, String cutListId) {

        // download Cutlist with given ID
        StringBuilder xmlContent = new StringBuilder();

        try {
            URL url = new URL("http://cutlist.at/getfile.php?id=" + cutListId);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            int status = httpConnection.getResponseCode();;

            if (status == 200) {
                BufferedReader inputReader = new BufferedReader(
                        new InputStreamReader(httpConnection.getInputStream()));
                String inputLine;
                Helper cutListFileWriter = new Helper();

                //temporary
                String fileName = globalVariables.getCutListFile();

                File cutListFile = new File(fileName);
                if (cutListFile.exists()) {
                    cutListFile.delete();
                }

                while ((inputLine = inputReader.readLine()) != null) {
                    cutListFileWriter.appendToFile(fileName, inputLine);
                    //System.out.println(inputLine);
                }
                inputReader.close();
            } else {
                System.out.println("Could not reach cutlist.at: HTTP " + String.valueOf(status));
                Reader streamReader = new InputStreamReader(httpConnection.getErrorStream());
            }
            httpConnection.disconnect();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
