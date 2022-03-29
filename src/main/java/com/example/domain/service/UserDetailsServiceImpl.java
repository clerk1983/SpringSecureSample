package com.example.domain.service;

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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginUserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

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

}
