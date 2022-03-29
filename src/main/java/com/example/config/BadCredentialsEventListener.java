package com.example.config;

import com.example.domain.model.AppUserDetails;
import com.example.domain.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BadCredentialsEventListener {

    @Autowired
    private UserDetailsServiceImpl service;

    // ポイント:@EventListener
    @EventListener
    public void onBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
        log.info("BadCredentialsEvent Start");
        if (event.getException() != null && event.getException().getClass() != null
                && event.getException().getClass().equals(UsernameNotFoundException.class)) {
            log.info("ユーザーが存在しない");
            return;
        }
        final String userId = event.getAuthentication().getName();
        final AppUserDetails user = (AppUserDetails) service.loadUserByUsername(userId);

        // ログイン失敗回数を1増やす
        int loginMissTime = user.getLoginMissTimes() + 1;
        service.updateUnlock(userId, loginMissTime);
        log.info("BadCredentialsEvent End");
    }


}
