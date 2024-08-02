package com.whatpl.project.util;

import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectSearchCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectCacheUtils {

    public static String buildListCacheKey(ProjectSearchCondition searchCondition) {
        return searchCondition.getStatus() != null ?
                searchCondition.getStatus().name().toLowerCase() :
                ProjectStatus.ALL.name().toLowerCase();
    }

    public static boolean isCacheable(Pageable pageable, ProjectSearchCondition searchCondition) {
        String latest = ProjectSearchCondition.OrderType.LATEST.name().toLowerCase();
        return pageable.getPageNumber() == 0 &&
                (pageable.getSort().isEmpty() || pageable.getSort().getOrderFor(latest) != null) &&
                searchCondition.isEmpty();
    }
}
