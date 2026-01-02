package com.sep.arservice.enums;

import java.util.Locale;

public enum AprilTagFamilySpec {

    // https://raw.githubusercontent.com/AprilRobotics/apriltag-imgs/master/tagStandard41h12/tag41_12_00000.png
    TAG_STANDARD_41H12("tagStandard41h12", "tag41_12_%05d.png", 2114);

    private final String folder;
    private final String filePattern;
    private final int maxId;

    AprilTagFamilySpec(String folder, String filePattern, int maxId) {
        this.folder = folder;
        this.filePattern = filePattern;
        this.maxId = maxId;
    }

    public String folder() { return folder; }

    public String fileName(int tagId) {
        return String.format(Locale.US, filePattern, tagId);
    }

    public int maxId() { return maxId; }

    /**
     * Backend CHỈ hỗ trợ tagStandard41h12.
     * - null/blank -> default tagStandard41h12
     * - mọi giá trị khác -> throw
     */
    public static AprilTagFamilySpec from(String family) {
        if (family == null || family.isBlank()) return TAG_STANDARD_41H12;

        String f = family.trim().toLowerCase(Locale.ROOT);
        if (f.equals("tagstandard41h12")) return TAG_STANDARD_41H12;

        throw new IllegalArgumentException("Unsupported tagFamily (only tagStandard41h12 is supported): " + family);
    }

    public static String canonicalFamily() {
        return TAG_STANDARD_41H12.folder(); // "tagStandard41h12"
    }
}
