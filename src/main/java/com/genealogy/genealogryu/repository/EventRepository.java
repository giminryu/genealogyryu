package com.genealogy.genealogryu.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.genealogy.genealogryu.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContainingIgnoreCase(String keyword);
    List<Event> findByAuthorContainingIgnoreCase(String author);
    List<Event> findByImportantTrueOrderByCreatedAtDesc();
    List<Event> findTop10ByOrderByViewCountDesc();
    List<Event> findTop5ByOrderByCreatedAtDesc();
    
    // 특정 날짜의 이벤트 조회
    List<Event> findByEventDate(LocalDate eventDate);
    
    // 유형별 이벤트 수 조회
    @Query("SELECT e.type, COUNT(e) FROM Event e GROUP BY e.type")
    List<Object[]> countEventsByType();
    
    // 월별 이벤트 수 조회
    @Query("SELECT MONTH(e.eventDate), COUNT(e) FROM Event e GROUP BY MONTH(e.eventDate) ORDER BY MONTH(e.eventDate)")
    List<Object[]> countEventsByMonth();

}
