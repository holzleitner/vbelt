package at.tripwire.vbelt;

public enum VibrationPattern {

    DISTANCE_100M(100, new long[]{0, 300}),
    DISTANCE_50M(50, new long[]{0, 300, 200, 300}),
    DISTANCE_10M(10, new long[]{0, 300, 200, 300, 200, 300}),
    DISTANCE_1M(1, new long[]{0, 1000});

    private int distance;

    private long[] pattern;

    VibrationPattern(int distance, long[] pattern) {
        this.distance = distance;
        this.pattern = pattern;
    }

    public static long[] getPattern(int distance) {
        for (VibrationPattern p : values()) {
            if (distance == p.distance) {
                return p.pattern;
            }
        }
        return null;
    }
}
