package com.whatpl.chat.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.chat.domain.ChatRoom;
import com.whatpl.chat.domain.QChatMessage;
import com.whatpl.chat.domain.QChatRoom;
import com.whatpl.chat.dto.ChatRoomResponse;
import com.whatpl.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.fields;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.whatpl.chat.domain.QChatMessage.chatMessage;
import static com.whatpl.chat.domain.QChatRoom.chatRoom;
import static com.whatpl.project.domain.QApply.apply;
import static com.whatpl.project.domain.QProject.project;

@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findChatRoomWithAllById(Long id) {
        QMember writer = new QMember("writer");
        QMember applicant = new QMember("applicant");
        ChatRoom chatRoom = queryFactory.selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.apply, apply).fetchJoin()
                .leftJoin(apply.project, project).fetchJoin()
                .leftJoin(project.writer, writer).fetchJoin()
                .leftJoin(apply.applicant, applicant).fetchJoin()
                .where(QChatRoom.chatRoom.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(chatRoom);
    }

    @Override
    public List<ChatRoomResponse> findChatRooms(Pageable pageable, Long memberId) {
        QMember writer = new QMember("writer");
        QMember applicant = new QMember("applicant");

        return queryFactory.select(fields(ChatRoomResponse.class,
                        chatRoom.id.as("chatRoomId"),
                        apply.type.as("applyType"),
                        project.id.as("projectId"),
                        project.title.as("projectTitle"),
                        getOpponentId(memberId).as("opponentId"),
                        getOpponentNickname(memberId).as("opponentNickname"),
                        chatMessage.createdAt.max().as("lastMessageTime"),
                        ExpressionUtils.as(getLastMessageContent(), "lastMessageContent"),
                        getLastMessageRead(memberId).as("lastMessageRead")
                ))
                .from(chatRoom)
                .leftJoin(chatRoom.apply, apply)
                .leftJoin(apply.project, project)
                .leftJoin(project.writer, writer)
                .leftJoin(apply.applicant, applicant)
                .leftJoin(chatRoom.messages, chatMessage)
                .where(apply.applicant.id.eq(memberId)
                        .or(project.writer.id.eq(memberId)))
                .groupBy(chatRoom.id)
                .orderBy(chatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private NumberExpression<Long> getOpponentId(Long memberId) {
        return new CaseBuilder()
                .when(apply.applicant.id.eq(memberId))
                .then(project.writer.id)
                .otherwise(apply.applicant.id);
    }

    private StringExpression getOpponentNickname(Long memberId) {
        return new CaseBuilder()
                .when(apply.applicant.id.eq(memberId))
                .then(project.writer.nickname)
                .otherwise(apply.applicant.nickname);
    }

    private JPQLQuery<String> getLastMessageContent() {
        QChatMessage scalaChatMessage = new QChatMessage("scalaChatMessage");
        return select(scalaChatMessage.content)
                .from(scalaChatMessage)
                .where(scalaChatMessage.id.eq(chatMessage.id.max()));
    }

    private BooleanExpression getLastMessageRead(Long memberId) {
        QChatMessage scalaChatMessage = new QChatMessage("scalaChatMessage");
        return new CaseBuilder()
                .when(select(scalaChatMessage.sender.id)
                        .from(scalaChatMessage)
                        .where(scalaChatMessage.id.eq(chatMessage.id.max())).eq(memberId))
                .then(true)
                .otherwise(select(scalaChatMessage.readAt)
                        .from(scalaChatMessage)
                        .where(scalaChatMessage.id.eq(chatMessage.id.max())).isNotNull());
    }
}
