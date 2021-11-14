//Author: @Rishi Meka
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
//method one: java.net.Httpurlconnection
public class driverMain {

    private static HttpURLConnection connection;

    public static void main(String[] args) {
        System.out.println("Disclaimer: The information in this driverMain.application is publicly available information provided by the US Food and Drug Administration (FDA). " +
                "Do not rely on openFDA to make decisions regarding medical care. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.");
        String drugEventInfo = "";
        String drugLabelInfo = "";
        String drugName = "";
        do {
            System.out.print("Please enter the drug name: ");
            drugName = new String(new Scanner(System.in).next());
            drugEventInfo = (getInfo("https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name:" + drugName));
            if(drugEventInfo.contains("\"error\""))
                System.out.println("The name: \"" + drugName + "\" is invalid. Please try again!");
        }
        while(drugEventInfo.contains("\"error\""));
        System.out.println("+-----+-----+-----+-----+-----+\n" +
                           "Medication name: " + drugName);
        drugLabelInfo = (getInfo("https://api.fda.gov/drug/label.json?search=openfda.brand_name:" + drugName));
        printDrugInfo(drugLabelInfo);
        printReactionInfo(drugEventInfo);
        printWarningInfo(drugLabelInfo);

        System.out.println("+-----+-----+-----+-----+-----+");


    }
    public static void printDrugInfo(String info){
        int indexOne = 0;
        if((indexOne = info.indexOf("\"manufacturer_name\"")) != -1){
            indexOne = info.indexOf("  \"", indexOne) + 3;
            System.out.println("Manufacturer name: " + info.substring(indexOne, info.indexOf("\"", indexOne)));
            System.out.println("-------------------------------");
        }
        indexOne = info.indexOf("\"purpose\"");
        indexOne = info.indexOf("        \"", indexOne) + 9;
        System.out.println(info.substring(indexOne, info.indexOf("\"", indexOne)));
    }
    public static void printWarningInfo(String info){
        int index = 0;
        if((index = info.indexOf("\"warnings\"")) != -1){
            System.out.println("-------------------------------");
            System.out.println("Warnings: ");
            index = info.indexOf("  \"", index) + 3;
            System.out.println(" - " + info.substring(index, info.indexOf("\"", index)));
        }
        if((index = info.indexOf("\"keep_out_of_reach_of_children\"")) != -1){
            index = info.indexOf("  \"", index) + 3;
            System.out.println(" - " + info.substring(index, info.indexOf("\"", index)));
        }
        if((index = info.indexOf("\"pregnancy_or_breast_feeding\"")) != -1){
            index = info.indexOf("  \"", index) + 3;
            System.out.println(" - " + info.substring(index, info.indexOf("\"", index)));
        }
        if((index = info.indexOf("\"stop_use\"")) != -1){
            index = info.indexOf("  \"", index) + 3;
            System.out.println(" - " + info.substring(index, info.indexOf("\"", index)));
        }
        if((index = info.indexOf("\"do_not_use\"")) != -1){
            index = info.indexOf("  \"", index) + 3;
            System.out.println(" - " + info.substring(index, info.indexOf("\"", index)));
        }
        if((index = info.indexOf("\"questions\"")) != -1){
            System.out.println("-------------------------------");
            index = info.indexOf("  \"", index) + 3;
            System.out.println(info.substring(index, info.indexOf("\"", index)));
        }
    }
    public static void printReactionInfo(String drugInfo){
        int indexOne = drugInfo.indexOf("\"reaction\": [");
        int indexTwo = drugInfo.indexOf("]", indexOne);
        String drugReactions = (drugInfo.substring(indexOne, indexTwo));
        ArrayList<String> reactions = new ArrayList<>();
        while((indexOne = drugReactions.indexOf("reactionmeddrapt")) != -1){
            indexTwo = drugReactions.indexOf("\": \"", indexOne) + 4;
            indexOne = drugReactions.indexOf("\",", indexTwo);
            reactions.add(drugReactions.substring(indexTwo, indexOne));
            drugReactions = drugReactions.substring(indexOne);
        }
        System.out.println("Possible reaction(s) to the drug may include: ");
        for(int i = 0; i < reactions.size(); i++){
            System.out.println("- " + reactions.get(i));
        }
    }
    public static String getInfo(String urlStr){
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            //request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2500);
            connection.setReadTimeout(2500);

            int status = connection.getResponseCode();
            //bad status
            if(status > 299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            else{
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            //System.out.println(responseContent.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }
        return (responseContent.toString());
    }
}

