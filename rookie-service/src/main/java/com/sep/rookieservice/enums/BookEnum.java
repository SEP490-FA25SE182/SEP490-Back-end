package com.sep.rookieservice.enums;

public enum BookEnum {
    IN_PROGRESS((byte) 0),   // Sách đang viết
    COMPLETED((byte) 1),     // Sách đã hoàn thành
    DROPPED((byte) 2);       // Sách bị drop

    private final byte status;

    BookEnum(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static BookEnum getByStatus(byte status) {
        for (BookEnum bookEnum : BookEnum.values()) {
            if (bookEnum.getStatus() == status) {
                return bookEnum;
            }
        }
        throw new IllegalArgumentException("Invalid book status");
    }
}
