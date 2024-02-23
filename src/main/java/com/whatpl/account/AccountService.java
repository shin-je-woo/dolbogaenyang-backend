package com.whatpl.account;

import com.whatpl.security.domain.AccountPrincipal;
import com.whatpl.security.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private Account createAccount(OAuth2UserInfo oAuth2UserInfo) {
        Account account = Account.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .registrationId(oAuth2UserInfo.getRegistrationId())
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
        return accountRepository.save(account);
    }

    @Transactional
    public AccountPrincipal getOrCreateAccount(OAuth2UserInfo oAuth2UserInfo) {
        Account account = accountRepository.findByRegistrationIdAndProviderId(
                        oAuth2UserInfo.getRegistrationId(),
                        oAuth2UserInfo.getProviderId())
                .orElseGet(() -> createAccount(oAuth2UserInfo));

        return AccountPrincipal.of(account);
    }
}
