package com.example.myapp.services;

import com.example.myapp.entities.Demand;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.DemandStatus;

import java.util.List;
import java.util.Optional;

public interface DemandService {

    Demand save(Demand demand);

    List<Demand> findAll();

    Optional<Demand> findById(Long id);

    void deleteById(Long id);

    List<Demand> findByClub(Club club);

    List<Demand> findByUser(User user);

    List<Demand> findByStatus(DemandStatus status);

    List<Demand> findPendingDemandsByClub(Club club);

    List<Demand> findPendingDemandsByUser(User user);

    Optional<Demand> getLatestDemand(User user, Club club);

    boolean hasActiveDemand(User user, Club club);

    Demand createDemand(User user, Club club, String comment);

    Demand approveDemand(Long demandId);

    Demand rejectDemand(Long demandId, String rejectionReason);

    boolean cancelDemand(Long demandId);
}