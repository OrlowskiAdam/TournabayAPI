package com.tournabay.api.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum Modification {
    NM("NoMod", 0),
    HD("Hidden", 8),
    HR("HardRock", 16),
    DT("DoubleTime", 64),
    FM("FreeMod", null),
    EZ("Easy", 2),
    HT("HalfTime", 256),
    FL("Flashlight", 1024),
    TB("TieBreaker", null);

    private final String converted;
    private final Integer bit;

    Modification(String converted, Integer bit) {
        this.converted = converted;
        this.bit = bit;
    }
}
