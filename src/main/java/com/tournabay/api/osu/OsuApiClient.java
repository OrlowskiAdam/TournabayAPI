package com.tournabay.api.osu;

import com.tournabay.api.osu.model.OsuBeatmap;
import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.Closeable;
import java.io.IOException;

@Getter
public class OsuApiClient implements RestClient, Closeable {
    private static final String OSU_API_URL = "https://osu.ppy.sh/api/v2/";
    private final OkHttpClient httpClient;
    private final OsuApi osuApi;
    @Getter(AccessLevel.NONE)
    private final String tokenValue;

    public OsuApiClient(String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            throw new IllegalArgumentException("Token value cannot be null or empty");
        }
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .authenticator((route, response) -> response.request()
                        .newBuilder()
                        .header("Authorization", "Bearer " + tokenValue)
                        .build())
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
                });
        this.httpClient = httpClientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OSU_API_URL)
                .client(this.httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.osuApi = retrofit.create(OsuApi.class);
        this.tokenValue = tokenValue;
    }

    @Override
    public void close() {
        this.httpClient.connectionPool().evictAll();
    }

    @Override
    public OsuBeatmap getBeatmap(Long beatmapId) throws IOException {
        return this.osuApi.getBeatmap(beatmapId).execute().body();
    }
}
