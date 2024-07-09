package com.whatpl.project.repository;

import com.whatpl.project.domain.*;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProjectQueryRepository {

    Optional<Project> findProjectWithParticipantsById(Long id);

    List<ProjectInfo> findProjectInfos(Pageable pageable, ProjectSearchCondition searchCondition);

    Map<Long, List<ProjectSkill>> findProjectSkillMap(List<Long> projectIds);

    Map<Long, List<RecruitJob>> findRecruitJobMap(List<Long> projectIds);

    Map<Long, List<ProjectParticipant>> findParticipantMap(List<Long> projectIds);

    Map<Long, List<ProjectLike>> findProjectLikeMap(List<Long> projectIds);

    Map<Long, List<ProjectComment>> findProjectCommentMap(List<Long> projectIds);
}
