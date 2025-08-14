package com.genealogy.genealogryu.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.genealogy.genealogryu.entity.Event;
import com.genealogy.genealogryu.service.EventService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    public EventController(EventService eventService) { this.eventService = eventService; }

    // 목록
    @GetMapping
    public String list(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", eventService.getEventCount());
        return "events/list";
    }

    // 작성 폼
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    // 저장
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("event") Event event,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/create";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            if (username != null && !username.isBlank()) {
                event.setAuthor(username);
            }
        }

        eventService.createEvent(event);
        redirectAttributes.addFlashAttribute("message", "경조사가 성공적으로 등록되었습니다.");
        return "redirect:/events";
    }

    // 상세
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Optional<Event> maybe = eventService.getEventById(id);
        if (maybe.isEmpty()) return "redirect:/events";

        Event event = maybe.get();
        eventService.increaseViewCount(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;
        boolean isAdmin = authenticated && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isOwner = username != null && event.getAuthor() != null && event.getAuthor().equals(username);

        model.addAttribute("event", event);
        model.addAttribute("canEdit", isOwner);
        model.addAttribute("canDelete", isOwner || isAdmin);
        return "events/view";
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> maybe = eventService.getEventById(id);
        if (maybe.isEmpty()) return "redirect:/events";

        Event event = maybe.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        if (!(username != null && event.getAuthor() != null && event.getAuthor().equals(username))) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/events/" + id;
        }

        model.addAttribute("event", event);
        return "events/edit";
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("event") Event event,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "events/edit";

        Optional<Event> maybeExisting = eventService.getEventById(id);
        if (maybeExisting.isEmpty()) return "redirect:/events";

        Event existing = maybeExisting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;

        if (!(username != null && existing.getAuthor() != null && existing.getAuthor().equals(username))) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/events/" + id;
        }

        event.setAuthor(existing.getAuthor()); // 작성자 보호
        Event updated = eventService.updateEvent(id, event);
        if (updated != null) {
            redirectAttributes.addFlashAttribute("message", "경조사가 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "경조사 수정에 실패했습니다.");
        }
        return "redirect:/events/" + id;
    }

    // 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Event> maybeExisting = eventService.getEventById(id);
        if (maybeExisting.isEmpty()) return "redirect:/events";

        Event existing = maybeExisting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken);
        String username = authenticated ? auth.getName() : null;
        boolean isAdmin = authenticated && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isOwner = username != null && existing.getAuthor() != null && existing.getAuthor().equals(username);

        if (!(isOwner || isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/events/" + id;
        }

        if (eventService.deleteEvent(id)) {
            redirectAttributes.addFlashAttribute("message", "경조사가 성공적으로 삭제되었습니다.");
            return "redirect:/events";
        } else {
            redirectAttributes.addFlashAttribute("error", "경조사 삭제에 실패했습니다.");
            return "redirect:/events/" + id;
        }
    }

    // 검색
    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                         @RequestParam(defaultValue = "title") String type,
                         Model model) {
        List<Event> events = "author".equals(type)
                ? eventService.searchByAuthor(keyword)
                : eventService.searchByTitle(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", type);
        return "events/search";
    }

    // 중요/인기
    @GetMapping("/important")
    public String important(Model model) {
        model.addAttribute("events", eventService.getImportantEvents());
        return "events/important";
    }

    @GetMapping("/popular")
    public String popular(Model model) {
        model.addAttribute("events", eventService.getPopularEvents());
        return "events/popular";
    }
}
