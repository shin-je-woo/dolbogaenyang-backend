package com.whatpl.member.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.member.dto.NicknameDuplRequest;
import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.dto.ProfileRequiredRequest;
import com.whatpl.member.service.MemberProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberProfileService memberProfileService;

    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameDuplResponse> checkNickname(@Valid @ModelAttribute NicknameDuplRequest request) {
        NicknameDuplResponse response = memberProfileService.nicknameDuplCheck(request.getNickname());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/required")
    public ResponseEntity<Void> required(@Valid @RequestBody ProfileRequiredRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {
        memberProfileService.updateRequiredProfile(request, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
