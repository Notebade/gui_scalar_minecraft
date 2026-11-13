package io.github.notebade.guiscalar;

public final class ScaleMathTest {
    public static void main(String[] args) {
        expect(3.56, ScaleMath.round(3.555));
        expect(1.0, ScaleMath.round(-20));
        expect(16.0, ScaleMath.round(100));
        expect(1.0, ScaleMath.round(Double.NaN));
        expect(3.56, ScaleMath.effective(3.56, 1920, 1080));
        expect(4.5, ScaleMath.effective(8, 1920, 1080));
        expect(1, ScaleMath.effective(8, 200, 100));
        System.out.println("Scale boundaries and fractional scaling passed");
    }
    private static void expect(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.000001) {
            throw new AssertionError(expected + " != " + actual);
        }
    }
}
