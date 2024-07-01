package com.whatpl.global.listener;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.WorkTime;
import com.whatpl.member.domain.*;
import com.whatpl.member.domain.enums.SocialType;
import com.whatpl.member.repository.MemberRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private static final AtomicLong memberCount = new AtomicLong(0L);
    private final MemberRepository memberRepository;
    private final RedissonClient redissonClient;

    @Override
    @Transactional
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        log.info("===== Application 로딩 후 DATA 초기화 작업을 시작합니다. =====");
        RLock lock = redissonClient.getLock("lock_application_init");
        try {
            boolean isLocked = lock.tryLock(10, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("Application 초기화 Lock 획득 실패");
                return;
            }

            setupInitMembers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Application 초기화 쓰레드가 인터럽트 되었습니다.");
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        log.info("===== Application 로딩 후 DATA 초기화 작업을 종료합니다. =====");
    }

    private void setupInitMembers() {
        createMemberIfNotFound(Set.of(Skill.JAVA, Skill.MYSQL));
        createMemberIfNotFound(Set.of(Skill.FIGMA, Skill.KOTLIN));
        createMemberIfNotFound(Set.of(Skill.PYTHON, Skill.TYPE_SCRIPT));
    }

    /**
     * 필수정보 입력된 사용자 생성
     */
    private void createMemberIfNotFound(Set<Skill> skills) {
        SocialType fixedSocialType = SocialType.NAVER;
        String fixedNickname = "왓플테스트멤버" + memberCount.incrementAndGet();
        Member member = memberRepository.findMemberWithAllBySocialTypeId(fixedSocialType, fixedNickname)
                .orElseGet(() -> Member.builder()
                        .nickname(fixedNickname)
                        .job(Job.BACKEND_DEVELOPER)
                        .career(Career.NONE)
                        .workTime(WorkTime.LESS_THAN_TEN)
                        .socialType(fixedSocialType)
                        .socialId(fixedNickname)
                        .profileOpen(false)
                        .build());
        member.modifyMemberSkills(skills);
        memberRepository.save(member);
    }
}
