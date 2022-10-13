package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.model.BeatmapModification;
import com.tournabay.api.model.Mappool;
import com.tournabay.api.model.Modification;
import com.tournabay.api.repository.BeatmapModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BeatmapModificationService {
    private final BeatmapModificationRepository beatmapModificationRepository;

    /**
     * Save the beatmap modification to the database.
     *
     * @param beatmapModification The BeatmapModification object to be saved.
     * @return The BeatmapModification object that was saved.
     */
    public BeatmapModification save(BeatmapModification beatmapModification) {
        setBeatmapsPositions(beatmapModification);
        return beatmapModificationRepository.save(beatmapModification);
    }

    /**
     * Find the beatmap modification with the given ID in the given mappool, or throw a ResourceNotFoundException if it
     * doesn't exist.
     *
     * @param beatmapModificationId The ID of the beatmap modification you want to find.
     * @param mappool               The mappool that the beatmap modification is in.
     * @return A BeatmapModification object
     */
    public BeatmapModification findById(Long beatmapModificationId, Mappool mappool) {
        return mappool.getBeatmapModifications()
                .stream()
                .filter(beatmapModification -> beatmapModification.getId().equals(beatmapModificationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap modification not found"));
    }

    /**
     * Find the beatmap modification that has the same modification as the given modification, and throw an exception if
     * it doesn't exist.
     *
     * @param modification The modification to find
     * @param mappool      The mappool that the beatmap modification is in
     * @return A BeatmapModification object
     */
    public BeatmapModification findByModification(Modification modification, Mappool mappool) {
        return mappool.getBeatmapModifications().stream()
                .filter(beatmapModification -> beatmapModification.getModification().equals(modification))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap modification not found"));
    }

    /**
     * Add a beatmap to a beatmap modification.
     *
     * @param beatmapModification The BeatmapModification object that you want to add the beatmap to.
     * @param beatmap             The beatmap to add to the modification.
     * @return A BeatmapModification object
     */
    public BeatmapModification addBeatmapToModification(BeatmapModification beatmapModification, Beatmap beatmap) {
        beatmapModification.getBeatmaps().add(beatmap);
        return beatmapModificationRepository.save(beatmapModification);
    }

    /**
     * Create a list of BeatmapModifications, where each BeatmapModification is a Modification with a hidden value of
     * true if the Modification is in the defaultHiddenMods list, and false otherwise.
     *
     * @return A list of BeatmapModifications
     */
    public List<BeatmapModification> createDefaultBeatmapModifications() {
        List<Modification> defaultHiddenMods = new ArrayList<>(Arrays.asList(Modification.EZ, Modification.HT, Modification.FL));
        AtomicInteger position = new AtomicInteger(0);
        List<BeatmapModification> beatmapModifications = Stream.of(Modification.values())
                        .map(modification ->
                                BeatmapModification.builder()
                                        .modification(modification)
                                        .hidden(defaultHiddenMods.contains(modification))
                                        .beatmaps(new ArrayList<>())
                                        .position(position.getAndIncrement())
                                        .build()
                        )
                        .collect(Collectors.toList());
        return beatmapModificationRepository.saveAll(beatmapModifications);
    }

    public BeatmapModification findByBeatmap(Beatmap beatmap, Mappool mappool) {
        return mappool.getBeatmapModifications().stream()
                .filter(beatmapModification -> beatmapModification.getBeatmaps().contains(beatmap))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Beatmap modification not found"));
    }

    public BeatmapModification removeBeatmapFromModification(BeatmapModification beatmapModification, Beatmap beatmap) {
        beatmapModification.getBeatmaps().remove(beatmap);
        return beatmapModificationRepository.save(beatmapModification);
    }

    /**
     * It sets the position of each beatmap in the list to its index in the list
     *
     * @param beatmapModification The BeatmapModification object that contains the beatmaps to be modified.
     */
    private void setBeatmapsPositions(BeatmapModification beatmapModification) {
        List<Beatmap> beatmaps = beatmapModification.getBeatmaps();
        for (int i = 0; i < beatmaps.size(); i++) {
            beatmaps.get(i).setPosition(i);
        }
    }
}
