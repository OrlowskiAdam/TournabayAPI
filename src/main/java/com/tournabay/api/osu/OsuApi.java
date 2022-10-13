package com.tournabay.api.osu;

import com.tournabay.api.osu.model.BeatmapAttributesBody;
import com.tournabay.api.osu.model.BeatmapDifficultyAttributesWrapper;
import com.tournabay.api.osu.model.OsuBeatmap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OsuApi {

    /**
     * Retrieve a single beatmap record.
     *
     * @param beatmapId The id of the beatmap you want to get.
     * @return A Beatmap object.
     */
    @GET("beatmaps/{beatmap}")
    Call<OsuBeatmap> getBeatmap(@Path("beatmap") Long beatmapId);

    @POST("beatmaps/{beatmap}/attributes")
    Call<BeatmapDifficultyAttributesWrapper> getBeatmapAttributes(@Path("beatmap") Long beatmapId, @Body BeatmapAttributesBody body);
}
