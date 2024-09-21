package com.whatpl.domain.whatplpople.controller;

import com.whatpl.domain.whatplpople.dto.WhatplpeopleDto;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleSearchCondition;
import com.whatpl.domain.whatplpople.service.WhatplpeopleService;
import com.whatpl.global.pagination.SliceResponse;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WhatplpeopleController {

    private final WhatplpeopleService whatplPeopleService;

    @GetMapping("/me-whatplpeoples")
    public ResponseEntity<SliceResponse<WhatplpeopleDto>> searchMeWhatplpeople(Pageable pageable,
                                                                             @ModelAttribute WhatplpeopleSearchCondition searchCondition,
                                                                             @AuthenticationPrincipal MemberPrincipal principal){
        searchCondition.assignLoginMember(principal);
        List<WhatplpeopleDto> whatplPeoples = whatplPeopleService.searchWhatplpeople(pageable,searchCondition);
        SliceImpl<WhatplpeopleDto> result = new SliceImpl<>(whatplPeoples, pageable, PaginationUtils.hasNext(whatplPeoples, pageable.getPageSize()));
        return ResponseEntity.ok(new SliceResponse<>(result));
    }

    @GetMapping("/whatplpeoples-me")
    public ResponseEntity<SliceResponse<WhatplpeopleDto>> searchWhatplpeopleMe(Pageable pageable,
                                                                             @ModelAttribute WhatplpeopleSearchCondition searchCondition,
                                                                             @AuthenticationPrincipal MemberPrincipal principal){
        searchCondition.assignLoginMember(principal);
        List<WhatplpeopleDto> whatplPeoples = whatplPeopleService.searchWhatplpeopleMe(pageable,searchCondition);
        SliceImpl<WhatplpeopleDto> result = new SliceImpl<>(whatplPeoples, pageable, PaginationUtils.hasNext(whatplPeoples, pageable.getPageSize()));
        return ResponseEntity.ok(new SliceResponse<>(result));
    }

    @GetMapping("/whatplpeoples-all")
    public ResponseEntity<SliceResponse<WhatplpeopleDto>> searchAllWhatplpeople(Pageable pageable,
                                                                               @ModelAttribute WhatplpeopleSearchCondition searchCondition,
                                                                               @AuthenticationPrincipal MemberPrincipal principal){
        searchCondition.assignLoginMember(principal);
        List<WhatplpeopleDto> whatplPeoples = whatplPeopleService.searchAllWhatplpeopleMe(pageable,searchCondition);
        SliceImpl<WhatplpeopleDto> result = new SliceImpl<>(whatplPeoples, pageable, PaginationUtils.hasNext(whatplPeoples, pageable.getPageSize()));
        return ResponseEntity.ok(new SliceResponse<>(result));
    }
}
