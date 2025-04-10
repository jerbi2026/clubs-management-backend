package com.example.myapp.services.impl;

import com.example.myapp.entities.Attestation;
import com.example.myapp.repositories.AttestationRepository;
import com.example.myapp.services.AttestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttestationServiceImpl implements AttestationService {

    private final AttestationRepository attestationRepository;

    @Autowired
    public AttestationServiceImpl(AttestationRepository attestationRepository) {
        this.attestationRepository = attestationRepository;
    }

    @Override
    public List<Attestation> getAllAttestations() {
        return attestationRepository.findAll();
    }

    @Override
    public Optional<Attestation> getAttestationById(Long id) {
        return attestationRepository.findById(id);
    }

    @Override
    public Attestation getAttestationByEvenementId(Long evenementId) {
        return attestationRepository.findByEvenementId(evenementId);
    }

    @Override
    public Attestation saveAttestation(Attestation attestation) {
        return attestationRepository.save(attestation);
    }

    @Override
    public void deleteAttestation(Long id) {
        attestationRepository.deleteById(id);
    }
}
