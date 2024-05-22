package com.whatpl.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    // thread-safe한 자료구조를 사용한다.
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long eventId, SseEmitter sseEmitter) {
        emitters.put(eventId, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> findById(Long eventId) {
        return Optional.ofNullable(emitters.get(eventId));
    }

    public void deleteById(Long eventId) {
        emitters.remove(eventId);
    }
}
