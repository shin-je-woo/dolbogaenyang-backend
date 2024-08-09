package com.whatpl.domain.member.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.web.validator.ValidFileList;
import com.whatpl.domain.member.dto.NicknameDuplRequest;
import com.whatpl.domain.member.dto.NicknameDuplResponse;
import com.whatpl.domain.member.dto.ProfileOptionalRequest;
import com.whatpl.domain.member.dto.ProfileRequiredRequest;
import com.whatpl.domain.member.service.MemberProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
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

    @PostMapping("/optional")
    public ResponseEntity<Void> optional(@Valid @RequestPart ProfileOptionalRequest info,
                                         @Size(max = 5, message = "포트폴리오는 최대 5개 첨부 가능합니다.")
                                         @ValidFileList(message = "포트폴리오에 허용된 확장자가 아닙니다. [jpg, jpeg, png, gif, pdf]")
                                         @RequestPart(required = false) List<MultipartFile> portfolios,
                                         @AuthenticationPrincipal MemberPrincipal principal) {
        memberProfileService.updateOptionalProfile(info, portfolios, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
