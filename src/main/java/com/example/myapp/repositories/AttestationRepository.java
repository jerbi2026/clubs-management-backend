package com.example.myapp.repositories;

import com.example.myapp.entities.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationRepository extends JpaRepository<Attestation, Long> {
    Attestation findByEvenementId(Long evenementId);
}

