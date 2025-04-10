package com.example.myapp.repositories;

import com.example.myapp.entities.Demand;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.DemandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    List<Demand> findByClub(Club club);

    List<Demand> findByUser(User user);

    List<Demand> findByStatus(DemandStatus status);

    List<Demand> findByClubAndStatus(Club club, DemandStatus status);

    List<Demand> findByUserAndStatus(User user, DemandStatus status);

    Optional<Demand> findFirstByUserAndClubOrderByRequestDateDesc(User user, Club club);

    boolean existsByUserAndClubAndStatus(User user, Club club, DemandStatus status);
}