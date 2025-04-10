package com.example.myapp.Controllers;

import com.example.myapp.entities.Member;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.services.MemberService;
import com.example.myapp.services.ClubService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final ClubService clubService;
    private final UserService userService;

    @Autowired
    public MemberController(MemberService memberService, ClubService clubService, UserService userService) {
        this.memberService = memberService;
        this.clubService = clubService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.findAll();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberService.findById(id);
        return member
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Member>> getMembersByClub(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            List<Member> members = memberService.findByClub(club.get());
            return new ResponseEntity<>(members, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/club/{clubId}/active")
    public ResponseEntity<List<Member>> getActiveMembers(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            List<Member> activeMembers = memberService.findActiveMembers(club.get());
            return new ResponseEntity<>(activeMembers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Member>> getMembershipsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Member> memberships = memberService.findByUser(user.get());
            return new ResponseEntity<>(memberships, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Member>> getUserActiveClubs(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Member> activeClubs = memberService.findUserActiveClubs(user.get());
            return new ResponseEntity<>(activeClubs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkMembership(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            boolean isMember = memberService.isUserMemberOfClub(user.get(), club.get());
            return new ResponseEntity<>(isMember, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/count/{clubId}")
    public ResponseEntity<Long> getMemberCount(@PathVariable Long clubId) {
        Optional<Club> club = clubService.getClubById(clubId);
        if (club.isPresent()) {
            long count = memberService.getMemberCount(club.get());
            return new ResponseEntity<>(count, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add")
    public ResponseEntity<Member> addMember(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            Member member = memberService.addMember(user.get(), club.get());
            return new ResponseEntity<>(member, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeMember(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            boolean removed = memberService.removeMember(user.get(), club.get());
            if (removed) {
                return new ResponseEntity<>("Member removed successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Member not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User or club not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivateMember(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            boolean deactivated = memberService.deactivateMember(user.get(), club.get());
            if (deactivated) {
                return new ResponseEntity<>("Member deactivated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Member not found or already inactive", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User or club not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/reactivate")
    public ResponseEntity<String> reactivateMember(@RequestParam Long userId, @RequestParam Long clubId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Club> club = clubService.getClubById(clubId);

        if (user.isPresent() && club.isPresent()) {
            boolean reactivated = memberService.reactivateMember(user.get(), club.get());
            if (reactivated) {
                return new ResponseEntity<>("Member reactivated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Member not found or already active", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User or club not found", HttpStatus.NOT_FOUND);
    }
}