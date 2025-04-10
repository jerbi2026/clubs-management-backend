package com.example.myapp.Controllers;

import com.example.myapp.entities.Demand;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.DemandStatus;
import com.example.myapp.services.DemandService;
import com.example.myapp.services.ClubService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/demands")
public class DemandController {

    private final DemandService demandService;
    private final ClubService clubService;
    private final UserService userService;

    @Autowired
    public DemandController(DemandService demandService, ClubService clubService, UserService userService) {
        this.demandService = demandService;
        this.clubService = clubService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Demand>> getAllDemands() {
        List<Demand> demands = demandService.findAll();
        return new ResponseEntity<>(demands, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demand> getDemandById(@PathVariable Long id) {
        Optional<Demand> demand = demandService.findById(id);
        return demand
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Demand>> getDemandsByClub(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            List<Demand> demands = demandService.findByClub(club.get());
            return new ResponseEntity<>(demands, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/club/{clubId}/pending")
    public ResponseEntity<List<Demand>> getPendingDemandsByClub(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            List<Demand> pendingDemands = demandService.findPendingDemandsByClub(club.get());
            return new ResponseEntity<>(pendingDemands, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Demand>> getDemandsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Demand> demands = demandService.findByUser(user.get());
            return new ResponseEntity<>(demands, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<Demand>> getPendingDemandsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Demand> pendingDemands = demandService.findPendingDemandsByUser(user.get());
            return new ResponseEntity<>(pendingDemands, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Demand>> getDemandsByStatus(@PathVariable String status) {
        try {
            DemandStatus demandStatus = DemandStatus.valueOf(status.toUpperCase());
            List<Demand> demands = demandService.findByStatus(demandStatus);
            return new ResponseEntity<>(demands, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkActiveDemand(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            boolean hasActiveDemand = demandService.hasActiveDemand(user.get(), club.get());
            return new ResponseEntity<>(hasActiveDemand, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/latest")
    public ResponseEntity<Demand> getLatestDemand(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            Optional<Demand> latestDemand = demandService.getLatestDemand(user.get(), club.get());
            return latestDemand
                    .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDemand(@RequestParam Long userId, @RequestParam Long clubId, @RequestParam(required = false) String comment) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            try {
                Demand demand = demandService.createDemand(user.get(), club.get(), comment);
                return new ResponseEntity<>(demand, HttpStatus.CREATED);
            } catch (IllegalStateException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("User or club not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/approve/{demandId}")
    public ResponseEntity<?> approveDemand(@PathVariable Long demandId) {
        try {
            Demand approvedDemand = demandService.approveDemand(demandId);
            return new ResponseEntity<>(approvedDemand, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject/{demandId}")
    public ResponseEntity<?> rejectDemand(@PathVariable Long demandId, @RequestParam String rejectionReason) {
        try {
            Demand rejectedDemand = demandService.rejectDemand(demandId, rejectionReason);
            return new ResponseEntity<>(rejectedDemand, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/cancel/{demandId}")
    public ResponseEntity<String> cancelDemand(@PathVariable Long demandId) {
        boolean cancelled = demandService.cancelDemand(demandId);
        if (cancelled) {
            return new ResponseEntity<>("Demand cancelled successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to cancel demand. It may not exist or not be in a pending state.", HttpStatus.BAD_REQUEST);
    }
}