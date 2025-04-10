package com.example.myapp.Controllers;

import com.example.myapp.entities.President;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.services.PresidentService;
import com.example.myapp.services.ClubService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/presidents")
public class PresidentController {

    private final PresidentService presidentService;
    private final ClubService clubService;
    private final UserService userService;

    @Autowired
    public PresidentController(PresidentService presidentService, ClubService clubService, UserService userService) {
        this.presidentService = presidentService;
        this.clubService = clubService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<President>> getAllPresidents() {
        List<President> presidents = presidentService.findAll();
        return new ResponseEntity<>(presidents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<President> getPresidentById(@PathVariable Long id) {
        Optional<President> president = presidentService.findById(id);
        return president
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/club/{clubId}/current")
    public ResponseEntity<President> getCurrentPresidentByClub(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            Optional<President> president = presidentService.findCurrentPresidentByClub(club.get());
            return president
                    .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/club/{clubId}/history")
    public ResponseEntity<List<President>> getPresidentHistory(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            List<President> presidentHistory = presidentService.getPresidentHistory(club.get());
            return new ResponseEntity<>(presidentHistory, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/appoint")
    public ResponseEntity<President> appointPresident(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            President president = presidentService.appointPresident(user.get(), club.get());
            return new ResponseEntity<>(president, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/change")
    public ResponseEntity<String> changePresident(@RequestParam Long clubId, @RequestParam Long newPresidentId) {
        Optional<Club> club = clubService.getClubById(clubId);
        Optional<User> newPresident = userService.getUserById(newPresidentId);

        if (club.isPresent() && newPresident.isPresent()) {
            boolean changed = presidentService.changePresident(club.get(), newPresident.get());
            if (changed) {
                return new ResponseEntity<>("President changed successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Failed to change president", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove/{clubId}")
    public ResponseEntity<String> removePresident(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);

        if (club.isPresent()) {
            boolean removed = presidentService.removePresident(club.get());
            if (removed) {
                return new ResponseEntity<>("President removed successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Club has no current president", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Club not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/end-presidency/{clubId}")
    public ResponseEntity<String> endPresidency(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);

        if (club.isPresent()) {
            boolean ended = presidentService.endPresidency(club.get(), new Date());
            if (ended) {
                return new ResponseEntity<>("Presidency ended successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Club has no current president", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Club not found", HttpStatus.NOT_FOUND);
    }
}