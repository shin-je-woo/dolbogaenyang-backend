package com.whatpl.notification.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.notification.domain.enums.NotificationType;
import com.whatpl.notification.dto.NotificationEvent;
import com.whatpl.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal MemberPrincipal principal) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Accel-Buffering", "no"); // nginx 버퍼링 사용 X (성능 개선)
        return new ResponseEntity<>(notificationService.subscribe(1L), headers, HttpStatus.OK);
    }

    @GetMapping("/notifications/test")
    public void test(@AuthenticationPrincipal MemberPrincipal principal,
                     @RequestParam String content) {
        NotificationEvent event = NotificationEvent.of(content, NotificationType.PROJECT_APPLY);
        notificationService.notify(1L, event);
    }
}
