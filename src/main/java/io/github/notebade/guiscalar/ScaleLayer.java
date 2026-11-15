package io.github.notebade.guiscalar;

public enum ScaleLayer {
    GLOBAL("scale"), CONTAINER("containerScale"), HOTBAR("hotbarScale");

    public final String key;

    ScaleLayer(String key) { this.key = key; }

    public ScaleLayer next() { return values()[(ordinal() + 1) % values().length]; }
}
