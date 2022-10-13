package com.tournabay.api.controller;

import com.tournabay.api.model.*;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.payload.AddBeatmapToMappool;
import com.tournabay.api.payload.CreateMappoolRequest;
import com.tournabay.api.payload.ReorderBeatmapRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mappool")
public class MappoolController {
    private final TournamentService tournamentService;
    private final MappoolService mappoolService;
    private final BeatmapService beatmapService;
    private final BeatmapModificationService beatmapModificationService;
    private final UserService userService;

    /**
     * Get the mappool with the given id from the tournament with the given id.
     *
     * @param mappoolId    The ID of the mappool you want to get.
     * @param tournamentId The id of the tournament that the mappool belongs to.
     * @return A mappool object
     */
    @GetMapping("/{mappoolId}/{tournamentId}")
    public ResponseEntity<Mappool> getMappool(@PathVariable Long mappoolId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        return ResponseEntity.ok(mappool);
    }

    /**
     * Return a list of all mappools for a given tournament
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId  The id of the tournament you want to get the mappools from.
     * @return A list of mappools
     */
    @GetMapping("/{tournamentId}")
    public ResponseEntity<List<Mappool>> getTournamentMappools(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Mappool> tournamentMappools = mappoolService.findAllByTournament(tournament);
        return ResponseEntity.ok(tournamentMappools);
    }

    /**
     * Create a mappool for a tournament.
     *
     * @param userPrincipal The user who is currently logged in.
     * @param tournamentId  The id of the tournament the mappool is being created for
     * @param body          The request body, which is a JSON object containing the stage and name of the mappool.
     * @return A ResponseEntity object is being returned.
     */
    @PostMapping("/create/{tournamentId}")
    public ResponseEntity<Mappool> createMappool(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreateMappoolRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.createMappool(tournament, body.getStage(), body.getName());
        return ResponseEntity.ok(mappool);
    }

    /**
     * Add a beatmap to a mappool
     *
     * @param userPrincipal The user that is currently logged in.
     * @param mappoolId     The id of the mappool you want to add the beatmap to
     * @param tournamentId  The id of the tournament that the mappool belongs to
     * @param body          The body of the request, which is a JSON object containing the beatmap URL and the beatmap modification.
     * @return A ResponseEntity with the updated mappool.
     */
    @PostMapping("/{mappoolId}/{tournamentId}/beatmap/add")
    public ResponseEntity<Mappool> addBeatmap(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId, @RequestBody AddBeatmapToMappool body) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        BeatmapModification beatmapModification = beatmapModificationService.findByModification(body.getModification(), mappool);
        Long beatmapId = beatmapService.extractBeatmapId(body.getBeatmapUrl(), tournament.getGameMode());
        Beatmap beatmap = beatmapService.addBeatmap(beatmapId, beatmapModification, mappool, user);
        BeatmapModification updatedBeatmapModification = beatmapModificationService.addBeatmapToModification(beatmapModification, beatmap);
        Mappool updatedMappool = mappoolService.replaceBeatmapModification(mappool, updatedBeatmapModification);
        return ResponseEntity.ok(updatedMappool);
    }

    /**
     * Delete a mappool from a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param mappoolId     The id of the mappool you want to delete
     * @param tournamentId  The id of the tournament that the mappool belongs to.
     * @return A ResponseEntity object is being returned.
     */
    @DeleteMapping("/{mappoolId}/{tournamentId}/delete")
    public ResponseEntity<Mappool> deleteMappool(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        mappoolService.deleteMappool(mappool);
        return ResponseEntity.ok(mappool);
    }

    /**
     * Delete a beatmap from a mappool.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param mappoolId     The ID of the mappool you want to delete the beatmap from
     * @param tournamentId  The id of the tournament that the mappool is in
     * @param beatmapId     The id of the beatmap to be deleted
     * @return A ResponseEntity with the mappool.
     */
    @DeleteMapping("/{mappoolId}/{tournamentId}/beatmap/{beatmapId}/delete")
    public ResponseEntity<Mappool> deleteBeatmap(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId, @PathVariable Long beatmapId) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        Beatmap beatmap = beatmapService.findById(beatmapId);
        BeatmapModification beatmapModification = beatmapModificationService.findByBeatmap(beatmap, mappool);
        beatmapModificationService.removeBeatmapFromModification(beatmapModification, beatmap);
        beatmapService.delete(beatmap);
        return ResponseEntity.ok(mappool);
    }

    /**
     * Reorder the beatmaps in a mappool.
     *
     * @param userPrincipal The user who is making the request.
     * @param mappoolId     The ID of the mappool you want to reorder
     * @param tournamentId  The ID of the tournament that the mappool belongs to.
     * @param body          The request body, which is a JSON object with the following properties:
     * @return A ResponseEntity with the updated mappool.
     */
    @PostMapping("/{mappoolId}/{tournamentId}/beatmap-reorder")
    public ResponseEntity<Mappool> reorderBeatmap(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId, @RequestBody ReorderBeatmapRequest body) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        Mappool updatedMappool = mappoolService.reorderBeatmap(mappool, body.getSourceId(), body.getDestinationId(), body.getFromIndex(), body.getToIndex(), user);
        return ResponseEntity.ok(updatedMappool);
    }

    /**
     * Release the mappool for the tournament with the given id.
     *
     * @param userPrincipal The user who is currently logged in.
     * @param mappoolId     The id of the mappool you want to release
     * @param tournamentId  The id of the tournament that the mappool is associated with
     * @return A ResponseEntity with the updated mappool.
     */
    @PutMapping("/{mappoolId}/{tournamentId}/publish")
    public ResponseEntity<Mappool> publishMappool(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        Mappool updatedMappool = mappoolService.releaseMappool(mappool);
        return ResponseEntity.ok(updatedMappool);
    }

    /**
     * Conceal the mappool for the tournament
     *
     * @param userPrincipal The user who is currently logged in.
     * @param mappoolId     The ID of the mappool you want to conceal.
     * @param tournamentId  The id of the tournament that the mappool belongs to
     * @return A ResponseEntity with the updated mappool.
     */
    @PutMapping("/{mappoolId}/{tournamentId}/conceal")
    public ResponseEntity<Mappool> concealMappool(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long mappoolId, @PathVariable Long tournamentId) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.findById(tournament, mappoolId);
        Mappool updatedMappool = mappoolService.concealMappool(mappool);
        return ResponseEntity.ok(updatedMappool);
    }
}
