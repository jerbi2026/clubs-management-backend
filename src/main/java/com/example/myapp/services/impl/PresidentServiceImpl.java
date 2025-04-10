package com.example.myapp.services.impl;

import com.example.myapp.entities.President;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.Role;
import com.example.myapp.repositories.PresidentRepository;
import com.example.myapp.services.PresidentService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PresidentServiceImpl implements PresidentService {

    private final PresidentRepository presidentRepository;

    @Autowired
    public PresidentServiceImpl(PresidentRepository presidentRepository) {
        this.presidentRepository = presidentRepository;
    }

    @Override
    public President save(President president) {
        return presidentRepository.save(president);
    }

    @Override
    public List<President> findAll() {
        return presidentRepository.findAll();
    }

    @Override
    public Optional<President> findById(Long id) {
        return presidentRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        presidentRepository.deleteById(id);
    }

    @Override
    public Optional<President> findCurrentPresidentByClub(Club club) {
        return presidentRepository.findByClubAndIsCurrentTrue(club);
    }

    @Override
    public Optional<President> findCurrentPresidentByUser(User user) {
        return presidentRepository.findByUserAndIsCurrentTrue(user);
    }

    @Override
    public List<President> getPresidentHistory(Club club) {
        return presidentRepository.findByClubOrderByStartDateDesc(club);
    }

    @Override
    public boolean isUserCurrentlyPresident(User user) {
        return presidentRepository.existsByUserAndIsCurrentTrue(user);
    }

    @Override
    public boolean hasClubCurrentPresident(Club club) {
        return presidentRepository.existsByClubAndIsCurrentTrue(club);
    }

    @Override
    @Transactional
    public President appointPresident(User user, Club club) {
        // Check if the club already has a president
        Optional<President> currentPresident = findCurrentPresidentByClub(club);

        if (currentPresident.isPresent()) {
            President president = currentPresident.get();
            president.setCurrent(false);
            president.setEndDate(new Date());
            presidentRepository.save(president);
        }

        President newPresident = new President();
        newPresident.setUser(user);
        newPresident.setClub(club);
        newPresident.setStartDate(new Date());
        newPresident.setCurrent(true);


        return presidentRepository.save(newPresident);
    }

    @Override
    @Transactional
    public boolean removePresident(Club club) {
        Optional<President> currentPresident = findCurrentPresidentByClub(club);

        if (currentPresident.isPresent()) {

            President president = currentPresident.get();
            president.setCurrent(false);
            president.setEndDate(new Date());
            presidentRepository.save(president);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean changePresident(Club club, User newPresident) {
        removePresident(club);

        appointPresident(newPresident, club);
        return true;
    }

    @Override
    @Transactional
    public boolean endPresidency(Club club, Date endDate) {
        Optional<President> currentPresident = findCurrentPresidentByClub(club);
        if (currentPresident.isPresent()) {
            President president = currentPresident.get();
            president.setCurrent(false);
            president.setEndDate(endDate);
            presidentRepository.save(president);
            return true;
        }
        return false;
    }
}