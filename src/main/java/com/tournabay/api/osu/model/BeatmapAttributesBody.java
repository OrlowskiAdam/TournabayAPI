package com.tournabay.api.osu.model;

import lombok.Setter;

@Setter
public class BeatmapAttributesBody {
    private String mods;

    public BeatmapAttributesBody(Integer mods) {
        this.mods = mods.toString();
    }
}
