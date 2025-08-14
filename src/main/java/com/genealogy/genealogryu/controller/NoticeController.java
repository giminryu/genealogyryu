package com.genealogy.genealogryu.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // 작성자 자동 세팅(로그인 사용자명)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            if (username != null && !username.isBlank()) {
                notice.setAuthor(username);
            }
        }

        noticeService.createNotice(notice);
        redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        return "redirect:/notices";
    }

    // 공지사항 상세 조회
    @GetMapping("/{id}")
    public String viewNotice(@PathVariable Long id, Model model) {
        Optional<Notice> maybeNotice = noticeService.getNoticeById(id);
        if (maybeNotice.isEmpty()) {
            return "redirect:/notices";
        }

        Notice notice = maybeNotice.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        boolean isAdmin = authenticated && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isOwner = username != null
                && notice.getAuthor() != null
                && notice.getAuthor().equals(username);

        boolean canEdit = isOwner;              // 작성자만 수정
        boolean canDelete = isOwner || isAdmin; // 작성자 또는 관리자 삭제

        model.addAttribute("notice", notice);
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("canDelete", canDelete);

        return "notice/view";
    }

    // 공지사항 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Notice> maybeNotice = noticeService.getNoticeById(id);
        if (maybeNotice.isEmpty()) {
            return "redirect:/notices";
        }

        Notice notice = maybeNotice.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        boolean isOwner = username != null
                && notice.getAuthor() != null
                && notice.getAuthor().equals(username);

        if (!isOwner) {
            // 관리자라도 소유자가 아니면 수정 불가
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/notices/" + id;
        }

        model.addAttribute("notice", notice);
        return "notice/edit";
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

        Optional<Notice> maybeExisting = noticeService.getNoticeById(id);
        if (maybeExisting.isEmpty()) {
            return "redirect:/notices";
        }

        Notice existing = maybeExisting.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        boolean isOwner = username != null
                && existing.getAuthor() != null
                && existing.getAuthor().equals(username);

        if (!isOwner) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/notices/" + id;
        }

        // 작성자 덮어쓰기 방지: 기존 작성자 유지
        notice.setAuthor(existing.getAuthor());

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
        Optional<Notice> maybeExisting = noticeService.getNoticeById(id);
        if (maybeExisting.isEmpty()) {
            return "redirect:/notices";
        }

        Notice existing = maybeExisting.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        boolean isAdmin = authenticated && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isOwner = username != null
                && existing.getAuthor() != null
                && existing.getAuthor().equals(username);

        if (!(isOwner || isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/notices/" + id;
        }

        if (noticeService.deleteNotice(id)) {
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
            return "redirect:/notices";
        } else {
            redirectAttributes.addFlashAttribute("error", "공지사항 삭제에 실패했습니다.");
            return "redirect:/notices/" + id;
        }
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
    
    // 최신 공지사항 목록
    @GetMapping("/recent")
    public String recentNotices(Model model) {
        List<Notice> notices = noticeService.getRecentNotices();
        model.addAttribute("notices", notices);
        return "notice/recent";
    }
    
    // 공지사항 통계
    @GetMapping("/statistics")
    public String noticeStatistics(Model model) {
        model.addAttribute("totalNotices", noticeService.getNoticeCount());
        model.addAttribute("importantNotices", noticeService.getImportantNotices());
        model.addAttribute("popularNotices", noticeService.getPopularNotices());
        model.addAttribute("recentNotices", noticeService.getRecentNotices());
        model.addAttribute("noticesByMonth", noticeService.getNoticeCountByMonth());
        return "notice/statistics";
    }
    
    // 공지사항 중요도 토글
    @PostMapping("/{id}/toggle-important")
    public String toggleImportant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = noticeService.toggleImportant(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "공지사항 중요도가 변경되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "공지사항 중요도 변경에 실패했습니다.");
        }
        return "redirect:/notices/" + id;
    }
    
    // 공지사항 조회수 증가
    @PostMapping("/{id}/increment-view")
    public String incrementViewCount(@PathVariable Long id) {
        noticeService.incrementViewCount(id);
        return "redirect:/notices/" + id;
    }
}
