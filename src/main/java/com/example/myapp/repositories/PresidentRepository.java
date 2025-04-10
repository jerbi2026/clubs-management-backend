package com.example.myapp.repositories;

import com.example.myapp.entities.President;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresidentRepository extends JpaRepository<President, Long> {

    Optional<President> findByClubAndIsCurrentTrue(Club club);

    Optional<President> findByUserAndIsCurrentTrue(User user);

    List<President> findByClubOrderByStartDateDesc(Club club);

    boolean existsByUserAndIsCurrentTrue(User user);

    boolean existsByClubAndIsCurrentTrue(Club club);
}