package com.tournabay.api.osu;

import com.tournabay.api.osu.model.OsuBeatmap;

import java.io.IOException;

public interface RestClient {
    OsuBeatmap getBeatmap(Long beatmapId) throws IOException;
}
