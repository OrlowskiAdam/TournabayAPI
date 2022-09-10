package com.tournabay.api.model;

public enum Stage {
    QUALIFIER(-1),
    GROUP_STAGE(0),
    RO128(1),
    RO64(2),
    RO32(3),
    RO16(4),
    QUARTERFINAL(5),
    SEMIFINAL(6),
    FINAL(7),
    GRANDFINAL(8);

    private final int converted;

    Stage(int converted) {
        this.converted = converted;
    }
}
