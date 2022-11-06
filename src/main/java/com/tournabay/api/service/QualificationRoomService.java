package com.tournabay.api.service;

import com.tournabay.api.model.Participant;
import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Team;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import com.tournabay.api.payload.UpdateQualificationRoomRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface QualificationRoomService {

    /**
     * Create a qualification room for the given tournament, starting at the given time.
     *
     * @param startTime  The time the tournament starts.
     * @param tournament The tournament that the qualification room is for.
     * @return A QualificationRoom object.
     */
    QualificationRoom createQualificationRoom(LocalDateTime startTime, Tournament tournament);

    /**
     * Removes a qualification room from a tournament.
     *
     * @param qualificationRoom The QualificationRoom object to be removed.
     * @param tournament        The tournament to remove the qualification room from.
     * @return A QualificationRoom object.
     */
    QualificationRoom removeQualificationRoom(QualificationRoom qualificationRoom, Tournament tournament);

    /**
     * It returns a QualificationRoom object.
     *
     * @param id         The id of the qualification room.
     * @param tournament The tournament object that you want to get the qualification room for.
     * @return A QualificationRoom object.
     */
    QualificationRoom getQualificationRoom(Long id, Tournament tournament);

    /**
     * Update a qualification room
     *
     * @param tournament        The tournament object that the qualification room belongs to.
     * @param qualificationRoom The qualification room to update.
     * @param request           The request object that contains the new values for the qualification room.
     * @return QualificationRoom
     */
    QualificationRoom updateQualificationRoom(Tournament tournament, QualificationRoom qualificationRoom, UpdateQualificationRoomRequest request);

    /**
     * Add a staff member to a qualification room.
     *
     * @param qualificationRoom The QualificationRoom object that you want to add the staff member to.
     * @param staffMember       The staff member to add to the qualification room.
     * @return A QualificationRoom object.
     */
    QualificationRoom addStaffMember(QualificationRoom qualificationRoom, StaffMember staffMember);

    /**
     * Remove a staff member from a qualification room.
     *
     * @param qualificationRoom The QualificationRoom object that you want to remove the staff member from.
     * @param staffMember       The staff member to be removed from the qualification room.
     * @return A QualificationRoom object.
     */
    QualificationRoom removeStaffMember(QualificationRoom qualificationRoom, StaffMember staffMember);

    /**
     * Get all qualification rooms for a tournament.
     *
     * @param tournament The tournament object that you want to get the qualification rooms for.
     * @return A list of QualificationRoom objects.
     */
    List<QualificationRoom> getQualificationRooms(Tournament tournament);

    /**
     * Submit the result of a qualification room
     *
     * @param qualificationRoom The qualification room to submit the result for.
     * @return The qualificationRoom object is being returned.
     */
    QualificationRoom submitResult(QualificationRoom qualificationRoom);

    /**
     * Assign a team to a qualification room.
     *
     * @param qualificationRoom The QualificationRoom object that you want to assign the team to.
     * @param team              The team to assign to the qualification room.
     * @return A QualificationRoom object.
     */
    QualificationRoom assignTeam(QualificationRoom qualificationRoom, Team team);

    /**
     * Assigns a participant to a qualification room
     *
     * @param qualificationRoom The QualificationRoom object that you want to assign the participant to.
     * @param participant       The participant to assign to the qualification room.
     * @return A QualificationRoom with the participant assigned to it.
     */
    QualificationRoom assignParticipant(QualificationRoom qualificationRoom, Participant participant);

    /**
     * Remove a QualificationResult from a QualificationRoom
     *
     * @param qualificationRoom   The QualificationRoom object that you want to remove the result from.
     * @param qualificationResult The QualificationResult to remove from the QualificationRoom.
     * @return The qualificationRoom object is being returned.
     */
    QualificationRoom removeResult(QualificationRoom qualificationRoom, QualificationResult qualificationResult);

    /**
     * Given a tournament, return a new symbol.
     *
     * @param tournament The tournament object that contains the current state of the game.
     * @return A new symbol is being returned.
     */
    Character getNewSymbol(Tournament tournament);

}
