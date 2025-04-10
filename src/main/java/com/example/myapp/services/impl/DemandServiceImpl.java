package com.example.myapp.services.impl;

import com.example.myapp.entities.Demand;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.DemandStatus;
import com.example.myapp.repositories.DemandRepository;
import com.example.myapp.services.DemandService;
import com.example.myapp.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DemandServiceImpl implements DemandService {

    private final DemandRepository demandRepository;
    private final MemberService memberService;

    @Autowired
    public DemandServiceImpl(DemandRepository demandRepository, MemberService memberService) {
        this.demandRepository = demandRepository;
        this.memberService = memberService;
    }

    @Override
    public Demand save(Demand demand) {
        return demandRepository.save(demand);
    }

    @Override
    public List<Demand> findAll() {
        return demandRepository.findAll();
    }

    @Override
    public Optional<Demand> findById(Long id) {
        return demandRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        demandRepository.deleteById(id);
    }

    @Override
    public List<Demand> findByClub(Club club) {
        return demandRepository.findByClub(club);
    }

    @Override
    public List<Demand> findByUser(User user) {
        return demandRepository.findByUser(user);
    }

    @Override
    public List<Demand> findByStatus(DemandStatus status) {
        return demandRepository.findByStatus(status);
    }

    @Override
    public List<Demand> findPendingDemandsByClub(Club club) {
        return demandRepository.findByClubAndStatus(club, DemandStatus.PENDING);
    }

    @Override
    public List<Demand> findPendingDemandsByUser(User user) {
        return demandRepository.findByUserAndStatus(user, DemandStatus.PENDING);
    }

    @Override
    public Optional<Demand> getLatestDemand(User user, Club club) {
        return demandRepository.findFirstByUserAndClubOrderByRequestDateDesc(user, club);
    }

    @Override
    public boolean hasActiveDemand(User user, Club club) {
        return demandRepository.existsByUserAndClubAndStatus(user, club, DemandStatus.PENDING);
    }

    @Override
    @Transactional
    public Demand createDemand(User user, Club club, String comment) {
        if (memberService.isUserMemberOfClub(user, club)) {
            throw new IllegalStateException("User is already a member of this club");
        }

        if (hasActiveDemand(user, club)) {
            throw new IllegalStateException("User already has a pending demand for this club");
        }

        Demand demand = new Demand();
        demand.setUser(user);
        demand.setClub(club);
        demand.setComment(comment);
        demand.setRequestDate(new Date());
        demand.setStatus(DemandStatus.PENDING);

        return demandRepository.save(demand);
    }

    @Override
    @Transactional
    public Demand approveDemand(Long demandId) {
        Optional<Demand> optionalDemand = demandRepository.findById(demandId);
        if (optionalDemand.isPresent()) {
            Demand demand = optionalDemand.get();

            if (demand.getStatus() == DemandStatus.PENDING) {
                demand.setStatus(DemandStatus.APPROVED);
                demand.setResponseDate(new Date());

                memberService.addMember(demand.getUser(), demand.getClub());

                return demandRepository.save(demand);
            }
        }
        throw new IllegalArgumentException("Invalid demand ID or demand is not pending");
    }

    @Override
    @Transactional
    public Demand rejectDemand(Long demandId, String rejectionReason) {
        Optional<Demand> optionalDemand = demandRepository.findById(demandId);
        if (optionalDemand.isPresent()) {
            Demand demand = optionalDemand.get();

            // Only process if demand is pending
            if (demand.getStatus() == DemandStatus.PENDING) {
                demand.setStatus(DemandStatus.REFUSED);
                demand.setResponseDate(new Date());
                demand.setComment(demand.getComment() + "\nRejection reason: " + rejectionReason);

                return demandRepository.save(demand);
            }
        }
        throw new IllegalArgumentException("Invalid demand ID or demand is not pending");
    }

    @Override
    @Transactional
    public boolean cancelDemand(Long demandId) {
        Optional<Demand> optionalDemand = demandRepository.findById(demandId);
        if (optionalDemand.isPresent()) {
            Demand demand = optionalDemand.get();

            if (demand.getStatus() == DemandStatus.PENDING) {
                demandRepository.deleteById(demandId);
                return true;
            }
        }
        return false;
    }
}