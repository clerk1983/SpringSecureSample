package com.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * セキュリティ設定クラス.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("SuccessHandler")
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    @Qualifier("UserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    //パスワードエンコーダーのBean定義
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // データソース
    @Autowired
    private DataSource dataSource;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 直リンクの禁止＆ログイン不要ページの設定
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll() // ログインページは直リンクOK
                .antMatchers("/error/session").permitAll() // セッションエラー
                .anyRequest().authenticated(); //それ以外は直リンク禁止

        // レスポンスヘッダの設定
        http
            .headers().contentSecurityPolicy("default-src 'self'");

        // HTTPSにリダイレクト
        http
            .requiresChannel().antMatchers("/login*").requiresSecure();

        // セッション管理
        http
            .sessionManagement().invalidSessionUrl("/error/session");

        //ログイン処理の実装
        http
            .formLogin()
                .loginProcessingUrl("/login") //ログイン処理のパス
                .loginPage("/login") //ログインページの指定
                .failureUrl("/login?error") //ログイン失敗時の遷移先
                .usernameParameter("userId") //ログインページのユーザーID
                .passwordParameter("password") //ログインページのパスワード
                .defaultSuccessUrl("/home", true) //ログイン成功後の遷移先
                .successHandler(successHandler); // SuccessHandlerの設定

        // ログアウト処理
        http
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID"); // 指定しないとセッションエラー扱い


        // RememberMe設定
        http
            .rememberMe()
                .key("uniqueKeyAndSecret") // トークン識別キー
                .rememberMeParameter("remember-me") // checkboxのname属性
                .rememberMeCookieName("remember-me") // Cookie名
                .tokenValiditySeconds(24 * 60 * 60) // 有効期限（秒)
                .useSecureCookie(true); // HTTPSのみ許可

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // ログイン処理時のユーザー情報を、DBから取得する
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());

    }
}
