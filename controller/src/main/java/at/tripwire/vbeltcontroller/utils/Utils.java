package at.tripwire.vbeltcontroller.utils;

public final class Utils {

    private Utils() {
        // prevent initialization
    }

    public static int normalize(double distance) {
        if (distance > 100) {
            return -1;
        } else if (distance <= 100 && distance > 50) {
            return 100;
        } else if (distance <= 50 && distance > 10) {
            return 50;
        } else if (distance <= 10 && distance > 1) {
            return 10;
        }
        return 1;
    }
}
