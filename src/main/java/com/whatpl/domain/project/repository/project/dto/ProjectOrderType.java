package com.whatpl.domain.project.repository.project.dto;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;

import static com.whatpl.domain.project.domain.QProject.project;

@RequiredArgsConstructor
public enum ProjectOrderType {
    LATEST(ProjectOrderSpecifier.Latest.getInstance()),
    POPULAR(ProjectOrderSpecifier.Popular.getInstance());

    private final OrderSpecifier<? extends Comparable<?>> orderSpecifier;

    public static Optional<OrderSpecifier<? extends Comparable<?>>> getOrderSpecifier(Sort.Order order) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(order.getProperty()))
                .findFirst()
                .map(orderType -> orderType.orderSpecifier);
    }

    private static class ProjectOrderSpecifier {

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        static class Latest {
            static class LatestInstanceHolder {
                private static final OrderSpecifier<? extends Comparable<?>> INSTANCE = new OrderSpecifier<>(Order.DESC, project.createdAt);
            }

            static OrderSpecifier<? extends Comparable<?>> getInstance() {
                return LatestInstanceHolder.INSTANCE;
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        static class Popular {
            static class PopularInstanceHolder {
                private static final OrderSpecifier<? extends Comparable<?>> INSTANCE = new OrderSpecifier<>(Order.DESC, project.views);
            }

            static OrderSpecifier<? extends Comparable<?>> getInstance() {
                return PopularInstanceHolder.INSTANCE;
            }
        }
    }
}
