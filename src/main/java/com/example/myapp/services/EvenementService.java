package com.example.myapp.services;

import com.example.myapp.entities.Evenement;
import com.example.myapp.enums.EventType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EvenementService {
    List<Evenement> getAllEvenements();
    Optional<Evenement> getEvenementById(Long id);
    List<Evenement> getEvenementsByClubId(Long clubId);
    List<Evenement> getEvenementsByType(EventType eventType);
    List<Evenement> getUpcomingEvenements(Date date);
    Evenement saveEvenement(Evenement evenement);
    void updateEvenement(Evenement evenement);
    void deleteEvenement(Long id);
}
