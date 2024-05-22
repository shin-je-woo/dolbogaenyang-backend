package com.whatpl.project.repository;

import com.whatpl.BaseRepositoryTest;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.converter.ProjectModelConverter;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectQueryRepositoryImplTest extends BaseRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void init() {
        Member writer = MemberFixture.withAll();
        memberRepository.save(writer);
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .title("1번 프로젝트")
                .subject(Subject.SOCIAL_MEDIA)
                .recruitJobs(Set.of(new ProjectCreateRequest.RecruitJobField(Job.BACKEND_DEVELOPER, 2)))
                .skills(Set.of(Skill.JAVA, Skill.FIGMA))
                .content("1번 프로젝트 내용")
                .profitable(false)
                .build();
        Project project1 = ProjectModelConverter.toProject(request, writer);
        projectRepository.save(project1);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("프로젝트 검색")
    void search() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 3);
        ProjectSearchCondition searchCondition = ProjectSearchCondition.builder()
                .status(ProjectStatus.RECRUITING)
                .job(Job.BACKEND_DEVELOPER)
                .subject(Subject.SOCIAL_MEDIA)
                .build();

        // when
        Slice<ProjectInfo> result = projectRepository.search(pageRequest, searchCondition);
        List<ProjectInfo> projectInfos = result.getContent();

        // then
        ProjectInfo projectInfo = projectInfos.get(0);
        assertEquals(1, projectInfos.size());
        assertEquals(2, projectInfo.getSkills().size());
        assertEquals(1, projectInfo.getRemainedJobs().size());
        assertEquals(2, projectInfo.getRemainedJobs().get(0).getRecruitAmount());
        assertEquals(1, projectInfo.getRemainedJobs().get(0).getRemainedAmount());
    }

}