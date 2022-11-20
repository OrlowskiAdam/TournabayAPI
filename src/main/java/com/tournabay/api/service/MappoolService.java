package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.repository.MappoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MappoolService {
    private final BeatmapModificationService beatmapModificationService;
    private final MappoolRepository mappoolRepository;
    private final BeatmapService beatmapService;

    /**
     * Find all mappools that are associated with a specific tournament.
     *
     * @param tournament The tournament you want to find the mappools for.
     * @return A list of mappools
     */
    public List<Mappool> findAllByTournament(Tournament tournament) {
        return mappoolRepository.findAllByTournamentOrderByIdAsc(tournament);
    }

    /**
     * Find a mappool by id and tournament, or throw an exception if it doesn't exist.
     *
     * @param tournament The tournament object that the mappool belongs to.
     * @param mappoolId  The id of the mappool you want to find
     * @return A Mappool object
     */
    public Mappool findById(Tournament tournament, Long mappoolId) {
        Mappool mappool = mappoolRepository.findById(mappoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Mappool not found"));
        if (!mappool.getTournament().getId().equals(tournament.getId()))
            throw new BadRequestException("Mappool does not belong to this tournament");
        return mappool;
    }

    /**
     * Find all mappools for a tournament, filter them by stage, and return the first one that matches, or throw an
     * exception if none are found.
     *
     * @param tournament The tournament you want to find the mappool for.
     * @param stage The stage of the tournament.
     * @return A Mappool object
     */
    public Mappool findByStage(Tournament tournament, Stage stage) {
        return this.findAllByTournament(tournament)
                .stream()
                .filter(mappool -> mappool.getStage().equals(stage))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Mappool for " + stage.name() + " not found"));
    }

    /**
     * Find a mappool by its id, or throw an exception if it doesn't exist.
     *
     * @param mappoolId The id of the mappool you want to find.
     * @return A mappool object
     */
    public Mappool findById(Long mappoolId) {
        return mappoolRepository.findById(mappoolId)
                .orElseThrow(() -> new RuntimeException("Mappool not found"));
    }

    /**
     * Save the mappool object to the database.
     *
     * @param mappool The mappool object that is being saved.
     * @return The mappool object that was saved.
     */
    public Mappool save(Mappool mappool) {
        return mappoolRepository.save(mappool);
    }

    /**
     * Replace a beatmap modification in a mappool.
     *
     * @param mappool             The mappool you want to add the beatmap to.
     * @param beatmapModification The beatmap modification to be replaced.
     * @return The mappool object with the updated beatmapModifications list.
     */
    public Mappool replaceBeatmapModification(Mappool mappool, BeatmapModification beatmapModification) {
        mappool.getBeatmapModifications().set(beatmapModification.getPosition(), beatmapModification);
        return mappoolRepository.save(mappool);
    }

    /**
     * Create a mappool with the given tournament, stage, and name, and save it to the database.
     *
     * @param tournament The tournament that the mappool belongs to.
     * @param stage      The stage of the tournament.
     * @param name       The name of the mappool.
     * @return A Mappool object
     */
    @Transactional
    public Mappool createMappool(Tournament tournament, Stage stage, String name) {
        Mappool mappool = Mappool.builder()
                .tournament(tournament)
                .stage(stage)
                .isReleased(false)
                .beatmapModifications(beatmapModificationService.createDefaultBeatmapModifications())
                .name(name)
                .build();
        mappool.getBeatmapModifications().forEach(bm -> bm.setMappool(mappool));
        return mappoolRepository.save(mappool);
    }

    @Transactional
    public Mappool reorderBeatmap(Mappool mappool, Long sourceBeatmapModificationId, Long destinationBeatmapModificationId, Integer fromIndex, Integer toIndex, User user) {
        BeatmapModification sourceBeatmapModification = mappool.getBeatmapModifications().stream()
                .filter(bm -> bm.getId().equals(sourceBeatmapModificationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Beatmap modification not found"));
        BeatmapModification destinationBeatmapModification = mappool.getBeatmapModifications().stream()
                .filter(bm -> bm.getId().equals(destinationBeatmapModificationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Beatmap modification not found"));
        if (sourceBeatmapModification.getBeatmaps().size() <= fromIndex)
            throw new RuntimeException("Beatmap not found");
        if (destinationBeatmapModification.getBeatmaps().size() < toIndex)
            throw new RuntimeException("Beatmap not found");
        Beatmap beatmap = sourceBeatmapModification.getBeatmaps().get(fromIndex);
        sourceBeatmapModification.getBeatmaps().remove(beatmap);
        destinationBeatmapModification.getBeatmaps().add(toIndex, beatmap);
        Beatmap updatedBeatmap = beatmapService.updateStatsForModification(beatmap, destinationBeatmapModification.getModification(), user);
        updatedBeatmap.setBeatmapModification(destinationBeatmapModification);
        beatmapService.save(updatedBeatmap);
        beatmapModificationService.save(sourceBeatmapModification);
        beatmapModificationService.save(destinationBeatmapModification);
        return mappool;
    }

    /**
     * Set the mappool's isReleased property to true and save it.
     *
     * @param mappool The mappool to be released.
     * @return The mappool object that was saved.
     */
    public Mappool releaseMappool(Mappool mappool) {
        mappool.setIsReleased(true);
        return mappoolRepository.save(mappool);
    }

    /**
     * Delete a mappool from the database.
     *
     * @param mappool The mappool object that you want to delete.
     * @return The mappool that was deleted.
     */
    public Mappool deleteMappool(Mappool mappool) {
        mappoolRepository.delete(mappool);
        return mappool;
    }

    /**
     * This function takes a mappool and sets its isReleased field to false, then saves it to the database.
     *
     * @param mappool The mappool to be concealed.
     * @return The mappool that was just saved.
     */
    public Mappool concealMappool(Mappool mappool) {
        mappool.setIsReleased(false);
        return mappoolRepository.save(mappool);
    }
}
