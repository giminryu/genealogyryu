package com.genealogy.genealogryu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.genealogy.genealogryu.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    // 중요 공지사항 조회
    List<Notice> findByIsImportantTrueOrderByCreatedAtDesc();
    
    // 제목으로 검색
    List<Notice> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    
    // 작성자로 검색
    List<Notice> findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc(String author);
    
    // 최신 공지사항 조회 (최대 10개)
    List<Notice> findTop10ByOrderByCreatedAtDesc();
    
    // 조회수 높은 순으로 조회
    List<Notice> findTop5ByOrderByViewCountDesc();
    
    // 전체 공지사항 수 조회
    @Query("SELECT COUNT(n) FROM Notice n")
    long countAllNotices();
    
    // 월별 공지사항 수 조회
    @Query("SELECT MONTH(n.createdAt), COUNT(n) FROM Notice n GROUP BY MONTH(n.createdAt) ORDER BY MONTH(n.createdAt)")
    List<Object[]> countNoticesByMonth();
}
