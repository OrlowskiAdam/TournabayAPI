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

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final UserService userService;
    private final TeamService teamService;

    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    public Participant getByOsuId(Long osuId) {
        return participantRepository.findByUserOsuId(osuId).orElse(createParticipantFromOsuId(osuId));
    }

    public Participant getById(Long participantId, Tournament tournament) {
        return tournament
                .getParticipants()
                .stream()
                .filter(participant -> participant.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found!"));
    }

    public List<Participant> getAllByIds(List<Long> ids, Tournament tournament) {
        return participantRepository.findAllById(ids);
    }

    public List<Participant> setParticipantsStatus(List<Participant> participants, ParticipantStatus status) {
        participants.forEach(participant -> participant.setStatus(status));
        return participantRepository.saveAll(participants);
    }

    public List<Participant> deleteAllByIds(List<Long> ids, Tournament tournament) {
        List<Participant> participants = this.getAllByIds(ids, tournament);
        participantRepository.deleteAllById(ids);
        return participants;
    }

    public Participant delete(Participant participant, Tournament tournament) {
        if (!tournament.containsParticipant(participant)) throw new BadRequestException("Participant doesn't exist in tournament");
        participantRepository.delete(participant);
        return participant;
    }

    public void deleteById(Long participantId, Tournament tournament) {
        if (!tournament.containsParticipantById(participantId)) throw new BadRequestException("Participant doesn't exist in tournament");
        participantRepository.deleteById(participantId);
    }

    public Participant createParticipantFromOsuId(Long osuId) {
        User user = userService.addUserByOsuId(osuId);
        return Participant.builder()
                .user(user)
                .discordId(user.getDiscordId())
                .joinedAt(LocalDateTime.now())
                .status(ParticipantStatus.ACCEPTED)
                .build();
    }

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
