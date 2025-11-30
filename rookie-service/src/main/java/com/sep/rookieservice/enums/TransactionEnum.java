package com.sep.rookieservice.enums;

public enum TransactionEnum {
    NOT_PAID((byte) 0),
    PROCESSING((byte) 1),
    CANCELED((byte) 2),
    PAID((byte) 3);

    private final Byte status;

    TransactionEnum(Byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }
    public static TransactionEnum getByStatus(Byte status) {
        if (status == null) return null;
        for (TransactionEnum x : TransactionEnum.values()) {
            if (x.getStatus() == status) {
                return x;
            }
        }
        throw new IllegalArgumentException("No Transactions Enum with status " + status);
    }
}
