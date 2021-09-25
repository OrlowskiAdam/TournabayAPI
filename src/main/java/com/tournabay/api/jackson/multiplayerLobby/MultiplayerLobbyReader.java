package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MultiplayerLobbyReader {

    private String jsonOutput = null;
    private Long id;

    public MultiplayerLobbyReader(Long id) {
        try {
            String url = "https://osu.ppy.sh/api/get_match?mp=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
            this.jsonOutput = get(url);
            this.id = id;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private boolean apiRequest(Long id) {
        try {
            String url = "https://osu.ppy.sh/api/get_match?mp=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
            this.jsonOutput = get(url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkResponse() throws Exception {
        if (jsonOutput == null) {
            boolean b = apiRequest(this.id);
            if (!b) {
                throw new Exception("RESPONSE IS STILL NULL!");
            }
        }
    }

    public List<MultiplayerLobbyProperty> getProperty() {
        try {
            checkResponse();

            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<MultiplayerLobbyProperty> properties = Arrays.asList(mapper.readValue(jsonOutput, MultiplayerLobbyProperty[].class));
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String get(String getUrl) throws IOException {
        URL url = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        return read(con.getInputStream());
    }

    private String read(InputStream is) throws IOException {
        BufferedReader in = null;
        String inputLine;
        StringBuilder body;
        try {
            in = new BufferedReader(new InputStreamReader(is));

            body = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();

            return "[" + body.toString() + "]";

        } catch (IOException ioe) {
            throw ioe;
        } finally {
            closeQuietly(in);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void flushData() {
        this.jsonOutput = "";
        this.id = null;
    }
}
