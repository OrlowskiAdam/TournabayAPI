package com.tournabay.api.util;

/**
 * All calculations were made by IceDynamix
 * Profile page: <a href="https://osu.ppy.sh/users/8599070">https://osu.ppy.sh/users/8599070</a>
 * Twitter: <a href="https://twitter.com/IceDynamix">https://twitter.com/IceDynamix</a>
 */
public class BeatmapMultiplier {

    public static double getMultipliedCircleSize(double cs, String modifier) {
        if (modifier.equalsIgnoreCase("HR")) {
            double value = cs * 1.3;
            if (value > 10) {
                value = 10;
            }
            return value;
        }
        return cs;
    }

    public static double getMultipliedApproachRate(double ar, String modifier) {
        if (modifier.equalsIgnoreCase("DT")) {
            double approachTime = calculateDifficultyRange(ar, 1800, 1200, 450);
            return calculateDifficultyRangeInverse(approachTime * (1.0 / 1.5), 1800, 1200, 450);
        } else if (modifier.equalsIgnoreCase("HR")) {
            double value = ar * 1.4;
            if (value > 10) {
                value = 10;
            }
            return value;
        }
        return ar;
    }

    public static double getMultipliedHealthDrain(double hp, String modifier) {
        if (modifier.equalsIgnoreCase("HR")) {
            double value = hp * 1.4;
            if (value > 10) {
                value = 10;
            }
            return value;
        }
        return hp;
    }

    public static double getMultipliedOverallDifficulty(double od, String modifier) {
        if (modifier.equalsIgnoreCase("DT")) {
            double drainRate = calculateDifficultyRange(od, 80, 50, 20);
            return calculateDifficultyRangeInverse(drainRate * (1.0 / 1.5), 80, 50, 20);
        } else if (modifier.equalsIgnoreCase("HR")) {
            double value = od * 1.4;
            if (value > 10) {
                value = 10;
            }
            return value;
        }
        return od;
    }



    private static double calculateDifficultyRange(double difficulty, int minimum, int middle, int maximum) {
        if (difficulty > 5.0) {
            return middle + (maximum - middle) * (difficulty - 5.0) / 5.0;
        }

        if (difficulty < 5.0) {
            return middle - (middle - minimum) * (5.0 - difficulty) / 5.0;
        }

        return middle;
    }

    private static double calculateDifficultyRangeInverse(double difficulty, int minimum, int middle, int maximum) {
        if (difficulty < middle) {
            return (difficulty * 5.0 - middle * 5.0) / (maximum - middle) + 5.0;
        }

        if (difficulty > middle) {
            return 5.0 - (middle * 5.0 - difficulty * 5.0) / (middle - minimum);
        }

        return middle;
    }
}
