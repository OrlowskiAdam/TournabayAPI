package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.Participant;
import com.tournabay.api.model.ParticipantStatus;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
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
        participants.forEach(participant -> participant.setParticipantStatus(status));
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

    public void deleteById(Long participantId) {
        participantRepository.deleteById(participantId);
    }

    public Participant createParticipantFromOsuId(Long osuId) {
        User user = userService.addUserByOsuId(osuId);
        return Participant.builder()
                .user(user)
                .joinedAt(LocalDateTime.now())
                .participantStatus(ParticipantStatus.ACCEPTED)
                .build();
    }
}
