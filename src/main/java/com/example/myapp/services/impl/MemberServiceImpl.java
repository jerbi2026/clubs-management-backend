package com.example.myapp.services.impl;

import com.example.myapp.entities.Member;
import com.example.myapp.entities.Club;
import com.example.myapp.entities.User;
import com.example.myapp.enums.Role;
import com.example.myapp.repositories.MemberRepository;
import com.example.myapp.services.MemberService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserService UserService;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, com.example.myapp.services.UserService userService) {
        this.memberRepository = memberRepository;
        UserService = userService;
    }

    @Override
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public List<Member> findByClub(Club club) {
        return memberRepository.findByClub(club);
    }

    @Override
    public List<Member> findByUser(User user) {
        return memberRepository.findByUser(user);
    }

    @Override
    public Optional<Member> findByUserAndClub(User user, Club club) {
        return memberRepository.findByUserAndClub(user, club);
    }

    @Override
    public List<Member> findActiveMembers(Club club) {
        return memberRepository.findByClubAndIsActiveTrue(club);
    }

    @Override
    public List<Member> findUserActiveClubs(User user) {
        return memberRepository.findByUserAndIsActiveTrue(user);
    }

    @Override
    public boolean isUserMemberOfClub(User user, Club club) {
        Optional<Member> member = findByUserAndClub(user, club);
        return member.isPresent() && member.get().isActive();
    }

    @Override
    public long getMemberCount(Club club) {
        return memberRepository.countByClub(club);
    }

    @Override
    @Transactional
    public Member addMember(User user, Club club) {
        // Check if membership already exists
        Optional<Member> existingMembership = findByUserAndClub(user, club);

        if (existingMembership.isPresent()) {
            Member member = existingMembership.get();
            if (!member.isActive()) {
                // Reactivate if inactive
                member.setActive(true);
                return memberRepository.save(member);
            }
            return member; // Already a member
        } else {
            // Create new membership
            Member newMember = new Member();
            newMember.setUser(user);
            newMember.setClub(club);
            newMember.setJoinDate(new Date());
            newMember.setActive(true);
            user.setRole(Role.MEMBRE);
            UserService.saveUser(user);
            return memberRepository.save(newMember);
        }
    }

    @Override
    @Transactional
    public boolean removeMember(User user, Club club) {
        Optional<Member> membership = findByUserAndClub(user, club);
        if (membership.isPresent()) {
            memberRepository.delete(membership.get());
            user.setRole(Role.USER);
            UserService.saveUser(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deactivateMember(User user, Club club) {
        Optional<Member> membership = findByUserAndClub(user, club);
        if (membership.isPresent() && membership.get().isActive()) {
            Member member = membership.get();
            member.setActive(false);
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean reactivateMember(User user, Club club) {
        Optional<Member> membership = findByUserAndClub(user, club);
        if (membership.isPresent() && !membership.get().isActive()) {
            Member member = membership.get();
            member.setActive(true);
            memberRepository.save(member);
            return true;
        }
        return false;
    }
}