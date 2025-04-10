package com.example.myapp.repositories;

import com.example.myapp.entities.Evenement;
import com.example.myapp.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByClubId(Long clubId);
    List<Evenement> findByEventType(EventType eventType);
    List<Evenement> findByDateAfter(Date date);
}