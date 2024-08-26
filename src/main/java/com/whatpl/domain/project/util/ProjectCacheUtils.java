package com.whatpl.domain.project.util;

import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.domain.project.dto.ProjectSearchCondition;
import com.whatpl.domain.project.repository.project.dto.ProjectOrderType;
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
        String latest = ProjectOrderType.LATEST.name().toLowerCase();
        return pageable.getPageNumber() == 0 &&
                (pageable.getSort().isEmpty() || pageable.getSort().getOrderFor(latest) != null) &&
                searchCondition.isEmpty();
    }
}
