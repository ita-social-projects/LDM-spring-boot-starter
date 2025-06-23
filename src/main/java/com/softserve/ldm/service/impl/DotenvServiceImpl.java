package com.softserve.ldm.service.impl;

import com.softserve.ldm.constant.ErrorMessage;
import com.softserve.ldm.exception.exceptions.BadSecretKeyException;
import com.softserve.ldm.service.DotenvService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DotenvServiceImpl implements DotenvService {

    private final Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateSecretKey(String secretKey) {
        String actualKey = environment.getProperty("LDM_SECRET_KEY");

        if (actualKey == null || !actualKey.equals(secretKey)) {
            throw new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY);
        }
    }
}
