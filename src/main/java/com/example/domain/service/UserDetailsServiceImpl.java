package com.example.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.domain.repository.LoginUserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("UserDetailsServiceImpl")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginUserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

    /** ログイン失敗の上限回数 */
    private static final int LOGIN_MISS_LIMIT = 3;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        //リポジトリー(DAO)からUserDetailsを取得
        return repository.selectOne(username);
    }

    /**
     * パスワード更新
     */
    public void updatePasswordDate(final String userId, final String password) throws ParseException {
        // パスワード暗号化
        final String encryptPass = encoder.encode(password);
        final Date date = new SimpleDateFormat("yyyy/MM/dd").parse("2099/12/31");
        repository.updatePassword(userId, encryptPass, date);
    }

    /**
     * 失敗回数と有効 / 無効 フラグを更新
     */
    public void updateUnlock(final String userId, final int loginMissTime) {
        // 有効・無効フラグ（有効）
        boolean unlock = true;
        if (loginMissTime >= LOGIN_MISS_LIMIT) {
            unlock = false; // 無効
            log.info(userId + " is LOCKED.");
        }
        repository.updateUnlock(userId, loginMissTime, unlock);
    }

}
