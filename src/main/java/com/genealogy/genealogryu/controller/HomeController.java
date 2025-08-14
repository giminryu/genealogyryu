package com.genealogy.genealogryu.controller;

import com.genealogy.genealogryu.entity.Event;
import com.genealogy.genealogryu.repository.EventRepository;
import com.genealogy.genealogryu.service.EventService;
import com.genealogy.genealogryu.service.MemberService;
import com.genealogy.genealogryu.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventService eventService;


    // 메인 홈페이지
    @GetMapping("/")
    public String home(Model model) {
        // 최신 공지사항 5개
        model.addAttribute("recentNotices", noticeService.getRecentNotices().subList(0, 
            Math.min(5, noticeService.getRecentNotices().size())));
        
        // 중요 공지사항
        model.addAttribute("importantNotices", noticeService.getImportantNotices());
        
        // 인기 공지사항 3개
        model.addAttribute("popularNotices", noticeService.getPopularNotices().subList(0, 
            Math.min(3, noticeService.getPopularNotices().size())));
        
        // 전체 회원 수
        model.addAttribute("totalMembers", memberService.getActiveMemberCount());
        
        // 전체 공지사항 수
        model.addAttribute("totalNotices", noticeService.getNoticeCount());

        List<Event> latest = eventRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("latestEvents", latest);


        return "home";
    }
    
    // 대시보드
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 최근 가입한 회원들
        model.addAttribute("recentMembers", memberService.getRecentMembers().subList(0, 
            Math.min(10, memberService.getRecentMembers().size())));
        
        // 항렬별 회원 수
        model.addAttribute("membersByRank", memberService.getMemberCountByFamilyRank());
        
        // 최신 공지사항
        model.addAttribute("recentNotices", noticeService.getRecentNotices());
        
        // 통계 정보
        model.addAttribute("totalMembers", memberService.getActiveMemberCount());
        model.addAttribute("totalNotices", noticeService.getNoticeCount());
        
        return "dashboard";
    }
    
    // 소개 페이지
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    // 연락처 페이지
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    // 달력 화면
    @GetMapping("/calendar")
    public String calendar(Model model) {
        // 달력에 뿌릴 전체(또는 필요한 기간 필터) 일정
        model.addAttribute("events", eventService.getAllEvents());
        return "events/calendar";
    }

}
