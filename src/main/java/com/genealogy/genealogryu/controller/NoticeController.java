package com.genealogy.genealogryu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.genealogy.genealogryu.entity.Notice;
import com.genealogy.genealogryu.service.NoticeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/notices")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    // 공지사항 목록 조회
    @GetMapping
    public String listNotices(Model model) {
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        model.addAttribute("totalNotices", noticeService.getNoticeCount());
        return "notice/list";
    }
    
    // 공지사항 등록 폼
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice/create";
    }
    
    // 공지사항 등록 처리
    @PostMapping("/create")
    public String createNotice(@Valid @ModelAttribute Notice notice, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "notice/create";
        }
        
        noticeService.createNotice(notice);
        redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        return "redirect:/notices";
    }
    
    // 공지사항 상세 조회
    @GetMapping("/{id}")
    public String viewNotice(@PathVariable Long id, Model model) {
        return noticeService.getNoticeById(id)
                .map(notice -> {
                    model.addAttribute("notice", notice);
                    return "notice/view";
                })
                .orElse("redirect:/notices");
    }
    
    // 공지사항 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return noticeService.getNoticeById(id)
                .map(notice -> {
                    model.addAttribute("notice", notice);
                    return "notice/edit";
                })
                .orElse("redirect:/notices");
    }
    
    // 공지사항 수정 처리
    @PostMapping("/{id}/edit")
    public String editNotice(@PathVariable Long id, 
                            @Valid @ModelAttribute Notice notice, 
                            BindingResult result, 
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "notice/edit";
        }
        
        Notice updatedNotice = noticeService.updateNotice(id, notice);
        if (updatedNotice != null) {
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "공지사항 수정에 실패했습니다.");
        }
        return "redirect:/notices/" + id;
    }
    
    // 공지사항 삭제
    @PostMapping("/{id}/delete")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (noticeService.deleteNotice(id)) {
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "공지사항 삭제에 실패했습니다.");
        }
        return "redirect:/notices";
    }
    
    // 공지사항 검색
    @GetMapping("/search")
    public String searchNotices(@RequestParam String keyword, 
                               @RequestParam(defaultValue = "title") String type, 
                               Model model) {
        List<Notice> notices;
        switch (type) {
            case "author":
                notices = noticeService.searchNoticesByAuthor(keyword);
                break;
            default:
                notices = noticeService.searchNoticesByTitle(keyword);
                break;
        }
        
        model.addAttribute("notices", notices);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", type);
        return "notice/search";
    }
    
    // 중요 공지사항 목록
    @GetMapping("/important")
    public String importantNotices(Model model) {
        List<Notice> notices = noticeService.getImportantNotices();
        model.addAttribute("notices", notices);
        return "notice/important";
    }
    
    // 인기 공지사항 목록
    @GetMapping("/popular")
    public String popularNotices(Model model) {
        List<Notice> notices = noticeService.getPopularNotices();
        model.addAttribute("notices", notices);
        return "notice/popular";
    }
}
