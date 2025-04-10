package com.example.myapp.Controllers;


import com.example.myapp.entities.Evenement;
import com.example.myapp.entities.Participation;
import com.example.myapp.entities.User;
import com.example.myapp.services.EvenementService;
import com.example.myapp.services.ParticipationService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participations")
@CrossOrigin(origins = "*")
public class ParticipationController {

    private final ParticipationService participationService;
    private final UserService userService;
    private final EvenementService evenementService;

    @Autowired
    public ParticipationController(
            ParticipationService participationService,
            UserService userService,
            EvenementService evenementService
    ) {
        this.participationService = participationService;
        this.userService = userService;
        this.evenementService = evenementService;
    }

    @GetMapping
    public ResponseEntity<List<Participation>> getAllParticipations() {
        List<Participation> participations = participationService.getAllParticipations();
        return new ResponseEntity<>(participations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participation> getParticipationById(@PathVariable Long id) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        return participation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Participation>> getParticipationsByUserId(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Participation> participations = participationService.getParticipationsByUserId(userId);
        return new ResponseEntity<>(participations, HttpStatus.OK);
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Participation>> getParticipationsByEvenementId(@PathVariable Long evenementId) {
        Optional<Evenement> evenement = evenementService.getEvenementById(evenementId);
        if (evenement.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Participation> participations = participationService.getParticipationsByEvenementId(evenementId);
        return new ResponseEntity<>(participations, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/evenement/{evenementId}")
    public ResponseEntity<Participation> getParticipationByUserAndEvenement(
            @PathVariable Long userId,
            @PathVariable Long evenementId) {
        Participation participation = participationService.getParticipationByUserAndEvenement(userId, evenementId);
        if (participation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(participation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Participation> createParticipation(@Valid @RequestBody Participation participation) {
        // Vérifier si l'utilisateur existe
        Optional<User> user = userService.getUserById(participation.getUser().getId());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Vérifier si l'événement existe
        Optional<Evenement> evenement = evenementService.getEvenementById(participation.getEvenement().getId());
        if (evenement.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Vérifier si la participation existe déjà
        Participation existingParticipation = participationService.getParticipationByUserAndEvenement(
                participation.getUser().getId(),
                participation.getEvenement().getId());

        if (existingParticipation != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Définir la date de participation à aujourd'hui si non spécifiée
        if (participation.getDate() == null) {
            participation.setDate(new Date());
        }

        Participation savedParticipation = participationService.saveParticipation(participation);
        return new ResponseEntity<>(savedParticipation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participation> updateParticipation(
            @PathVariable Long id,
            @Valid @RequestBody Participation participationDetails) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        if (participation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Participation existingParticipation = participation.get();
        existingParticipation.setDate(participationDetails.getDate());
        existingParticipation.setUser(participationDetails.getUser());
        existingParticipation.setEvenement(participationDetails.getEvenement());

        Participation updatedParticipation = participationService.saveParticipation(existingParticipation);
        return new ResponseEntity<>(updatedParticipation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long id) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        if (participation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        participationService.deleteParticipation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}