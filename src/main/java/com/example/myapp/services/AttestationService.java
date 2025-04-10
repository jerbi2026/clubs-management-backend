package com.example.myapp.services;

import com.example.myapp.entities.Attestation;

import java.util.List;
import java.util.Optional;

public interface AttestationService {
    List<Attestation> getAllAttestations();
    Optional<Attestation> getAttestationById(Long id);
    Attestation getAttestationByEvenementId(Long evenementId);
    Attestation saveAttestation(Attestation attestation);
    void deleteAttestation(Long id);
}
