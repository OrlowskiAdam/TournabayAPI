package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class ReorderBeatmapRequest {
    private Long sourceId;
    private Long destinationId;
    private Long beatmapId;
    private Integer fromIndex;
    private Integer toIndex;
}
