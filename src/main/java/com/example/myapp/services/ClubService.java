package com.example.myapp.services;

import com.example.myapp.entities.Club;

import java.util.List;
import java.util.Optional;

public interface ClubService {
    List<Club> getAllClubs();
    Optional<Club> getClubById(Long id);
    Club getClubByNom(String nom);
    Club saveClub(Club club);
    void deleteClub(Long id);
}
