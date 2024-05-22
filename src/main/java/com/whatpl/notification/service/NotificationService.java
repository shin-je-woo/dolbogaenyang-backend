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
import org.springframework.http.MediaType;
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

        // 구독 시 사용자가 읽지 않은 알림 갯수 데이터 전송
        int unreadCount = notificationRepository.findUnreadCount(memberId);
        publish(sseEmitter, memberId, unreadCount);
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
     */
    private void publish(@NonNull final SseEmitter sseEmitter, final Long eventId, final Object eventPayload) {
        try {
            // 이벤트 데이터 전송
            sseEmitter.send(SseEmitter.event()
                    .id(String.valueOf(eventId))
                    .data(eventPayload, MediaType.APPLICATION_JSON)); // data가 메시지만 포함된다면 타입을 지정해줄 필요는 없다.
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            sseEmitterRepository.deleteById(eventId);
        }
    }

    @Transactional
    public void notify(final long receiverId, @NonNull final NotificationEvent event) {
        // Notification을 저장한다.
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        Notification notification = Notification.of(event.getNotificationType(), receiver);
        notificationRepository.save(notification);

        // 수신자와 연결된 sseEmitter를 찾아서 event를 발송한다.
        sseEmitterRepository.findById(receiver.getId())
                .ifPresent(emitter -> publish(emitter, receiver.getId(), event));
    }
}