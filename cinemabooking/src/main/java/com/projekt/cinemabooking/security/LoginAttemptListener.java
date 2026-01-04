package com.projekt.cinemabooking.security;

import com.projekt.cinemabooking.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAttemptListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LogRepository logRepository;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String email = (String) event.getAuthentication().getPrincipal();

        logRepository.saveLog(
                "AUTH_FAILURE",
                "Nieudana próba logowania - błędne hasło",
                email
        );
        System.out.println("LOG: Zapisano nieudane logowanie dla " + email);
    }
}