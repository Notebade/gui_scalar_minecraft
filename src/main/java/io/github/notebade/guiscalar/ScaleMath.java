package io.github.notebade.guiscalar;

public final class ScaleMath {
    public static final double MIN = 1.0;
    public static final double MAX = 16.0;

    private ScaleMath() {}

    public static double round(double scale) {
        if (!Double.isFinite(scale)) return MIN;
        return Math.round(Math.clamp(scale, MIN, MAX) * 100.0) / 100.0;
    }

    public static double maximum(int width, int height) {
        return Math.clamp(Math.min(width / 320.0, height / 240.0), MIN, MAX);
    }

    public static double effective(double requested, int width, int height) {
        return Math.min(round(requested), maximum(width, height));
    }
}
