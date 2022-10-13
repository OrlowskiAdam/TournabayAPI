package com.tournabay.api.controller;

import com.tournabay.api.model.*;
import com.tournabay.api.payload.AddBeatmapToMappool;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beatmap")
public class BeatmapController {
    private final TournamentService tournamentService;
    private final BeatmapService beatmapService;
    private final MappoolService mappoolService;
    private final UserService userService;
    private final BeatmapModificationService beatmapModificationService;

//    @PostMapping("/create")
//    public ResponseEntity<List<BeatmapModification>> createBeatmap(@CurrentUser UserPrincipal userPrincipal, @RequestBody AddBeatmapToMappool body) {
//        Tournament tournament = tournamentService.getTournamentById(body.getTournamentId());
//        User user = userService.getUserFromPrincipal(userPrincipal);
//        Mappool mappool = mappoolService.findById(tournament, body.getMappoolId());
//        BeatmapModification beatmapModification = beatmapModificationService.findByModificationAndMappool(body.getModification(), mappool);
//        Long beatmapId = beatmapService.extractBeatmapId(body.getBeatmapUrl(), tournament.getGameMode());
//        Beatmap beatmap = beatmapService.addBeatmap(beatmapId, beatmapModification, user);
//        Mappool updatedMappool = mappoolService.addBeatmapToMappool(mappool, beatmap, beatmapModification);
//        return ResponseEntity.ok(updatedMappool.getBeatmapModifications());
//    }
}
