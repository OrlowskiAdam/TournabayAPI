package com.tournabay.api.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateTeamBasedQualificationRoomRequest.class, name = "team"),
        @JsonSubTypes.Type(value = UpdatePlayerBasedQualificationRoomRequest.class, name = "player")
})
public class UpdateQualificationRoomRequest {
    private LocalDateTime startDate;
    private List<Long> staffMemberIds;
}
