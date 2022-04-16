package io.sephy.playwright;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microsoft.playwright.Playwright;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sephy
 * @date 2022-04-07 13:14
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
class PlaywrightWorkerEngineTest {

    @Autowired
    private PlaywrightWorkerEngine playwrightWorkerEngine;

    @Test
    public void test() {

    }

    private static class TestPlaywrightWorker extends DefaultPlaywrightWorker<Void> {

        public TestPlaywrightWorker(@NonNull PlaywrightPageWorker<Void> playwrightPageWorker) {
            super(playwrightPageWorker);
        }
    }

    @EnableConfigurationProperties(PlaywrightProperties.class)
    @Configuration
    public static class Config {

        @Bean
        public GenericObjectPool<Playwright> playwrightPool(PlaywrightProperties playwrightProperties) {
            GenericObjectPoolConfig<Playwright> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setJmxEnabled(false);
            poolConfig.setTestOnBorrow(true);
            return new GenericObjectPool<>(new PlaywrightObjectFactory(playwrightProperties.getCreateOptions()),
                poolConfig);
        }

        @Bean
        public PlaywrightWorkerEngine playwrightPoolEngine(GenericObjectPool<Playwright> playwrightPool,
            PlaywrightProperties playwrightProperties) throws Exception {
            return new PlaywrightWorkerEngine(playwrightPool, playwrightProperties);
        }
    }
}