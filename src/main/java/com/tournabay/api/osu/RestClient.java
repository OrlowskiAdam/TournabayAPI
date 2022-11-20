package com.tournabay.api.osu;

import com.tournabay.api.osu.model.BeatmapAttributesBody;
import com.tournabay.api.osu.model.BeatmapDifficultyAttributesWrapper;
import com.tournabay.api.osu.model.MultiplayerLobbyData;
import com.tournabay.api.osu.model.OsuBeatmap;

import java.io.IOException;
import java.util.Optional;

public interface RestClient {
    Optional<OsuBeatmap> getBeatmap(Long beatmapId) throws IOException;
    Optional<BeatmapDifficultyAttributesWrapper> getBeatmapAttributes(Long beatmapId, BeatmapAttributesBody body) throws IOException;
    Optional<MultiplayerLobbyData> getMatchData(Long lobbyId) throws IOException;
}
