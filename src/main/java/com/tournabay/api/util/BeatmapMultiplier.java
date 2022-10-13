package com.tournabay.api.util;

import com.tournabay.api.model.Modification;
import com.tournabay.api.model.User;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.model.beatmap.Stats;
import com.tournabay.api.osu.OsuApiClient;
import com.tournabay.api.osu.model.BeatmapAttributesBody;
import com.tournabay.api.osu.model.BeatmapDifficultyAttributes;
import com.tournabay.api.osu.model.BeatmapDifficultyAttributesWrapper;
import com.tournabay.api.osu.model.OsuBeatmap;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All calculations were made by IceDynamix
 * Profile page: <a href="https://osu.ppy.sh/users/8599070">https://osu.ppy.sh/users/8599070</a>
 * Twitter: <a href="https://twitter.com/IceDynamix">https://twitter.com/IceDynamix</a>
 */
@AllArgsConstructor
@Builder
public class BeatmapMultiplier {

    private Long beatmapId;
    private OsuBeatmap osuBeatmap;
    private Beatmap beatmap;

    private final Modification modification;
    private final User user;
    private final List<Modification> allowedModifications = new ArrayList<>(Arrays.asList(
            Modification.NM, Modification.HR, Modification.DT, Modification.EZ, Modification.HT)
    );

    public Stats calculate() {
        try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
//            if (!allowedModifications.contains(modification)) return null;
            BeatmapDifficultyAttributesWrapper beatmapAttributes = osuApiClient.getBeatmapAttributes(beatmapId, new BeatmapAttributesBody(modification.getBit()))
                    .orElseThrow(() -> new Exception("Beatmap not found"));
            return Stats.builder()
                    .ar(getMultipliedApproachRate(osuBeatmap.getAr()))
                    .cs(getMultipliedCircleSize(osuBeatmap.getCs()))
                    .accuracy(getMultipliedOverallDifficulty(osuBeatmap.getAccuracy()))
                    .hp(getMultipliedHealthDrain(osuBeatmap.getDrain()))
                    .bpm(getMultipliedBpm(osuBeatmap.getBpm()))
                    .maxCombo(osuBeatmap.getMax_combo().longValue())
                    .length(getMultipliedLength(osuBeatmap.getTotal_length().intValue()))
                    .stars(beatmapAttributes.getAttributes().getStar_rating())
                    .modification(modification)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to osu! API");
        }
    }

    public Stats calculateFromBeatmap() {
        if (beatmap == null) throw new RuntimeException("Beatmap not found");
        Stats nm = beatmap.getStats().stream()
                .filter(stats -> stats.getModification() == Modification.NM)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("NM stats not found"));
        try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
            if (!allowedModifications.contains(modification)) return null;
            BeatmapDifficultyAttributesWrapper beatmapAttributes = osuApiClient.getBeatmapAttributes(beatmapId, new BeatmapAttributesBody(modification.getBit()))
                    .orElseThrow(() -> new RuntimeException("Beatmap not found"));
            return Stats.builder()
                    .ar(getMultipliedApproachRate(nm.getAr()))
                    .cs(getMultipliedCircleSize(nm.getCs()))
                    .accuracy(getMultipliedOverallDifficulty(nm.getAccuracy()))
                    .hp(getMultipliedHealthDrain(nm.getHp()))
                    .bpm(getMultipliedBpm(nm.getBpm()))
                    .maxCombo(nm.getMaxCombo())
                    .length(getMultipliedLength(nm.getLength()))
                    .stars(beatmapAttributes.getAttributes().getStar_rating())
                    .modification(modification)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to osu! API");
        }
    }

    public int getMultipliedLength(int length) {
        switch (modification) {
            case DT:
                return (int) (length * 0.75);
            case HT:
                return (int) (length * 1.5);
            default:
                return length;
        }
    }

    public float getMultipliedBpm(float bpm) {
        switch (modification) {
            case DT:
                return (float) Math.ceil(bpm * 0.75f);
            case HT:
                return (float) Math.ceil(bpm * 1.5f);
            default:
                return bpm;
        }
    }

    public float getMultipliedCircleSize(float cs) {
        if (modification.equals(Modification.HR)) {
            double value = cs * 1.3;
            if (value > 10) {
                value = 10;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.EZ)) {
            double value = cs * 0.5;
            if (value < 2) {
                value = 2;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        }
        return cs;
    }

    public float getMultipliedApproachRate(float ar) {
        if (modification.equals(Modification.DT)) {
            double approachTime = calculateDifficultyRange(ar, 1800, 1200, 450);
            return calculateDifficultyRangeInverse((float) (approachTime * (1.0 / 1.5)), 1800, 1200, 450);
        } else if (modification.equals(Modification.HR)) {
            double value = ar * 1.4;
            if (value > 10) {
                value = 10;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.EZ)) {
            double value = ar * 0.5;
            if (value < 0) {
                value = 0;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.HT)) {
            double approachTime = calculateDifficultyRange(ar, 1800, 1200, 450);
            return calculateDifficultyRangeInverse((float) (approachTime * 1.5), 1800, 1200, 450);
        }
        return ar;
    }

    public float getMultipliedHealthDrain(float hp) {
        if (modification.equals(Modification.HR)) {
            double value = hp * 1.4;
            if (value > 10) {
                value = 10;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.EZ)) {
            double value = hp * 0.5;
            if (value < 0) {
                value = 0;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        }
        return hp;
    }

    public float getMultipliedOverallDifficulty(float od) {
        if (modification.equals(Modification.DT)) {
            double drainRate = calculateDifficultyRange(od, 80, 50, 20);
            return calculateDifficultyRangeInverse((float) (drainRate * (1.0 / 1.5)), 80, 50, 20);
        } else if (modification.equals(Modification.HR)) {
            double value = od * 1.4;
            if (value > 10) {
                value = 10;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.EZ)) {
            double value = od * 0.5;
            if (value < 0) {
                value = 0;
            }
            return (float) ((float) Math.round(value * 100.0) / 100.0);
        } else if (modification.equals(Modification.HT)) {
            double drainRate = calculateDifficultyRange(od, 80, 50, 20);
            return calculateDifficultyRangeInverse((float) (drainRate * 1.5), 80, 50, 20);
        }
        return od;
    }


    private static float calculateDifficultyRange(float difficulty, int minimum, int middle, int maximum) {
        if (difficulty > 5.0) {
            return (float) (middle + (maximum - middle) * (difficulty - 5.0) / 5.0);
        }

        if (difficulty < 5.0) {
            return (float) (middle - (middle - minimum) * (5.0 - difficulty) / 5.0);
        }

        return middle;
    }

    private static float calculateDifficultyRangeInverse(float difficulty, int minimum, int middle, int maximum) {
        if (difficulty < middle) {
            return (float) ((difficulty * 5.0 - middle * 5.0) / (maximum - middle) + 5.0);
        }

        if (difficulty > middle) {
            return (float) (5.0 - (middle * 5.0 - difficulty * 5.0) / (middle - minimum));
        }

        return middle;
    }
}
