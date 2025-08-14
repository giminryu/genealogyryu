package com.genealogy.genealogryu.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genealogy.genealogryu.entity.Event;
import com.genealogy.genealogryu.repository.EventRepository;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event event) {
        return eventRepository.findById(id).map(existing -> {
            existing.setTitle(event.getTitle());
            existing.setContent(event.getContent());
            existing.setType(event.getType());
            existing.setEventDate(event.getEventDate());
            existing.setImportant(event.isImportant());
            return existing;
        }).orElse(null);
    }

    public boolean deleteEvent(Long id) {
        return eventRepository.findById(id).map(e -> {
            eventRepository.delete(e);
            return true;
        }).orElse(false);
    }

    public void increaseViewCount(Long id) {
        eventRepository.findById(id).ifPresent(e -> e.setViewCount(e.getViewCount() + 1));
    }

    @Transactional(readOnly = true)
    public List<Event> searchByTitle(String keyword) {
        return eventRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Event> searchByAuthor(String author) {
        return eventRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Transactional(readOnly = true)
    public List<Event> getImportantEvents() {
        return eventRepository.findByImportantTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Event> getPopularEvents() {
        return eventRepository.findTop10ByOrderByViewCountDesc();
    }

    @Transactional(readOnly = true)
    public long getEventCount() {
        return eventRepository.count();
    }
    
    // 특정 날짜의 이벤트 조회
    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return eventRepository.findByEventDate(localDate);
        } catch (Exception e) {
            return List.of();
        }
    }
    
    // 유형별 이벤트 수 조회
    @Transactional(readOnly = true)
    public List<Object[]> getEventCountByType() {
        return eventRepository.countEventsByType();
    }
    
    // 월별 이벤트 수 조회
    @Transactional(readOnly = true)
    public List<Object[]> getEventCountByMonth() {
        return eventRepository.countEventsByMonth();
    }
}
