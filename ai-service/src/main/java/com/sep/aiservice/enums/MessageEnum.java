package com.sep.aiservice.enums;

public enum MessageEnum {
    SENDING((byte) 0),
    SENT((byte) 1),
    FAILED((byte) 2);

    private final byte status;

    MessageEnum(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static MessageEnum getByStatus(byte status) {
        for (MessageEnum messageEnum : MessageEnum.values()) {
            if (messageEnum.getStatus() == status) {
                return messageEnum;
            }
        }
        throw new IllegalArgumentException("Invalid message status");
    }
}
