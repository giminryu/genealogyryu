package com.genealogy.genealogryu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.genealogy.genealogryu.entity.Member;
import com.genealogy.genealogryu.entity.Notice;
import com.genealogy.genealogryu.repository.MemberRepository;
import com.genealogy.genealogryu.repository.NoticeRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private NoticeRepository noticeRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 샘플 회원 데이터 생성
        if (memberRepository.count() == 0) {
            createSampleMembers();
        }
        
        // 샘플 공지사항 데이터 생성
        if (noticeRepository.count() == 0) {
            createSampleNotices();
        }
    }
    
    private void createSampleMembers() {
        Member member1 = new Member();
        member1.setName("김철수");
        member1.setPhone("010-1234-5678");
        member1.setEmail("kim@example.com");
        member1.setBirthDate("1980-05-15");
        member1.setFamilyRank("25세");
        member1.setAddress("서울시 강남구");
        memberRepository.save(member1);
        
        Member member2 = new Member();
        member2.setName("이영희");
        member2.setPhone("010-2345-6789");
        member2.setEmail("lee@example.com");
        member2.setBirthDate("1985-08-22");
        member2.setFamilyRank("26세");
        member2.setAddress("서울시 서초구");
        memberRepository.save(member2);
        
        Member member3 = new Member();
        member3.setName("박민수");
        member3.setPhone("010-3456-7890");
        member3.setEmail("park@example.com");
        member3.setBirthDate("1975-12-03");
        member3.setFamilyRank("24세");
        member3.setAddress("경기도 성남시");
        memberRepository.save(member3);
        
        Member member4 = new Member();
        member4.setName("최지영");
        member4.setPhone("010-4567-8901");
        member4.setEmail("choi@example.com");
        member4.setBirthDate("1990-03-10");
        member4.setFamilyRank("27세");
        member4.setAddress("인천시 남동구");
        memberRepository.save(member4);
        
        Member member5 = new Member();
        member5.setName("정현우");
        member5.setPhone("010-5678-9012");
        member5.setEmail("jung@example.com");
        member5.setBirthDate("1988-11-25");
        member5.setFamilyRank("26세");
        member5.setAddress("부산시 해운대구");
        memberRepository.save(member5);
    }
    
    private void createSampleNotices() {
        Notice notice1 = new Notice();
        notice1.setTitle("2024년 종친회 정기총회 안내");
        notice1.setContent("안녕하세요. 2024년 종친회 정기총회를 개최합니다.\n\n" +
                "일시: 2024년 3월 15일 오후 2시\n" +
                "장소: 종친회관 대회의실\n" +
                "주요 안건: 2024년 사업계획 및 예산안 심의\n\n" +
                "많은 참석 부탁드립니다.");
        notice1.setAuthor("종친회장");
        notice1.setImportant(true);
        noticeRepository.save(notice1);
        
        Notice notice2 = new Notice();
        notice2.setTitle("족보 발간 사업 진행상황");
        notice2.setContent("현재 진행 중인 족보 발간 사업의 진행상황을 안내드립니다.\n\n" +
                "1. 자료 수집 완료: 95%\n" +
                "2. 편집 작업: 70%\n" +
                "3. 인쇄 작업: 30%\n\n" +
                "예정 발간일: 2024년 6월");
        notice2.setAuthor("족보편찬위원회");
        notice2.setImportant(false);
        noticeRepository.save(notice2);
        
        Notice notice3 = new Notice();
        notice3.setTitle("청년회 활동 안내");
        notice3.setContent("청년회 활동에 대한 안내입니다.\n\n" +
                "정기 모임: 매월 첫째 주 토요일\n" +
                "활동 내용: 봉사활동, 문화행사, 네트워킹\n" +
                "참여 문의: 청년회장 (010-1234-5678)");
        notice3.setAuthor("청년회장");
        notice3.setImportant(false);
        noticeRepository.save(notice3);
        
        Notice notice4 = new Notice();
        notice4.setTitle("종친회관 이용 안내");
        notice4.setContent("종친회관 이용에 대한 안내입니다.\n\n" +
                "개방 시간: 평일 09:00-18:00, 주말 10:00-17:00\n" +
                "예약 문의: 02-1234-5678\n" +
                "이용료: 무료 (종친회원 한정)");
        notice4.setAuthor("종친회관 관리자");
        notice4.setImportant(false);
        noticeRepository.save(notice4);
        
        Notice notice5 = new Notice();
        notice5.setTitle("2024년 설날 대제례 안내");
        notice5.setContent("2024년 설날 대제례를 안내드립니다.\n\n" +
                "일시: 2024년 2월 10일 오전 10시\n" +
                "장소: 종친회관 제례실\n" +
                "준비물: 제례복장\n\n" +
                "참석하실 분들은 미리 연락 부탁드립니다.");
        notice5.setAuthor("제례위원회");
        notice5.setImportant(true);
        noticeRepository.save(notice5);
    }
}
