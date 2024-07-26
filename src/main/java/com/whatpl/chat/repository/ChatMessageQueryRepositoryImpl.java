package com.whatpl.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.chat.dto.ChatMessageDto;
import com.whatpl.global.util.PaginationUtils;
import com.whatpl.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.whatpl.chat.domain.QChatMessage.chatMessage;

@RequiredArgsConstructor
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ChatMessageDto> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        QMember sender = new QMember("sender");
        List<ChatMessageDto> list = queryFactory.select(Projections.fields(ChatMessageDto.class,
                        chatMessage.id.as("messageId"),
                        chatMessage.content.as("content"),
                        sender.id.as("senderId"),
                        sender.nickname.as("senderNickname"),
                        chatMessage.createdAt.as("sendAt"),
                        chatMessage.readAt.as("readAt")
                ))
                .from(chatMessage)
                .leftJoin(chatMessage.sender, sender)
                .where(chatMessage.chatRoom.id.eq(chatRoomId))
                .orderBy(chatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();
        return new SliceImpl<>(list, pageable, PaginationUtils.hasNext(list, pageSize));
    }
}
