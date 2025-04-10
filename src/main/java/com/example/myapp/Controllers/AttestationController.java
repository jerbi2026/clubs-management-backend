package com.example.myapp.Controllers;


import com.example.myapp.entities.Attestation;
import com.example.myapp.entities.Evenement;
import com.example.myapp.services.AttestationService;
import com.example.myapp.services.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attestations")
@CrossOrigin(origins = "*")
public class AttestationController {

    private final AttestationService attestationService;
    private final EvenementService evenementService;

    @Autowired
    public AttestationController(
            AttestationService attestationService,
            EvenementService evenementService
    ) {
        this.attestationService = attestationService;
        this.evenementService = evenementService;
    }

    @GetMapping
    public ResponseEntity<List<Attestation>> getAllAttestations() {
        List<Attestation> attestations = attestationService.getAllAttestations();
        return new ResponseEntity<>(attestations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attestation> getAttestationById(@PathVariable Long id) {
        Optional<Attestation> attestation = attestationService.getAttestationById(id);
        return attestation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<Attestation> getAttestationByEvenementId(@PathVariable Long evenementId) {
        Optional<Evenement> evenement = evenementService.getEvenementById(evenementId);
        if (evenement.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Attestation attestation = attestationService.getAttestationByEvenementId(evenementId);
        if (attestation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(attestation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Attestation> createAttestation(@Valid @RequestBody Attestation attestation) {
        // Vérifier si l'événement existe
        if (attestation.getEvenement() != null && attestation.getEvenement().getId() != null) {
            Optional<Evenement> evenement = evenementService.getEvenementById(attestation.getEvenement().getId());
            if (evenement.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Vérifier si une attestation existe déjà pour cet événement
            Attestation existingAttestation = attestationService.getAttestationByEvenementId(attestation.getEvenement().getId());
            if (existingAttestation != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        Attestation savedAttestation = attestationService.saveAttestation(attestation);
        return new ResponseEntity<>(savedAttestation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attestation> updateAttestation(
            @PathVariable Long id,
            @Valid @RequestBody Attestation attestationDetails) {
        Optional<Attestation> attestation = attestationService.getAttestationById(id);
        if (attestation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Attestation existingAttestation = attestation.get();
        existingAttestation.setNom(attestationDetails.getNom());
        existingAttestation.setDescription(attestationDetails.getDescription());

        // Mettre à jour l'événement si fourni
        if (attestationDetails.getEvenement() != null && attestationDetails.getEvenement().getId() != null) {
            Optional<Evenement> evenement = evenementService.getEvenementById(attestationDetails.getEvenement().getId());
            if (evenement.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingAttestation.setEvenement(attestationDetails.getEvenement());
        }

        Attestation updatedAttestation = attestationService.saveAttestation(existingAttestation);
        return new ResponseEntity<>(updatedAttestation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttestation(@PathVariable Long id) {
        Optional<Attestation> attestation = attestationService.getAttestationById(id);
        if (attestation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        attestationService.deleteAttestation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint pour générer une attestation pour un événement
    @PostMapping("/generer/{evenementId}")
    public ResponseEntity<Attestation> genererAttestation(
            @PathVariable Long evenementId,
            @Valid @RequestBody Attestation attestationModel) {

        Optional<Evenement> evenement = evenementService.getEvenementById(evenementId);
        if (evenement.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Vérifier si une attestation existe déjà pour cet événement
        Attestation existingAttestation = attestationService.getAttestationByEvenementId(evenementId);
        if (existingAttestation != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Créer une nouvelle attestation
        Attestation attestation = new Attestation();
        attestation.setNom(attestationModel.getNom());
        attestation.setDescription(attestationModel.getDescription());
        attestation.setEvenement(evenement.get());

        Attestation savedAttestation = attestationService.saveAttestation(attestation);
        return new ResponseEntity<>(savedAttestation, HttpStatus.CREATED);
    }
}
