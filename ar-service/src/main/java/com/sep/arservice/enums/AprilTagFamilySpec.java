package com.sep.arservice.enums;

import java.util.Locale;

public enum AprilTagFamilySpec {

    // Folder: tag36h11
    // Filename: tag36_11_00007.png (5 digits, zero-padded)
    TAG36H11("tag36h11", "tag36_11_%05d.png", 586);

    private final String folder;
    private final String filePattern;
    private final int maxId;

    AprilTagFamilySpec(String folder, String filePattern, int maxId) {
        this.folder = folder;
        this.filePattern = filePattern;
        this.maxId = maxId;
    }

    public String folder() { return folder; }
    public String fileName(int tagId) { return String.format(Locale.US, filePattern, tagId); }
    public int maxId() { return maxId; }

    public static AprilTagFamilySpec from(String family) {
        if (family == null) return TAG36H11;
        String f = family.trim().toLowerCase(Locale.ROOT);
        return switch (f) {
            case "tag36h11" -> TAG36H11;
            default -> throw new IllegalArgumentException("Unsupported tagFamily: " + family);
        };
    }
}

