package com.whatpl.notification.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.notification.domain.Notification;
import com.whatpl.notification.dto.NotificationEvent;
import com.whatpl.notification.repository.NotificationRepository;
import com.whatpl.notification.repository.SseEmitterRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseEmitterRepository sseEmitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private static final Long TIME_OUT = 60000L;

    public SseEmitter subscribe(final long memberId) {
        SseEmitter sseEmitter = createSseEmitter(memberId);
        publish(sseEmitter, memberId);
        return sseEmitter;
    }

    private SseEmitter createSseEmitter(final long memberId) {
        // sseEmitter를 생성하고 저장소에 저장한다. (기존의 연결은 삭제)
        SseEmitter sseEmitter = new SseEmitter(TIME_OUT);
        sseEmitterRepository.deleteById(memberId);
        sseEmitterRepository.save(memberId, sseEmitter);

        // SseEmitter 의 완료/시간초과/에러로 인한 전송 불가 시 sseEmitter 삭제
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError(e -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteById(memberId));

        return sseEmitter;
    }

    /**
     * 알림 이벤트 발생 시 저장소에서 sseEmitter를 조회하여 publish 한다.
     * 안읽은 알림 갯수만 발송
     */
    private void publish(@NonNull final SseEmitter sseEmitter, long receiverId) {
        int unreadCount = notificationRepository.findUnreadCount(receiverId);
        String eventId = String.format("event_%d_%d", receiverId, System.currentTimeMillis());
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(unreadCount));
        } catch (IOException | IllegalStateException e) {
            log.error("이벤트 send 중 에러가 발생했습니다. 이벤트를 전송하지 않고 SseEmmiter를 삭제합니다.", e);
            sseEmitterRepository.deleteById(receiverId);
        }
    }

    /**
     * 알림 저장 및 알림 발송
     */
    @Transactional
    public void notify(@NonNull final NotificationEvent event) {
        // Notification을 저장한다.
        Member receiver = memberRepository.findById(event.getReceiverId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        Notification notification = Notification.of(event.getNotificationType(), receiver, event.getRelatedId());
        notificationRepository.save(notification);

        // 수신자와 연결된 sseEmitter를 찾아서 event를 발송한다.
        sseEmitterRepository.findById(receiver.getId())
                .ifPresent(emitter -> publish(emitter, receiver.getId()));
    }
}