package com.ivan.softserve.ldm.config;

import com.ivan.softserve.ldm.constant.AppConstant;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdmDotenvConfig {
    @Bean("ldm-dotenv")
    Dotenv dotenv() {
        return Dotenv.configure()
                .filename(AppConstant.DOTENV_FILENAME)
                .ignoreIfMissing()
                .load();
    }
}