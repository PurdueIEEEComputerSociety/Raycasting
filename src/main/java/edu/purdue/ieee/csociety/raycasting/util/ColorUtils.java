package edu.purdue.ieee.csociety.raycasting.util;

public class ColorUtils {

    public static final int RED_OFFSET = 24;
    public static final int GREEN_OFFSET = 16;
    public static final int BLUE_OFFSET = 8;
    public static final int ALPHA_OFFSET = 0;
    public static final int BYTE_MASK = 0xFF;
    public static final int RED_MASK = BYTE_MASK << RED_OFFSET;
    public static final int GREEN_MASK = BYTE_MASK << GREEN_OFFSET;
    public static final int BLUE_MASK = BYTE_MASK << BLUE_OFFSET;
    public static final int ALPHA_MASK = BYTE_MASK << ALPHA_OFFSET;

    private ColorUtils() {}

    public static int color(int red, int green, int blue) {
        return color(red, green, blue, 0xFF);
    }

    public static int color(int red, int green, int blue, int alpha) {
        return (BYTE_MASK & red) << RED_OFFSET |
                (BYTE_MASK & green) << GREEN_OFFSET |
                (BYTE_MASK & blue) << BLUE_OFFSET |
                (BYTE_MASK & alpha) << ALPHA_OFFSET;
    }

    public static int gray(int brightness) {
        return gray(brightness, 0xFF);
    }

    public static int gray(int brightness, int alpha) {
        return color(brightness, brightness, brightness, alpha);
    }

    public static int setRed(int color, int red) {
        return color & ~RED_MASK | (BYTE_MASK & red) << RED_OFFSET;
    }

    public static int setGreen(int color, int green) {
        return color & ~GREEN_MASK | (BYTE_MASK & green) << GREEN_OFFSET;
    }

    public static int setBlue(int color, int blue) {
        return color & ~BLUE_MASK | (BYTE_MASK & blue) << BLUE_OFFSET;
    }

    public static int setAlpha(int color, int alpha) {
        return color & ~ALPHA_MASK | (BYTE_MASK & alpha) << ALPHA_OFFSET;
    }

    public static int getRed(int color) {
        return (color & RED_MASK) >> RED_OFFSET;
    }

    public static int getGreen(int color) {
        return (color & GREEN_MASK) >> GREEN_OFFSET;
    }

    public static int getBlue(int color) {
        return (color & BLUE_MASK) >> BLUE_OFFSET;
    }

    public static int getAlpha(int color) {
        return (color & ALPHA_MASK) >> ALPHA_OFFSET;
    }
}
