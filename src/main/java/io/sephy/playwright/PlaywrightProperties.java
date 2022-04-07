package io.sephy.playwright;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Proxy;

import lombok.Data;

/**
 * @author sephy
 * @date 2021-07-25 10:46
 */
@Data
@ConfigurationProperties(prefix = "playwright")
public class PlaywrightProperties implements InitializingBean {

    private List<Path> initScripts;

    @NestedConfigurationProperty
    private ProxySettings proxySettings;

    @NestedConfigurationProperty
    private ScreenSizeSettings screenSizeSettings;

    @NestedConfigurationProperty
    private ViewportSizeSettings viewportSizeSettings;

    @NestedConfigurationProperty
    private PageSettings pageSettings = new PageSettings();

    private Playwright.CreateOptions createOptions = new Playwright.CreateOptions();

    private BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();

    private Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

    private String browderType;

    private List<Path> extraInitScripts;

    @Override
    public void afterPropertiesSet() throws Exception {

        if (proxySettings != null && proxySettings.isUseProxy) {
            Proxy proxy = new Proxy(proxySettings.server);
            proxy.setUsername(proxySettings.username);
            proxy.setPassword(proxySettings.password);
            contextOptions.setProxy(proxy);
        }

        if (screenSizeSettings != null) {
            contextOptions.setScreenSize(screenSizeSettings.width, screenSizeSettings.height);
        }

        if (viewportSizeSettings != null) {
            contextOptions.setViewportSize(viewportSizeSettings.width, viewportSizeSettings.height);
        }

        if (initScripts != null && !initScripts.isEmpty()) {

        }
    }

    @Data
    public static class ProxySettings {

        private boolean isUseProxy = false;

        private String server;

        private String username;

        private String password;
    }

    @Data
    public static class ScreenSizeSettings {

        /**
         * page width in pixels.
         */
        public int width = 1920;
        /**
         * page height in pixels.
         */
        public int height = 1080;
    }

    @Data
    public static class ViewportSizeSettings {

        public int width = 1920;
        /**
         * page height in pixels.
         */
        public int height = 1080;
    }

    @Data
    public static class PageSettings {

        double defaultTimeout = 3000D;
    }
}
