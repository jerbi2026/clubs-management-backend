package com.example.myapp.repositories;

import com.example.myapp.entities.Member;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByClub(Club club);

    List<Member> findByUser(User user);

    Optional<Member> findByUserAndClub(User user, Club club);

    List<Member> findByClubAndIsActiveTrue(Club club);

    List<Member> findByUserAndIsActiveTrue(User user);

    boolean existsByUserAndClub(User user, Club club);

    long countByClub(Club club);
}