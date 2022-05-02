package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.UpdateParticipantRequest;
import com.tournabay.api.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final UserService userService;
    private final TeamService teamService;

    /**
     * Save the participant to the database.
     *
     * @param participant The participant object that is being saved.
     * @return The participant object is being returned.
     */
    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    /**
     * If a participant exists with the given osuId, return it, otherwise create a new participant with the given osuId.
     *
     * @param osuId The osuId of the user you want to get the participant for.
     * @return A participant object
     */
    public Participant getByOsuId(Long osuId) {
        return participantRepository.findByUserOsuId(osuId).orElse(createParticipantFromOsuId(osuId));
    }

    /**
     * Return the participant with the given id, or throw an exception if no such participant exists.
     *
     * The first thing we do is get the list of participants from the tournament. Then we use the Stream API to filter the
     * list of participants to only those with the given id. Then we use the findFirst() method to get the first
     * participant in the list. If there is no such participant, we throw an exception
     *
     * @param participantId The id of the participant we want to retrieve.
     * @param tournament The tournament that the participant is in.
     * @return A participant
     */
    public Participant getById(Long participantId, Tournament tournament) {
        return tournament
                .getParticipants()
                .stream()
                .filter(participant -> participant.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found!"));
    }

    /**
     * Get all participants from a tournament that have an id in the given list of ids.
     *
     * The function is a bit more complicated than that, but it's still pretty simple
     *
     * @param ids The list of ids of the participants to be returned
     * @param tournament The tournament to get the participants from.
     * @return A list of participants that are in the tournament and have an id that is in the list of ids.
     */
    public List<Participant> getAllByIds(List<Long> ids, Tournament tournament) {
        return tournament.getParticipants()
                .stream()
                .filter(participant -> ids.contains(participant.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Delete all participants with the given ids and return them.
     *
     * @param ids The ids of the participants to be deleted.
     * @param tournament The tournament that the participants are in.
     * @return A list of participants.
     */
    public List<Participant> deleteAllByIds(List<Long> ids, Tournament tournament) {
        List<Participant> participants = this.getAllByIds(ids, tournament);
        participantRepository.deleteAllById(ids);
        return participants;
    }

    /**
     * If the participant doesn't exist in the tournament, throw an exception. Otherwise, delete the participant.
     *
     * @param participant The participant to be deleted
     * @param tournament The tournament that the participant is in.
     * @return The participant that was deleted.
     */
    public Participant delete(Participant participant, Tournament tournament) {
        if (!tournament.containsParticipant(participant)) throw new BadRequestException("Participant doesn't exist in tournament");
        participantRepository.delete(participant);
        return participant;
    }

    /**
     * If the participant doesn't exist in the tournament, throw a BadRequestException, otherwise delete the participant.
     *
     * @param participantId The id of the participant to be deleted
     * @param tournament The tournament that the participant is in.
     */
    public void deleteById(Long participantId, Tournament tournament) {
        if (!tournament.containsParticipantById(participantId)) throw new BadRequestException("Participant doesn't exist in tournament");
        participantRepository.deleteById(participantId);
    }

    /**
     * Create a new participant from an osuId.
     *
     * @param osuId The osu! user id of the user you want to add.
     * @return A participant object
     */
    public Participant createParticipantFromOsuId(Long osuId) {
        User user = userService.addUserByOsuId(osuId);
        return Participant.builder()
                .user(user)
                .discordId(user.getDiscordId())
                .seed(Seed.UNKNOWN)
                .joinedAt(LocalDateTime.now())
                .status(ParticipantStatus.ACCEPTED)
                .build();
    }

    /**
     * If the tournament doesn't already contain the participant, add the participant to the tournament and save the
     * participant.
     *
     * The first thing we do is check if the tournament already contains the participant. If it does, we throw a
     * BadRequestException. If it doesn't, we set the tournament of the participant to the tournament we're adding the
     * participant to, and then we save the participant
     *
     * @param tournament The tournament to add the participant to.
     * @param participant The participant to be added to the tournament.
     * @return A participant object
     */
    public Participant addParticipant(Tournament tournament, Participant participant) {
        if (!tournament.containsParticipant(participant)) {
            participant.setTournament(tournament);
            return participantRepository.save(participant);
        }
        throw new BadRequestException(participant.getUser().getUsername() + " is already a participant!");
    }

    // TODO: Docs
    public Participant updateParticipant(Participant participant, UpdateParticipantRequest body, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament teamBasedTournament) {
            // TODO: Limit team size when settings are done for TeamBasedTournaments
            // Detach the participant from the team if he's in any
            teamBasedTournament.getTeams()
                    .stream()
                    .filter(team -> team.getParticipants().contains(participant))
                    .findFirst()
                    .ifPresent(team -> {
                        team.getParticipants().remove(participant);
                        teamService.save(team);
                    });
            Team team = teamService.findById(body.getTeamId());
            team.getParticipants().add(participant);
            teamService.save(team);
            participant.setDiscordId(body.getDiscordId());
            return participantRepository.save(participant);
        } else if (tournament instanceof PlayerBasedTournament playerBasedTournament) {
            participant.setDiscordId(body.getDiscordId());
            return participantRepository.save(participant);
        }
        throw new BadRequestException("Tournament type not supported");
    }
}
