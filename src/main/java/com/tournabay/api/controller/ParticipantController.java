package com.tournabay.api.controller;

import com.tournabay.api.model.Participant;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.DeleteParticipantsRequest;
import com.tournabay.api.payload.SetParticipantsStatusRequest;
import com.tournabay.api.service.ParticipantService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final TournamentService tournamentService;
    private final ParticipantService participantService;

    @Secured("ROLE_USER")
    @PostMapping("/add/{participantOsuId}/{tournamentId}")
    // TODO: Security check
    public ResponseEntity<Participant> addParticipant(@PathVariable Long participantOsuId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Participant participant = participantService.getParticipantByOsuId(participantOsuId);
        Participant addedParticipant = tournamentService.addParticipant(tournament, participant);
        return ResponseEntity.ok(addedParticipant);
    }

    @Secured("ROLE_USER")
    @PostMapping("/delete/{tournamentId}")
    // TODO: Security check
    public ResponseEntity<List<Participant>> deleteParticipants(@PathVariable Long tournamentId, @RequestBody DeleteParticipantsRequest deleteParticipantsRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Participant> deletedParticipants = participantService.deleteAllByIds(deleteParticipantsRequest.getParticipantIds(), tournament);
        return ResponseEntity.ok(deletedParticipants);
    }

    @Secured("ROLE_USER")
    // TODO: Security check
    @PutMapping("/status/{tournamentId}")
    public ResponseEntity<List<Participant>> setStatus(@PathVariable Long tournamentId, @RequestBody SetParticipantsStatusRequest setParticipantsStatusRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Participant> participants = participantService.getAllByIds(setParticipantsStatusRequest.getParticipantIds(), tournament);
        List<Participant> updatedParticipants = participantService.setParticipantsStatus(participants, setParticipantsStatusRequest.getStatus());
        return ResponseEntity.ok(updatedParticipants);
    }
}
