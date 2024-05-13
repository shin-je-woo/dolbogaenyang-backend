package com.whatpl.project.controller;

import com.whatpl.project.service.ProjectParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectParticipantController {

    private final ProjectParticipantService projectParticipantService;

    @PreAuthorize("hasPermission(#participantId, 'PARTICIPANT', 'DELETE')")
    @DeleteMapping("/projects/{projectId}/participants/{participantId}")
    public ResponseEntity<Void> delete(@PathVariable long projectId,
                                       @PathVariable long participantId) {
        projectParticipantService.deleteParticipant(projectId, participantId);
        return ResponseEntity.noContent().build();
    }
}
