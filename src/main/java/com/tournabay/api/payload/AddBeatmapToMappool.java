package com.tournabay.api.payload;

import com.tournabay.api.model.Modification;
import lombok.Getter;

@Getter
public class AddBeatmapToMappool {
    private String beatmapUrl;
    private Modification modification;
}
