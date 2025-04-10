package com.example.myapp.services;

import com.example.myapp.entities.Member;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    Member save(Member member);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    void deleteById(Long id);

    List<Member> findByClub(Club club);

    List<Member> findByUser(User user);

    Optional<Member> findByUserAndClub(User user, Club club);

    List<Member> findActiveMembers(Club club);

    List<Member> findUserActiveClubs(User user);

    boolean isUserMemberOfClub(User user, Club club);

    long getMemberCount(Club club);

    Member addMember(User user, Club club);

    boolean removeMember(User user, Club club);

    boolean deactivateMember(User user, Club club);

    boolean reactivateMember(User user, Club club);
}