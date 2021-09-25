package com.tournabay.api.jackson.user;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournabay.api.exception.BadRequestException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserReader {

    private String jsonOutput = null;
    private Long id;

    public UserReader(Long osuId) {
        try {
            String url = "https://osu.ppy.sh/api/get_user?u=" + osuId + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
            this.jsonOutput = get(url);
            this.id = osuId;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean apiRequest(Long osuId) {
        try {
            String url = "https://osu.ppy.sh/api/get_user?u=" + osuId + "&k=3e5accb0bcd2c2d7f6a9801b3d922d39ca8f26b0";
            this.jsonOutput = get(url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkResponse() {
        return !jsonOutput.equals("[]");
    }

    private UserProperty getProperty() {
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            UserProperty[] properties = mapper.readValue(jsonOutput, UserProperty[].class);
            if (properties.length == 0) {
                return null;
            }
            return properties[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getOsuId() {
        UserProperty property = getProperty();
        if (property != null) {
            return property.getOsuId();
        } else {
            return -1L;
        }
    }

    public String getUsername() {
        UserProperty property = getProperty();
        if (property != null) {
            return property.getUsername();
        } else {
            return "#ERR";
        }
    }

    public int getRank() {
        UserProperty property = getProperty();
        if (property != null) {
            return property.getRank();
        } else {
            return -1;
        }
    }

    public int getCountryRank() {
        UserProperty property = getProperty();
        if (property != null) {
            return property.getCountryRank();
        } else {
            return -1;
        }
    }

    public String getCountry() {
        UserProperty property = getProperty();
        if (property != null) {
            return property.getCountry();
        } else {
            return "";
        }
    }

    public int getPerformancePoints() {
        UserProperty property = getProperty();
        if (property != null) {
            return (int) property.getPerformancePoints();
        } else {
            return -1;
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
