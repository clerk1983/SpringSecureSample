package com.example.controller;

import com.example.domain.model.AppUserDetails;
import com.example.domain.model.PasswordForm;
import com.example.domain.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;

@Controller
@Slf4j
public class PasswordChangeController {

    @Autowired
    UserDetailsServiceImpl service;

    /** パスワード変更画面の表示 */
    @GetMapping("/password/change")
    public String getPasswordChange(@ModelAttribute final PasswordForm form) {

        return "password_change";
    }

    /** パスワード変更 */
    @PostMapping("/password/change")
    public String postPasswordChange(@ModelAttribute final PasswordForm form,
                             @AuthenticationPrincipal final AppUserDetails user) throws ParseException {

        service.updatePasswordDate(user.getUserId(), form.getPassword());
        return "home";
    }

}

