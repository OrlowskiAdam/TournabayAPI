package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.model.beatmap.Stats;
import com.tournabay.api.osu.OsuApiClient;
import com.tournabay.api.osu.model.OsuBeatmap;
import com.tournabay.api.repository.BeatmapRepository;
import com.tournabay.api.util.BeatmapMultiplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BeatmapService {
    private final StatsService statsService;
    private final BeatmapRepository beatmapRepository;

    /**
     * Find a beatmap by its id, or throw a ResourceNotFoundException if it doesn't exist.
     *
     * @param beatmapId The id of the beatmap you want to find.
     * @return A Beatmap object
     */
    public Beatmap findById(Long beatmapId) {
        return beatmapRepository.findById(beatmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap not found"));
    }

    public Beatmap delete(Beatmap beatmap) {
        beatmapRepository.delete(beatmap);
        return beatmap;
    }

    public Beatmap findByBeatmapModificationId(Long beatmapModificationId) {
        return beatmapRepository.findByBeatmapModificationId(beatmapModificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap not found"));
    }

    /**
     * Find the beatmap with the given id in the given beatmap modification, or throw a ResourceNotFoundException if it
     * doesn't exist.
     *
     * @param beatmapId           The id of the beatmap we want to find
     * @param beatmapModification The BeatmapModification object that we're trying to find the beatmap in.
     * @return A Beatmap object
     */
    public Beatmap findByIdAndBeatmapModification(Long beatmapId, BeatmapModification beatmapModification) {
        return beatmapModification.getBeatmaps()
                .stream()
                .filter(beatmap -> beatmap.getId().equals(beatmapId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap not found"));
    }

    /**
     * Save all the beatmaps in the database.
     *
     * @param beatmaps The list of beatmaps to save.
     * @return A list of beatmaps
     */
    public List<Beatmap> saveAll(List<Beatmap> beatmaps) {
        return beatmapRepository.saveAll(beatmaps);
    }

    /**
     * Save the beatmap to the database.
     *
     * @param beatmap The beatmap object that is being saved.
     * @return The beatmap object that was saved.
     */
    public Beatmap save(Beatmap beatmap) {
        return beatmapRepository.save(beatmap);
    }

//    public Beatmap saveAndRecalculate(Beatmap beatmap, User user) {
//        try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
//            Modification modification = beatmap.getBeatmapModification().getModification();
//
//        } catch (Exception e) {
//            throw new BadRequestException("Could not connect to osu! API");
//        }
//    }

    /**
     * It takes a beatmap URL and a game mode, and returns the beatmap ID
     *
     * @param beatmapUrl The URL of the beatmap you want to extract the ID from.
     * @param gameMode   The game mode of the beatmap.
     * @return A Long object
     */
    public Long extractBeatmapId(String beatmapUrl, GameMode gameMode) {
        if (gameMode.equals(GameMode.STANDARD)) {
            try {
                String regex = "https://osu\\.ppy\\.sh/beatmapsets/(\\d+)#osu/(\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(beatmapUrl);
                if (!matcher.matches()) throw new BadRequestException("Invalid beatmap url");
                return Long.parseLong(matcher.group(2));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Couldn't extract beatmap ID from URL.");
            }
        }
        throw new UnsupportedOperationException("Game mode not supported yet");
    }

    /**
     * It takes a beatmap ID and a user, and then it uses the user's osu! API key to fetch the beatmap from the osu! API,
     * and then it saves the beatmap to the database
     *
     * @param beatmapId The ID of the beatmap you want to add.
     * @param user      The user who is adding the beatmap.
     * @return A Mappool object
     */
    @Transactional
    public Beatmap addBeatmap(Long beatmapId, BeatmapModification modification, Mappool mappool, User user) {
        try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
            Beatmap beatmap = osuApiClient.getBeatmap(beatmapId)
                    .map(osuBeatmap ->
                            Beatmap.builder()
                                    .beatmapsetId(osuBeatmap.getBeatmapset_id())
                                    .beatmapId(osuBeatmap.getId())
                                    .artist(osuBeatmap.getBeatmapset().getArtist())
                                    .title(osuBeatmap.getBeatmapset().getTitle())
                                    .version(osuBeatmap.getVersion())
                                    .creator(osuBeatmap.getBeatmapset().getCreator())
                                    .stats(createStatistics(beatmapId, osuBeatmap, modification.getModification(), user))
                                    .position(modification.getBeatmaps().size() + 1)
                                    .beatmapModification(modification)
                                    .normalCover("https://assets.ppy.sh/beatmaps/" + osuBeatmap.getBeatmapset_id() + "/covers/cover.jpg")
                                    .cardCover(osuBeatmap.getBeatmapset().getCovers().getCard())
                                    .listCover(osuBeatmap.getBeatmapset().getCovers().getList())
                                    .slimCover(osuBeatmap.getBeatmapset().getCovers().getSlimcover())
                                    .build()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException("Beatmap not found"));
            return this.save(beatmap);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Beatmap not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Beatmap updateStatsForModification(Beatmap beatmap, Modification modification, User user) {
        boolean doStatsExist = beatmap.getStats().stream()
                .anyMatch(stats -> stats.getModification().equals(modification));
        if (doStatsExist) return beatmap;
        boolean doNoModStatsExist = beatmap.getStats().stream()
                .anyMatch(stats -> stats.getModification().equals(Modification.NM));
        if (!doNoModStatsExist) {
            try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
                Stats noModStats = osuApiClient.getBeatmap(beatmap.getBeatmapId())
                        .map(osuBeatmap ->
                                Stats.builder()
                                    .ar(osuBeatmap.getAr())
                                    .cs(osuBeatmap.getCs())
                                    .accuracy(osuBeatmap.getAccuracy())
                                    .hp(osuBeatmap.getDrain())
                                    .maxCombo(osuBeatmap.getMax_combo().longValue())
                                    .length(osuBeatmap.getTotal_length().intValue())
                                    .stars(osuBeatmap.getDifficulty_rating())
                                    .modification(Modification.NM)
                                    .build())
                        .orElseThrow(() -> new ResourceNotFoundException("Beatmap not found"));
                beatmap.getStats().add(noModStats);
            } catch (Exception e) {
                throw new BadRequestException("Could not connect to osu! API");
            }
        }
        boolean doRequestedModExists = beatmap.getStats().stream()
                .anyMatch(stats -> stats.getModification().equals(modification));
        if (doRequestedModExists) return beatmap;
        Stats modStats = BeatmapMultiplier.builder()
                .user(user)
                .beatmapId(beatmap.getBeatmapId())
                .beatmap(beatmap)
                .modification(modification)
                .build()
                .calculateFromBeatmap();
        if (modStats != null) beatmap.getStats().add(modStats);
        return beatmap;
    }

    private List<Stats> createStatistics(
            Long beatmapId,
            OsuBeatmap osuBeatmap,
            Modification modification,
            User user
    ) {
        Stats noModStats = Stats.builder()
                .ar(osuBeatmap.getAr())
                .cs(osuBeatmap.getCs())
                .accuracy(osuBeatmap.getAccuracy())
                .hp(osuBeatmap.getDrain())
                .maxCombo(osuBeatmap.getMax_combo().longValue())
                .length(osuBeatmap.getTotal_length().intValue())
                .stars(osuBeatmap.getDifficulty_rating())
                .bpm(osuBeatmap.getBpm())
                .modification(Modification.NM)
                .build();
        if (noModStats.getModification().equals(modification)) return new ArrayList<>(List.of(noModStats));
        Stats modStats = BeatmapMultiplier.builder()
                .user(user)
                .beatmapId(beatmapId)
                .osuBeatmap(osuBeatmap)
                .modification(modification)
                .build()
                .calculate();
        return new ArrayList<>(Arrays.asList(noModStats, modStats));
    }
}
