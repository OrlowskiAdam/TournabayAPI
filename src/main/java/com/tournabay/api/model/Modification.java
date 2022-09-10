package com.tournabay.api.model;

import lombok.ToString;

@ToString
public enum Modification {
    FM("FreeMod"),
    HD("Hidden"),
    HR("HardRock"),
    DT("Double Time"),
    NM("No Mod"),
    TB("Tie Breaker");

    private final String converted;

    Modification(String converted) {
        this.converted = converted;
    }
}
