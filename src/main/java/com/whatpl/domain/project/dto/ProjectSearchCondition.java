package com.whatpl.domain.project.dto;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.whatpl.domain.project.domain.enums.ProjectStatus;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.whatpl.domain.project.domain.QProject.project;

@Getter
@Builder
@AllArgsConstructor
public class ProjectSearchCondition {

    private Subject subject;
    private Job job;
    private Skill skill;
    private ProjectStatus status;
    private Boolean profitable;
    private String keyword;
    private Long longinMemberId;

    public void assignLoginMember(MemberPrincipal principal) {
        if (longinMemberId != null) {
            return;
        }
        longinMemberId = principal != null ? principal.getId() : Long.MIN_VALUE;
    }

    public boolean isEmpty() {
        return this.subject == null &&
                this.job == null &&
                this.skill == null &&
                this.profitable == null &&
                !StringUtils.hasText(this.keyword);
    }

    public enum OrderType {
        LATEST {
            @Override
            public Optional<OrderSpecifier<? extends Comparable<?>>> getOrderSpecifier(Sort.Order order) {
                if (order.getProperty().equalsIgnoreCase(name())) {
                    return Optional.of(new OrderSpecifier<>(Order.DESC, project.createdAt));
                }
                return Optional.empty();
            }
        },
        POPULAR {
            @Override
            public Optional<OrderSpecifier<? extends Comparable<?>>> getOrderSpecifier(Sort.Order order) {
                if (order.getProperty().equalsIgnoreCase(ProjectSearchCondition.OrderType.POPULAR.name())) {
                    return Optional.of(new OrderSpecifier<>(Order.DESC, project.views));
                }
                return Optional.empty();
            }
        };

        public abstract Optional<OrderSpecifier<? extends Comparable<?>>> getOrderSpecifier(Sort.Order order);
    }
}
