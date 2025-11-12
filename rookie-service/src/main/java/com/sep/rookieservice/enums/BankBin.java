package com.sep.rookieservice.enums;

import java.util.Arrays;

public enum BankBin {
    TPBANK("TPbank", "tp bank", "tien phong", "tienphong", "tpb", "970423"),
    VIETINBANK("Vietinbank", "vietin", "ctg", "970415"),
    VIETCOMBANK("Vietcombank", "vcb", "970436"),
    BIDV("bidv", "970418"),
    ACB("acb", "asia commercial bank", "970416"),
    MBBANK("MBbank", "mb bank", "mb", "970422"),
    SACOMBANK("Sacombank", "stk sacom", "970403"),
    TECHCOMBANK("Techcombank", "tcb", "970407"),
    AGRIBANK("Agribank", "vbard", "970405");

    private final String[] aliases;
    private final String bin;

    BankBin(String... aliasesAndBin) {
        this.bin = aliasesAndBin[aliasesAndBin.length - 1];
        this.aliases = Arrays.copyOf(aliasesAndBin, aliasesAndBin.length - 1);
    }

    public String getBin() {
        return bin;
    }

    /** Tìm theo tên ngân hàng */
    public static String resolveBin(String bankName) {
        if (bankName == null || bankName.isBlank()) return "";
        String normalized = bankName.trim().toLowerCase();
        for (BankBin b : values()) {
            for (String a : b.aliases) {
                if (normalized.contains(a)) return b.bin;
            }
        }
        return "";
    }
}