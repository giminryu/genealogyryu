package com.genealogy.genealogryu.controller;

import com.genealogy.genealogryu.entity.Member;
import com.genealogy.genealogryu.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    // 회원 목록 조회
    @GetMapping
    public String listMembers(Model model) {
        List<Member> members = memberService.getActiveMembers();
        model.addAttribute("members", members);
        model.addAttribute("totalMembers", memberService.getActiveMemberCount());
        return "member/list";
    }
    
    // 회원 등록 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("member", new Member());
        return "member/register";
    }
    
    // 회원 등록 처리
    @PostMapping("/register")
    public String registerMember(@Valid @ModelAttribute Member member, 
                                BindingResult result, 
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "member/register";
        }
        
        // 전화번호 중복 확인
        if (memberService.getMemberByPhone(member.getPhone()).isPresent()) {
            result.rejectValue("phone", "error.phone", "이미 등록된 전화번호입니다.");
            return "member/register";
        }
        
        // 이메일 중복 확인 (이메일이 있는 경우)
        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            if (memberService.getMemberByEmail(member.getEmail()).isPresent()) {
                result.rejectValue("email", "error.email", "이미 등록된 이메일입니다.");
                return "member/register";
            }
        }
        
        memberService.registerMember(member);
        redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 등록되었습니다.");
        return "redirect:/members";
    }
    
    // 회원 상세 조회
    @GetMapping("/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        return memberService.getMemberById(id)
                .map(member -> {
                    model.addAttribute("member", member);
                    return "member/view";
                })
                .orElse("redirect:/members");
    }
    
    // 회원 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return memberService.getMemberById(id)
                .map(member -> {
                    model.addAttribute("member", member);
                    return "member/edit";
                })
                .orElse("redirect:/members");
    }
    
    // 회원 수정 처리
    @PostMapping("/{id}/edit")
    public String editMember(@PathVariable Long id, 
                            @Valid @ModelAttribute Member member, 
                            BindingResult result, 
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "member/edit";
        }
        
        Member updatedMember = memberService.updateMember(id, member);
        if (updatedMember != null) {
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 정보 수정에 실패했습니다.");
        }
        return "redirect:/members/" + id;
    }
    
    // 회원 삭제 (비활성화)
    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (memberService.deactivateMember(id)) {
            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 삭제에 실패했습니다.");
        }
        return "redirect:/members";
    }
    
    // 회원 검색
    @GetMapping("/search")
    public String searchMembers(@RequestParam String keyword, 
                               @RequestParam(defaultValue = "name") String type, 
                               Model model) {
        List<Member> members;
        switch (type) {
            case "phone":
                members = memberService.getMemberByPhone(keyword)
                        .map(List::of)
                        .orElse(List.of());
                break;
            case "email":
                members = memberService.getMemberByEmail(keyword)
                        .map(List::of)
                        .orElse(List.of());
                break;
            case "address":
                members = memberService.searchMembersByAddress(keyword);
                break;
            case "familyRank":
                members = memberService.getMembersByFamilyRank(keyword);
                break;
            default:
                members = memberService.searchMembersByName(keyword);
                break;
        }
        
        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", type);
        return "member/search";
    }
    
    // 회원 통계
    @GetMapping("/statistics")
    public String memberStatistics(Model model) {
        model.addAttribute("totalMembers", memberService.getActiveMemberCount());
        model.addAttribute("membersByRank", memberService.getMemberCountByFamilyRank());
        model.addAttribute("recentMembers", memberService.getRecentMembers());
        model.addAttribute("membersByRegion", memberService.getMemberCountByRegion());
        return "member/statistics";
    }
    
    // 회원 일괄 관리
    @GetMapping("/bulk")
    public String bulkManagement(Model model) {
        model.addAttribute("members", memberService.getActiveMembers());
        return "member/bulk";
    }
    
    // 회원 일괄 삭제
    @PostMapping("/bulk/delete")
    public String bulkDelete(@RequestParam List<Long> memberIds, 
                            RedirectAttributes redirectAttributes) {
        int deletedCount = memberService.bulkDeactivateMembers(memberIds);
        redirectAttributes.addFlashAttribute("message", 
            deletedCount + "명의 회원이 성공적으로 삭제되었습니다.");
        return "redirect:/members";
    }
    
    // 회원 활성화/비활성화 토글
    @PostMapping("/{id}/toggle-status")
    public String toggleMemberStatus(@PathVariable Long id, 
                                    RedirectAttributes redirectAttributes) {
        boolean success = memberService.toggleMemberStatus(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "회원 상태가 변경되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 상태 변경에 실패했습니다.");
        }
        return "redirect:/members";
    }
    
    // 회원 내보내기 (CSV)
    @GetMapping("/export")
    public String exportMembers(Model model) {
        List<Member> members = memberService.getActiveMembers();
        model.addAttribute("members", members);
        return "member/export";
    }
}
