package com.example.myapp.services;

import com.example.myapp.entities.Participation;

import java.util.List;
import java.util.Optional;

/**
 * @author Vermeg
 **/
public interface ParticipationService {
    List<Participation> getAllParticipations();
    Optional<Participation> getParticipationById(Long id);
    List<Participation> getParticipationsByUserId(Long userId);
    List<Participation> getParticipationsByEvenementId(Long evenementId);
    Participation getParticipationByUserAndEvenement(Long userId, Long evenementId);
    Participation saveParticipation(Participation participation);
    void deleteParticipation(Long id);
}
