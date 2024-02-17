package com.whatpl.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_registration_provider",
                        columnNames = {"registration_id", "provider_id"})})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(name = "registration_id", length = 30)
    private String registrationId;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Builder
    public Account(String name, String email, String registrationId, String providerId) {
        this.name = name;
        this.email = email;
        this.registrationId = registrationId;
        this.providerId = providerId;
    }
}
