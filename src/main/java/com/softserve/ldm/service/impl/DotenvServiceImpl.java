package com.softserve.ldm.service.impl;

import com.softserve.ldm.constant.AppConstant;
import com.softserve.ldm.constant.ErrorMessage;
import com.softserve.ldm.exception.exceptions.BadSecretKeyException;
import com.softserve.ldm.exception.exceptions.FunctionalityNotAvailableException;
import com.softserve.ldm.service.DotenvService;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class DotenvServiceImpl implements DotenvService {
    private Dotenv dotenv;
    private final PasswordEncoder passwordEncoder;

    public DotenvServiceImpl(@Qualifier("ldm-passwordEncoder") PasswordEncoder passwordEncoder,
                             @Qualifier("ldm-dotenv") Dotenv dotenv) {
        this.passwordEncoder = passwordEncoder;
        this.dotenv = dotenv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateSecretKey(String secretKey) {
        reloadEnvFile();
        String actualKey = dotenv.get("logs.secretKey");

        if (actualKey == null || !passwordEncoder.matches(secretKey, actualKey)) {
            throw new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDotenvFile(String secretKey) {
        validateSecretKey(secretKey);

        String dotenvFilePath = System.getProperty("user.dir") + File.separator + AppConstant.DOTENV_FILENAME;
        File dotenvFile = new File(dotenvFilePath);

        try {
            if (!Files.deleteIfExists(dotenvFile.toPath())) {
                throw new FunctionalityNotAvailableException(ErrorMessage.CANNOT_DELETE_DOTENV);
            }
        } catch (IOException e) {
            throw new FunctionalityNotAvailableException(ErrorMessage.CANNOT_DELETE_DOTENV, e);
        }
    }

    /**
     * Reloads the environment variables from `*.env` file to make sure it is not
     * deleted. If the `*.env` file is missing or cannot be loaded, this method
     * throws a {@link FunctionalityNotAvailableException} to indicate that the
     * required functionality is unavailable.
     *
     *
     * @throws FunctionalityNotAvailableException if the `*.env` file cannot be
     *                                            loaded
     * @author Hrenevych Ivan
     */
    public void reloadEnvFile() {
        try {
            dotenv = Dotenv.configure()
                .filename(AppConstant.DOTENV_FILENAME)
                .load();
        } catch (DotenvException ex) {
            throw new FunctionalityNotAvailableException(ErrorMessage.FUNCTIONALITY_NOT_AVAILABLE);
        }
    }
}
