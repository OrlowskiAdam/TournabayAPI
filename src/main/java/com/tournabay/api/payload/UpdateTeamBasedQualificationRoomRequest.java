package com.tournabay.api.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateTeamBasedQualificationRoomRequest extends UpdateQualificationRoomRequest {
    private List<Long> teamIds;
}
