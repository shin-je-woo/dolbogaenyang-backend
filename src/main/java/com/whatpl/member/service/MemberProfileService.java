package com.whatpl.member.service;

import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;

    public NicknameDuplResponse nicknameDuplCheck(final String nickname) {
        AtomicReference<NicknameDuplResponse> result = new AtomicReference<>();
        memberRepository.findByNickname(nickname)
                .ifPresentOrElse(
                        member -> result.set(NicknameDuplResponse.of(false, nickname)),
                        () -> result.set(NicknameDuplResponse.of(true, nickname))
                );
        return result.get();
    }
}
