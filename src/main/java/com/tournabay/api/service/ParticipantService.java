package com.tournabay.api.service;

import com.tournabay.api.exception.AppException;
import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final UserService userService;

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
     * Save all the participants in the list to the database.
     *
     * @param participants The list of participants to be saved.
     * @return A list of participants
     */
    public List<Participant> saveAll(List<Participant> participants) {
        return participantRepository.saveAll(participants);
    }

    /**
     * Get the participant with the given osuId from the given tournament, or create a new one if it doesn't exist.
     * <p>
     * The first thing we do is get the participants from the tournament. Then we filter the
     * participants by the given osuId. Then we use findFirst() method to get the first participant that
     * matches the filter. If there is no participant that matches the filter, we create a new one
     *
     * @param osuId      The osuId of the user you want to get the participant of.
     * @param tournament The tournament that the participant is in
     * @return A participant object
     */
    public Participant getByOsuId(Long osuId, Tournament tournament) {
        return tournament
                .getParticipants()
                .stream()
                .filter(participant -> participant.getUser().getOsuId().equals(osuId))
                .findFirst()
                .orElse(createParticipantFromOsuId(osuId, tournament));
    }

    /**
     * Return the participant with the given id, or throw an exception if no such participant exists.
     * <p>
     * The first thing we do is get the list of participants from the tournament. Then we use the Stream API to filter the
     * list of participants to only those with the given id. Then we use the findFirst() method to get the first
     * participant in the list. If there is no such participant, we throw an exception
     *
     * @param participantId The id of the participant we want to retrieve.
     * @param tournament    The tournament that the participant is in.
     * @return A participant
     */
    public Participant getById(Long participantId, Tournament tournament) {
        if (participantId == null) {
            throw new BadRequestException("Participant id cannot be null!");
        }
        if (tournament == null) {
            throw new AppException("Tournament cannot be null!");
        }
        return tournament
                .getParticipants()
                .stream()
                .filter(participant -> participant.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found!"));
    }

    /**
     * Get all participants from a tournament that have an id in the given list of participantIds.
     * <p>
     * If the number of the participantIds is not equal to the number of participants in the tournament, we throw an exception.
     *
     * @param participantIds The list of participantIds of the participants to be returned
     * @param tournament     The tournament to get the participants from.
     * @return A list of participants that are in the tournament and have an id that is in the list of participantIds.
     */
    public List<Participant> getAllByIds(List<Long> participantIds, Tournament tournament) {
        List<Participant> participants = tournament.getParticipants()
                .stream()
                .filter(participant -> participantIds.contains(participant.getId()))
                .collect(Collectors.toList());
        if (participants.size() != participantIds.size())
            throw new BadRequestException("One or more participants not found!");
        return participants;
    }

    /**
     * Get all participants from a tournament by their ids, and throw an exception if any of them are not found.
     *
     * @param participantIds A set of participant IDs
     * @param tournament     The tournament that the participants are in.
     * @return A list of participants
     */
    public List<Participant> getAllByIds(Set<Long> participantIds, Tournament tournament) {
        List<Participant> participants = tournament.getParticipants()
                .stream()
                .filter(participant -> participantIds.contains(participant.getId()))
                .collect(Collectors.toList());
        if (participants.size() != participantIds.size())
            throw new BadRequestException("One or more participants not found!");
        return participants;
    }

    /**
     * Delete all participants with the given ids and return them.
     *
     * @param ids        The ids of the participants to be deleted.
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
     * @param tournament  The tournament that the participant is in.
     * @return The participant that was deleted.
     */
    public Participant delete(Participant participant, Tournament tournament) {
        if (!tournament.containsParticipant(participant))
            throw new BadRequestException("Participant doesn't exist in tournament");
        participantRepository.delete(participant);
        return participant;
    }

    /**
     * If the participant doesn't exist in the tournament, throw a BadRequestException, otherwise delete the participant.
     *
     * @param participantId The id of the participant to be deleted
     * @param tournament    The tournament that the participant is in.
     */
    public void deleteById(Long participantId, Tournament tournament) {
        if (!tournament.containsParticipantById(participantId))
            throw new BadRequestException("Participant doesn't exist in tournament");
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            boolean isCaptain = teamBasedTournament.getTeams().stream().anyMatch(team -> team.getCaptain().getId().equals(participantId));
            if (isCaptain) throw new BadRequestException("Participant is captain of a team. Cannot be deleted!");
        }
        participantRepository.deleteById(participantId);
    }

    /**
     * Create a participant from an osuId, and add the user to the database if they don't exist.
     * <p>
     * The first thing we do is call the `userService.addUserByOsuId(osuId)` function. This function will return a user
     * object if the user exists in the database, or create a new user and return that
     *
     * @param osuId      The osu! id of the user you want to add.
     * @param tournament The tournament that the participant is being added to.
     * @return A participant object
     */
    public Participant createParticipantFromOsuId(Long osuId, Tournament tournament) {
        User user = userService.addUserByOsuId(osuId);
        return Participant.builder()
                .user(user)
                .discordId(user.getDiscordId())
                .seed(Seed.UNKNOWN)
                .joinedAt(LocalDateTime.now())
                .status(ParticipantStatus.ACCEPTED)
                .tournament(tournament)
                .build();
    }

    /**
     * If the tournament doesn't already contain the participant, add the participant to the tournament and save the
     * participant.
     * <p>
     * The first thing we do is check if the tournament already contains the participant. If it does, we throw a
     * BadRequestException. If it doesn't, we set the tournament of the participant to the tournament we're adding the
     * participant to, and then we save the participant
     *
     * @param tournament  The tournament to add the participant to.
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
//    public Participant updateParticipant(Participant participant, UpdateParticipantRequest body, Tournament tournament) {
//        if (tournament instanceof TeamBasedTournament) {
//            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
//            // TODO: Limit team size when settings are done for TeamBasedTournaments
//            // Detach the participant from the team if he's in any
//            teamBasedTournament.getTeams()
//                    .stream()
//                    .filter(team -> team.getParticipants().contains(participant))
//                    .findFirst()
//                    .ifPresent(team -> {
//                        team.getParticipants().remove(participant);
//                        teamService.save(team);
//                    });
//            Team team = teamService.findById(body.getTeamId());
//            team.getParticipants().add(participant);
//            teamService.save(team);
//            participant.setDiscordId(body.getDiscordId());
//            return participantRepository.save(participant);
//        } else if (tournament instanceof PlayerBasedTournament playerBasedTournament) {
//            participant.setDiscordId(body.getDiscordId());
//            return participantRepository.save(participant);
//        }
//        throw new BadRequestException("Tournament type not supported");
//    }
}
