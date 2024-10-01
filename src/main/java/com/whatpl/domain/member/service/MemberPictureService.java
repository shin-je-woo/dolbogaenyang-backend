package com.whatpl.domain.member.service;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.attachment.event.AttachmentDeleteEvent;
import com.whatpl.domain.attachment.service.AttachmentService;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberPictureService {

    private final AttachmentService attachmentService;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    @Transactional
    public void modifyPicture(Member member, MultipartFile picture) {
        Attachment newPicture = attachmentService.upload(picture);
        Attachment oldPicture = member.getPicture();
        member.modifyPicture(newPicture);
        if(oldPicture != null) {
            eventPublisher.publishEvent(AttachmentDeleteEvent.from(oldPicture.getStoredName()));
        }
    }

    @Transactional(readOnly = true)
    public ResourceDto readPicture(Long pictureId) {
        Member member = memberRepository.findByPictureId(pictureId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA));
        Attachment attachment = member.getPicture();
        return attachmentService.download(attachment.getId());
    }
}
