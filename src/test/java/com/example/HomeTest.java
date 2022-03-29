package com.example;

import com.example.controller.HomeController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HomeController homeController;

    /**
     * system ユーザでログインして /home を表示
     */
    @Test
    @WithUserDetails(value="system", userDetailsServiceBeanName="UserDetailsServiceImpl")
    public void homeTestSystem() throws Exception {
        mockMvc
            .perform(get("/home"))
            .andExpect(content()
                    .string(containsString("ユーザーID：<span>system</span>")))
            .andExpect(header()
                    .exists("Content-Security-Policy"))
                .andExpect(header()
                    .string("Content-Security-Policy", "default-src 'self'"));
    }

    /**
     * sample2 ユーザでログインして /home を表示
     */
    @Test
    @WithUserDetails(value="sample2", userDetailsServiceBeanName="UserDetailsServiceImpl")
    public void homeTestSample2() throws Exception {
        mockMvc
                .perform(get("/home"))
                .andExpect(content()
                        .string(containsString("ユーザーID：<span>sample2</span>")));
    }


    /**
     * system ユーザでログインして /home2 を表示
     */
    @Test
    @WithUserDetails(value="system", userDetailsServiceBeanName="UserDetailsServiceImpl")
    public void home2TestSystem() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> {
            // メソッドにアクセスできるため、NullPointerException例外が発生
            homeController.getHome2(null, null);
        });

    }

    /**
     * sample2 ユーザでログインして /home2 を表示
     */
    @Test
    @WithUserDetails(value="sample2", userDetailsServiceBeanName="UserDetailsServiceImpl")
    public void home2TestSample2() throws Exception {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            // アクセス拒否例外
            homeController.getHome2(null, null);
        });
    }

    /**
     * system ユーザでログインして ログアウト
     */
    @Test
    @WithUserDetails(value="system", userDetailsServiceBeanName="UserDetailsServiceImpl")
    public void logoutTestSystem() throws Exception {
        mockMvc
            .perform(logout("/logout"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/login"));
    }


}
