package com.whatpl.domain.chat.repository.message;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.domain.attachment.domain.QAttachment;
import com.whatpl.domain.chat.dto.ChatMessageDto;
import com.whatpl.domain.member.domain.QMember;
import com.whatpl.global.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.whatpl.domain.chat.domain.QChatMessage.chatMessage;

@RequiredArgsConstructor
public class ChatMessageQueryRepositoryImpl implements ChatMessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ChatMessageDto> findMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        QMember sender = new QMember("sender");
        QAttachment senderPicture = new QAttachment("senderPicture");
        List<ChatMessageDto> list = queryFactory.select(Projections.fields(ChatMessageDto.class,
                        chatMessage.id.as("messageId"),
                        chatMessage.content.as("content"),
                        sender.id.as("senderId"),
                        sender.nickname.as("senderNickname"),
                        senderPicture.id.as("senderPictureId"),
                        chatMessage.createdAt.as("sendAt"),
                        chatMessage.readAt.as("readAt")
                ))
                .from(chatMessage)
                .leftJoin(chatMessage.sender, sender)
                .leftJoin(chatMessage.sender.picture, senderPicture)
                .where(chatMessage.chatRoom.id.eq(chatRoomId))
                .orderBy(chatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();
        return new SliceImpl<>(list, pageable, PaginationUtils.hasNext(list, pageSize));
    }
}
