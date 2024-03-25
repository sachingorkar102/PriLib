package com.github.sachin.prilib;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

// Class from Jefflib https://github.com/mfnalex/JeffLib/blob/master/core/src/main/java/com/jeff_media/jefflib/data/McVersion.java
public class McVersion implements Comparable<McVersion> {

    private static final McVersion CURRENT_VERSION;
    private static final McVersion v1_17 = new McVersion(1, 17);

    static {
        final int currentMajor = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[0]);
        final int currentMinor = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].split("-")[0]);
        boolean hasPatch = countChar(Bukkit.getBukkitVersion(), '.') == 3;
        final int currentPatch = hasPatch ? Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[2].split("-")[0]) : 0;

        CURRENT_VERSION = new McVersion(currentMajor, currentMinor, currentPatch);
    }

    private final int major;
    private final int minor;
    private final int patch;

    public McVersion(final int major, final int minor) {
        this(major, minor, 0);
    }

    public McVersion(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Gets the currently running McVersion
     */
    public static McVersion current() {
        return CURRENT_VERSION;
    }

    public boolean hasVersionInNmsPackageName() {
        return !this.isAtLeast(v1_17);
    }

    public boolean isAtLeast(final McVersion other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(@NotNull final McVersion other) {
        if (this.major > other.major) return 3;
        if (other.major > this.major) return -3;
        if (this.minor > other.minor) return 2;
        if (other.minor > this.minor) return -2;
        return Integer.compare(this.patch, other.patch);
    }

    /**
     * Gets the "major" part of this McVersion. For 1.16.5, this would be 1
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the "minor" part of this McVersion. For 1.16.5, this would be 16
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the "patch" part of this McVersion. For 1.16.5, this would be 5.
     */
    public int getPatch() {
        return patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final McVersion mcVersion = (McVersion) o;
        return major == mcVersion.major && minor == mcVersion.minor && patch == mcVersion.patch;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        if (patch == 0) {
            return major + "." + minor;
        } else {
            return major + "." + minor + "." + patch;
        }
    }

    public boolean isAtLeast(final int major, final int minor, final int patch) {
        return this.isAtLeast(new McVersion(major, minor, patch));
    }

    public boolean isAtLeast(final int major, final int minor) {
        return this.isAtLeast(new McVersion(major, minor));
    }

    private static int countChar(final String string, final char aChar) {
        int count = 0;
        char[] arr = string.toCharArray();
        for (int i = 0; i < string.length(); i++) {
            if (arr[i] == aChar) {
                count++;
            }
        }
        return count;
    }

}
