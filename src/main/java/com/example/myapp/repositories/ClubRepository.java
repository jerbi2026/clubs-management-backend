package com.example.myapp.repositories;

import com.example.myapp.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Club findByNom(String nom);
}
