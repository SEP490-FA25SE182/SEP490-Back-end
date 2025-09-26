package com.sep.aiservice.enums;

public enum AIGenerationEnum {
    PENDING((byte) 0),
    SUCCESS((byte) 1),
    FAILED((byte) 2);

    private final byte status;

    AIGenerationEnum(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static AIGenerationEnum getByStatus(byte status) {
        for (AIGenerationEnum aiGenerationEnum : AIGenerationEnum.values()) {
            if (aiGenerationEnum.getStatus() == status) {
                return aiGenerationEnum;
            }
        }
        throw new IllegalArgumentException("Invalid aiGeneration status");
    }
}
