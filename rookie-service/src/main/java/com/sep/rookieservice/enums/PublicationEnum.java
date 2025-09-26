package com.sep.rookieservice.enums;

public enum PublicationEnum {
    DRAFT((byte) 0),
    PUBLISHED((byte) 1),
    ARCHIVED((byte) 2),
    PENDING((byte) 3);

    private final byte status;

    PublicationEnum(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static PublicationEnum getByStatus(byte status) {
        for (PublicationEnum publicationEnum : PublicationEnum.values()) {
            if (publicationEnum.getStatus() == status) {
                return publicationEnum;
            }
        }
        throw new IllegalArgumentException("Invalid publication status");
    }
}
