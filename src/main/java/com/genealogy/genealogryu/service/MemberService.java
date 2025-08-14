package com.genealogy.genealogryu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genealogy.genealogryu.entity.Member;
import com.genealogy.genealogryu.repository.MemberRepository;

@Service
@Transactional
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;
    
    // 회원 등록
    public Member registerMember(Member member) {
        return memberRepository.save(member);
    }
    
    // 회원 조회 (ID로)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    
    // 전체 회원 조회
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    
    // 활성 회원만 조회
    public List<Member> getActiveMembers() {
        return memberRepository.findByIsActiveTrue();
    }
    
    // 이름으로 검색
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
    
    // 전화번호로 검색
    public Optional<Member> getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }
    
    // 이메일로 검색
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    // 항렬별 회원 조회
    public List<Member> getMembersByFamilyRank(String familyRank) {
        return memberRepository.findByFamilyRank(familyRank);
    }
    
    // 주소로 검색
    public List<Member> searchMembersByAddress(String address) {
        return memberRepository.findByAddressContainingIgnoreCase(address);
    }
    
    // 회원 정보 수정
    public Member updateMember(Long id, Member memberDetails) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setName(memberDetails.getName());
            member.setPhone(memberDetails.getPhone());
            member.setEmail(memberDetails.getEmail());
            member.setBirthDate(memberDetails.getBirthDate());
            member.setFamilyRank(memberDetails.getFamilyRank());
            member.setAddress(memberDetails.getAddress());
            member.setActive(memberDetails.isActive());
            return memberRepository.save(member);
        }
        return null;
    }
    
    // 회원 삭제 (비활성화)
    public boolean deactivateMember(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setActive(false);
            memberRepository.save(member);
            return true;
        }
        return false;
    }
    
    // 활성 회원 수 조회
    public long getActiveMemberCount() {
        return memberRepository.countActiveMembers();
    }
    
    // 항렬별 회원 수 조회
    public List<Object[]> getMemberCountByFamilyRank() {
        return memberRepository.countMembersByFamilyRank();
    }
    
    // 최근 가입한 회원들 조회
    public List<Member> getRecentMembers() {
        return memberRepository.findRecentMembers();
    }
    
    // 지역별 회원 수 조회
    public List<Object[]> getMemberCountByRegion() {
        return memberRepository.countMembersByRegion();
    }
    
    // 회원 상태 토글
    public boolean toggleMemberStatus(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setActive(!member.isActive());
            memberRepository.save(member);
            return true;
        }
        return false;
    }
    
    // 일괄 회원 비활성화
    public int bulkDeactivateMembers(List<Long> memberIds) {
        int count = 0;
        for (Long id : memberIds) {
            if (deactivateMember(id)) {
                count++;
            }
        }
        return count;
    }
    
    // 회원 통계 정보
    public Object getMemberStatistics() {
        // 여기에 더 상세한 통계 정보를 추가할 수 있습니다
        return null;
    }
}
