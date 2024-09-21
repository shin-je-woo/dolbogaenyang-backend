package com.whatpl.domain.whatplpople.service;

import com.whatpl.domain.whatplpople.dto.WhatplpeopleDto;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleSearchCondition;
import com.whatpl.domain.whatplpople.repository.WhatplpeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WhatplpeopleService {
    private final WhatplpeopleRepository whatplpeopleRepository;

    public List<WhatplpeopleDto> searchWhatplpeople(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition){
        return whatplpeopleRepository.findWhatplpeopleMe(pageable, whatplpeopleSearchCondition);
    }

    public List<WhatplpeopleDto> searchWhatplpeopleMe(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition){
        return whatplpeopleRepository.findMeWhatplpeople(pageable, whatplpeopleSearchCondition);
    }

    public List<WhatplpeopleDto> searchAllWhatplpeopleMe(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition){
        return whatplpeopleRepository.findAllWhatplpeople(pageable, whatplpeopleSearchCondition);
    }
}
