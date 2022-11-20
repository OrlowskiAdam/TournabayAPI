package com.tournabay.api.model;

import lombok.Getter;

@Getter
public enum Stage {
    QUALIFIER(-1, 0),
    GROUP_STAGE(0, 0),
    RO128(1, 128),
    RO64(2, 64),
    RO32(3, 32),
    RO16(4, 16),
    QUARTERFINAL(5, 8),
    SEMIFINAL(6, 4),
    FINAL(7, 2),
    GRANDFINAL(8, 1);

    private final int converted;
    private final int value;

    Stage(int converted, int value) {
        this.converted = converted;
        this.value = value;
    }
}
