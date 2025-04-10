package com.example.myapp.repositories;

import com.example.myapp.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUserId(Long userId);
    List<Participation> findByEvenementId(Long evenementId);
    Participation findByUserIdAndEvenementId(Long userId, Long evenementId);
}

