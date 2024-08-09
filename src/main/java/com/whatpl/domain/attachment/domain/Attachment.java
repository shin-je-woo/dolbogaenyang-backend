package com.whatpl.domain.attachment.domain;

import com.whatpl.global.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "attachment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String storedName;

    private String mimeType;

    @Builder
    public Attachment(String fileName, String storedName, String mimeType) {
        this.fileName = fileName;
        this.storedName = storedName;
        this.mimeType = mimeType;
    }
}
