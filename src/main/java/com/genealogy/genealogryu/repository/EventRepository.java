package com.genealogy.genealogryu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genealogy.genealogryu.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContainingIgnoreCase(String keyword);
    List<Event> findByAuthorContainingIgnoreCase(String author);
    List<Event> findByImportantTrueOrderByCreatedAtDesc();
    List<Event> findTop10ByOrderByViewCountDesc();
    List<Event> findTop5ByOrderByCreatedAtDesc();

}
