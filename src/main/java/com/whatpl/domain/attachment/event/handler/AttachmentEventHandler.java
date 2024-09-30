package com.whatpl.domain.attachment.event.handler;

import com.whatpl.domain.attachment.event.AttachmentDeleteEvent;
import com.whatpl.external.upload.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AttachmentEventHandler {

    private final FileUploader fileUploader;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDelete(AttachmentDeleteEvent event) {
        fileUploader.delete(event.getStoredName());
    }
}
