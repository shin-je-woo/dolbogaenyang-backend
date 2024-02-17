package com.whatpl.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByRegistrationIdAndProviderId(String registrationId, String providerId);
}
