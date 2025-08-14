package com.genealogy.genealogryu.repository;

import com.genealogy.genealogryu.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    // 이름으로 검색
    List<Member> findByNameContainingIgnoreCase(String name);
    
    // 전화번호로 검색
    Optional<Member> findByPhone(String phone);
    
    // 이메일로 검색
    Optional<Member> findByEmail(String email);
    
    // 활성 회원만 조회
    List<Member> findByIsActiveTrue();
    
    // 항렬별로 조회
    List<Member> findByFamilyRank(String familyRank);
    
    // 주소로 검색
    List<Member> findByAddressContainingIgnoreCase(String address);
    
    // 전체 회원 수 조회
    @Query("SELECT COUNT(m) FROM Member m WHERE m.isActive = true")
    long countActiveMembers();
    
    // 항렬별 회원 수 조회
    @Query("SELECT m.familyRank, COUNT(m) FROM Member m WHERE m.isActive = true GROUP BY m.familyRank")
    List<Object[]> countMembersByFamilyRank();
    
    // 최근 가입한 회원들 조회
    @Query("SELECT m FROM Member m WHERE m.isActive = true ORDER BY m.joinDate DESC")
    List<Member> findRecentMembers();
    
    // 지역별 회원 수 조회
    @Query("SELECT SUBSTRING(m.address, 1, 2), COUNT(m) FROM Member m WHERE m.isActive = true GROUP BY SUBSTRING(m.address, 1, 2)")
    List<Object[]> countMembersByRegion();
}
