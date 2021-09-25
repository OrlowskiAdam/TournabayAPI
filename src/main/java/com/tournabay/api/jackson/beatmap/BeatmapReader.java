package com.tournabay.api.jackson.beatmap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BeatmapReader {

    private String jsonOutput = null;
    private Long id;

    public BeatmapReader(Long id, String modifier) {
        try {
            if (modifier == null) {
                String url = "https://osu.ppy.sh/api/get_beatmaps?b=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
                this.jsonOutput = get(url);
                this.id = id;
            } else if (modifier.equalsIgnoreCase("HR")) {
                String url = "https://osu.ppy.sh/api/get_beatmaps?b=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0&mods=16";
                this.jsonOutput = get(url);
                this.id = id;
            } else if (modifier.equalsIgnoreCase("DT")) {
                String url = "https://osu.ppy.sh/api/get_beatmaps?b=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0&mods=64";
                this.jsonOutput = get(url);
                this.id = id;
            } else {
                String url = "https://osu.ppy.sh/api/get_beatmaps?b=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
                this.jsonOutput = get(url);
                this.id = id;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean apiRequest(Long id) {
        try {
            String url = "https://osu.ppy.sh/api/get_beatmaps?b=" + id + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
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

    private BeatmapProperty getProperty() {
        try {
            checkResponse();

            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            BeatmapProperty[] properties = mapper.readValue(jsonOutput, BeatmapProperty[].class);
            return properties[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getMapsetId() {
        BeatmapProperty property = getProperty();
        return property.getMapsetId();
    }

    public int getLength() {
        BeatmapProperty property = getProperty();
        return property.getLength();
    }

    public Long getBeatmapId() {
        BeatmapProperty property = getProperty();
        return property.getBeatmapId();
    }

    public String getTitle() {
        BeatmapProperty property = getProperty();
        return property.getTitle();
    }

    public String getArtist() {
        BeatmapProperty property = getProperty();
        return property.getArtist();
    }

    public String getDifficultyName() {
        BeatmapProperty property = getProperty();
        return property.getDifficultyName();
    }

    public Long getCreatorId() {
        BeatmapProperty property = getProperty();
        return property.getCreatorId();
    }

    public String getCreator() {
        BeatmapProperty property = getProperty();
        return property.getCreator();
    }

    public Float getCs() {
        BeatmapProperty property = getProperty();
        return property.getCs();
    }

    public Float getAr() {
        BeatmapProperty property = getProperty();
        return property.getAr();
    }

    public Float getHp() {
        BeatmapProperty property = getProperty();
        return property.getHp();
    }

    public Float getOd() {
        BeatmapProperty property = getProperty();
        return property.getOd();
    }

    public Float getStarRating() {
        BeatmapProperty property = getProperty();
        return property.getStarRating();
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

            return body.toString();
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
