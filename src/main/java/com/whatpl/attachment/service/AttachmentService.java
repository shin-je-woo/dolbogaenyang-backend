package com.whatpl.attachment.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.attachment.dto.ResourceDto;
import com.whatpl.attachment.repository.AttachmentRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.upload.FileUploader;
import com.whatpl.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileUploader fileUploader;

    @Transactional
    public Long upload(MultipartFile multipartFile) {
        String storedName = fileUploader.upload(multipartFile);
        String mimeType = FileUtils.extractMimeType(multipartFile);
        Attachment attachment = Attachment.builder()
                .fileName(multipartFile.getOriginalFilename())
                .storedName(storedName)
                .mimeType(mimeType)
                .build();
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return savedAttachment.getId();
    }

    public ResourceDto download(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_FILE));
        Resource resource = fileUploader.download(attachment.getStoredName());
        return ResourceDto.builder()
                .fileName(attachment.getFileName())
                .resource(resource)
                .mimeType(attachment.getMimeType())
                .build();
    }

    public Attachment findById(long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_FILE));
    }
}
