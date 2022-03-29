package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * system ユーザーでログイン実行
     */
    @Test
    public void loginTestSystem() throws Exception {
        // ログイン処理
        mockMvc.perform(post("https://sample.localhost.com:8443/login")
                .with(csrf())
                .param("userId", "system")
                .param("password", "password"))
            .andExpect(status().isFound())
            .andExpect(authenticated()
                    .withUsername("system").withRoles("ADMIN", "GENERAL"))
            .andExpect(redirectedUrl("/home"));

    }

    /**
     * sample1 ユーザーでログイン実行
     * [期限切れ]
     */
    @Test
    public void loginTestSample1() throws Exception {
        // ログイン処理
        mockMvc.perform(post("https://sample.localhost.com:8443/login")
                        .with(csrf())
                        .param("userId", "sample1")
                        .param("password", "password"))
                .andExpect(status().isFound())
                .andExpect(authenticated()
                        .withUsername("sample1").withRoles("GENERAL"))
                .andExpect(redirectedUrl("/password/change"));
    }

    /**
     * sample2 ユーザーでログイン実行
     * [ロック中]
     */
    @Test
    public void loginTestSample2() throws Exception {
        // ログイン処理
        mockMvc.perform(post("https://sample.localhost.com:8443/login")
                        .with(csrf())
                        .param("userId", "sample2")
                        .param("password", "password"))
                .andExpect(status().isFound())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

}
