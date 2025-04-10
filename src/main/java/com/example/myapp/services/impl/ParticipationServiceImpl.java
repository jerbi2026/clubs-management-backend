package com.example.myapp.services.impl;


import com.example.myapp.entities.Participation;
import com.example.myapp.repositories.ParticipationRepository;
import com.example.myapp.services.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;

    @Autowired
    public ParticipationServiceImpl(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    @Override
    public List<Participation> getAllParticipations() {
        return participationRepository.findAll();
    }

    @Override
    public Optional<Participation> getParticipationById(Long id) {
        return participationRepository.findById(id);
    }

    @Override
    public List<Participation> getParticipationsByUserId(Long userId) {
        return participationRepository.findByUserId(userId);
    }

    @Override
    public List<Participation> getParticipationsByEvenementId(Long evenementId) {
        return participationRepository.findByEvenementId(evenementId);
    }

    @Override
    public Participation getParticipationByUserAndEvenement(Long userId, Long evenementId) {
        return participationRepository.findByUserIdAndEvenementId(userId, evenementId);
    }

    @Override
    public Participation saveParticipation(Participation participation) {
        return participationRepository.save(participation);
    }

    @Override
    public void deleteParticipation(Long id) {
        participationRepository.deleteById(id);
    }
}
