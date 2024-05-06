package com.whatpl.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.chat.domain.ChatRoom;
import com.whatpl.chat.domain.QChatRoom;
import com.whatpl.member.domain.QMember;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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
                .fetchOne();
        return Optional.ofNullable(chatRoom);
    }
}
