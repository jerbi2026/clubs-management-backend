package com.example.myapp.services;

import com.example.myapp.entities.President;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PresidentService {

    President save(President president);

    List<President> findAll();

    Optional<President> findById(Long id);

    void deleteById(Long id);

    Optional<President> findCurrentPresidentByClub(Club club);

    Optional<President> findCurrentPresidentByUser(User user);

    List<President> getPresidentHistory(Club club);

    boolean isUserCurrentlyPresident(User user);

    boolean hasClubCurrentPresident(Club club);

    President appointPresident(User user, Club club);

    boolean removePresident(Club club);

    boolean changePresident(Club club, User newPresident);

    boolean endPresidency(Club club, Date endDate);
}