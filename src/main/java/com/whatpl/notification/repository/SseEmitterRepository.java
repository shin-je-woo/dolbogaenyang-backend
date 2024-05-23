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

    public SseEmitter save(Long emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> findById(Long emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }

    public void deleteById(Long emitterId) {
        emitters.remove(emitterId);
    }
}
