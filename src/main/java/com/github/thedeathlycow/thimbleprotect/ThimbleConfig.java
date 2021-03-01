package com.github.thedeathlycow.thimbleprotect;

public class ThimbleConfig {

    public final boolean blockBreak;
    public final boolean blockPlace;
    public final boolean explosion;

    /**
     * Loads a new Thimble Config with custom parameters.
     *
     * @param logBlockBreak
     * @param logBlockPlace
     * @param logExplosion
     */
    public ThimbleConfig(boolean logBlockBreak, boolean logBlockPlace, boolean logExplosion) {
        this.blockBreak = logBlockBreak;
        this.blockPlace = logBlockPlace;
        this.explosion = logExplosion;
    }

    public static ThimbleConfig createDefaultConfig() {
        return new ThimbleConfig(true, true, true);
    }

    public String toString() {
        return "blockBreak:{" + this.blockBreak + "},blockPlace:{" + this.blockPlace + "},explosion:{" + this.explosion + "}";
    }

}
