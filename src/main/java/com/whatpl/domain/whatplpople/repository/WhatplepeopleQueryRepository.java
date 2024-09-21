package com.whatpl.domain.whatplpople.repository;

import com.whatpl.domain.whatplpople.dto.WhatplpeopleDto;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WhatplepeopleQueryRepository {
    List<WhatplpeopleDto> findWhatplpeopleMe(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition);
    List<WhatplpeopleDto> findMeWhatplpeople(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition);
    List<WhatplpeopleDto> findAllWhatplpeople(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition);
}
