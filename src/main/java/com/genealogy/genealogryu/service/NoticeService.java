package com.genealogy.genealogryu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genealogy.genealogryu.entity.Notice;
import com.genealogy.genealogryu.repository.NoticeRepository;

@Service
@Transactional
public class NoticeService {
    
    @Autowired
    private NoticeRepository noticeRepository;
    
    // 공지사항 등록
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }
    
    // 공지사항 조회 (ID로)
    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }
    
    // 전체 공지사항 조회
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }
    
    // 최신 공지사항 조회 (최대 10개)
    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    // 중요 공지사항 조회
    public List<Notice> getImportantNotices() {
        return noticeRepository.findByIsImportantTrueOrderByCreatedAtDesc();
    }
    
    // 인기 공지사항 조회 (조회수 높은 순)
    public List<Notice> getPopularNotices() {
        return noticeRepository.findTop5ByOrderByViewCountDesc();
    }
    
    // 제목으로 검색
    public List<Notice> searchNoticesByTitle(String title) {
        return noticeRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
    }
    
    // 작성자로 검색
    public List<Notice> searchNoticesByAuthor(String author) {
        return noticeRepository.findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc(author);
    }
    
    // 공지사항 수정
    public Notice updateNotice(Long id, Notice noticeDetails) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            notice.setTitle(noticeDetails.getTitle());
            notice.setContent(noticeDetails.getContent());
            notice.setAuthor(noticeDetails.getAuthor());
            notice.setImportant(noticeDetails.isImportant());
            return noticeRepository.save(notice);
        }
        return null;
    }
    
    // 공지사항 삭제
    public boolean deleteNotice(Long id) {
        if (noticeRepository.existsById(id)) {
            noticeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // 전체 공지사항 수 조회
    public long getNoticeCount() {
        return noticeRepository.countAllNotices();
    }
    
    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long id) {
        noticeRepository.findById(id).ifPresent(notice -> {
            notice.setViewCount(notice.getViewCount() + 1);
            noticeRepository.save(notice);
        });
    }
    
    // 중요도 토글
    public boolean toggleImportant(Long id) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            notice.setImportant(!notice.isImportant());
            noticeRepository.save(notice);
            return true;
        }
        return false;
    }
    
    // 월별 공지사항 수 조회
    public List<Object[]> getNoticeCountByMonth() {
        return noticeRepository.countNoticesByMonth();
    }
}
