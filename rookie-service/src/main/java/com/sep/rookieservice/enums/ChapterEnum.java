package com.sep.rookieservice.enums;

public enum ChapterEnum {
    IN_REVIEW((byte) 0),
    REJECTED((byte) 1),
    APPROVED((byte) 2);

    private final byte status;

    ChapterEnum(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static ChapterEnum getByStatus(byte status) {
        for (ChapterEnum chapterEnum : ChapterEnum.values()) {
            if (chapterEnum.getStatus() == status) {
                return chapterEnum;
            }
        }
        throw new IllegalArgumentException("Invalid chapter status");
    }
}
