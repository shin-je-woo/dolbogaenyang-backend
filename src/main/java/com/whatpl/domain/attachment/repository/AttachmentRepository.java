package com.whatpl.domain.attachment.repository;

import com.whatpl.domain.attachment.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
