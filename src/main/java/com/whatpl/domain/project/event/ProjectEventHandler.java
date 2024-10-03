package com.whatpl.domain.project.event;

import com.whatpl.domain.chat.service.ChatMessageService;
import com.whatpl.domain.project.domain.Apply;
import com.whatpl.domain.project.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventHandler {

    private final ProjectRepository projectRepository;
    private final ChatMessageService chatMessageService;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProjectReadEvent(ProjectReadEvent event) {
        Long projectId = event.getProjectId();
        projectRepository.increaseViews(projectId);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProjectApplyEvent(ProjectApplyEvent event) {
        Apply apply = event.getApply();
        chatMessageService.sendMessage(
                apply.getChatRoom().getId(),
                apply.getChatRoom().getRoomMaker(),
                event.getApplyContent()
        );
    }
}
