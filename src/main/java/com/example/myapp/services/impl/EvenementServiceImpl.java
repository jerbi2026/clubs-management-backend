package com.example.myapp.services.impl;

import com.example.myapp.entities.Evenement;
import com.example.myapp.enums.EventType;
import com.example.myapp.repositories.EvenementRepository;
import com.example.myapp.services.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;

    @Autowired
    public EvenementServiceImpl(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    public Optional<Evenement> getEvenementById(Long id) {
        return evenementRepository.findById(id);
    }

    @Override
    public List<Evenement> getEvenementsByClubId(Long clubId) {
        return evenementRepository.findByClubId(clubId);
    }

    @Override
    public List<Evenement> getEvenementsByType(EventType eventType) {
        return evenementRepository.findByEventType(eventType);
    }

    @Override
    public List<Evenement> getUpcomingEvenements(Date date) {
        return evenementRepository.findByDateAfter(date);
    }

    @Override
    public Evenement saveEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public void updateEvenement(Evenement evenement) {
        evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }
}