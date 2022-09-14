package com.autcraft.com.betterlogs;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;

public class Webhook {
    public void send(String webhookurl, String title, String message, String image){
        String jsonBrut = "";
        jsonBrut += "{\"embeds\": [{"
                + "\"title\": \""+ title +"\","
                + "\"description\": \""+ message +"\","
                + "\"thumbnail\": {\"url\": \"" + image + "\"},"
                + "\"color\": 15258703"
                + "}]}";
        // Strip out line breaks (most likely from books) that will break the json
        jsonBrut = jsonBrut.replace("\n", "").replace("\r", "");
        try {
            URL url = new URL(webhookurl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.addRequestProperty("Content-Type", "application/json");
            con.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            OutputStream stream = con.getOutputStream();
            stream.write(jsonBrut.getBytes());
            stream.flush();
            stream.close();
            con.getInputStream().close();
            con.disconnect();
        }
        catch (Exception e){
            //System.out.println(jsonBrut);
            e.printStackTrace();
        }
    }
}
