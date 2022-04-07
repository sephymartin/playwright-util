package io.sephy.playwright;

import java.nio.file.Path;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sephy
 * @date 2022-02-11 22:09
 */
@Slf4j
public class DefaultPlaywrightWorker<T> {

    private PlaywrightPageWorker<T> playwrightPageWorker;

    public DefaultPlaywrightWorker(@NonNull PlaywrightPageWorker<T> playwrightPageWorker) {
        this.playwrightPageWorker = playwrightPageWorker;
    }

    public T doWithPlaywright(Playwright playwright) {
        return doWithPlaywright(playwright, null);
    }

    public T doWithPlaywright(Playwright playwright, PlaywrightProperties properties) {
        BrowserType.LaunchOptions options = properties != null ? properties.getLaunchOptions() : null;
        Browser.NewContextOptions contextOptions = properties != null ? properties.getContextOptions() : null;

        BrowserType browserType = playwright.chromium();
        if (properties != null && properties.getBrowderType() != null) {
            if ("chromium".equals(properties.getBrowderType())) {
                browserType = playwright.chromium();
            } else if ("firefox".equals(properties.getBrowderType())) {
                browserType = playwright.firefox();
            } else if ("webkit".equals(properties.getBrowderType())) {
                browserType = playwright.webkit();
            }
        }
        Browser browser = null;
        BrowserContext context = null;
        Page page = null;
        try {
            browser = browserType.launch(options); //
            context = browser.newContext(contextOptions); //
            if (properties != null && properties.getInitScripts() != null && !properties.getInitScripts().isEmpty()) {
                for (Path path : properties.getInitScripts()) {
                    context.addInitScript(path);
                }
            }
            page = context.newPage();
            PlaywrightProperties.PageSettings pageSettings = properties.getPageSettings();
            if (pageSettings != null) {
                page.setDefaultTimeout(pageSettings.getDefaultTimeout());
            }
            return playwrightPageWorker.doWithPage(page);
        } finally {
            if (page != null) {
                try {
                    page.close();
                } catch (Exception e) {
                    log.error("close page error", e);
                }
            }
            if (context != null) {
                try {
                    context.close();
                } catch (Exception e) {
                    log.error("close context error", e);
                }
            }
            if (browser != null) {
                try {
                    browser.close();
                } catch (Exception e) {
                    log.error("browser close error", e);
                }
            }
            if (playwright != null) {
                try {
                    playwright.close();
                } catch (Exception e) {
                    log.error("close playwright error", e);
                }
            }

        }
    }
}
