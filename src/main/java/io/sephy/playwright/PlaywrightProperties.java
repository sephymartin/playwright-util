package io.sephy.playwright;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author sephy
 * @date 2021-07-25 10:46
 */
@Data
@ConfigurationProperties(prefix = "playwright")
public class PlaywrightProperties {

    private String browderType;

    private String userAgent;

    private String mobileUserAgent;

    private int mobileWidth = 1920;

    private int mobileHeight = 1080;

    private String executorPath;

    private String persistPath;
}
