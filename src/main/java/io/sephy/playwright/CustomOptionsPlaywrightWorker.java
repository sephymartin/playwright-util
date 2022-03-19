package io.sephy.playwright;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * @author sephy
 * @date 2022-02-11 22:37
 */
@Slf4j
public class CustomOptionsPlaywrightWorker<T> implements PlaywrightWorker<T> {

    private List<String> initScripts;

    private BrowserType.LaunchOptions launchOptions;

    private Browser.NewContextOptions contextOptions;

    private PlaywrightPageWorker<T> playwrightPageWorker;

    public CustomOptionsPlaywrightWorker(BrowserType.LaunchOptions launchOptions,
        Browser.NewContextOptions contextOptions, List<String> initScripts,
        PlaywrightPageWorker<T> playwrightPageWorker) {
        this.initScripts = initScripts;
        this.launchOptions = launchOptions;
        this.contextOptions = contextOptions;
        this.playwrightPageWorker = playwrightPageWorker;
    }

    @Override
    public T doWithPlaywright(Playwright playwright) {
        Browser browser = null;
        BrowserContext context = null;
        Page page = null;
        try {
            browser = playwright.chromium().launch(launchOptions); //
            context = browser.newContext(contextOptions); //
            if (CollectionUtils.isEmpty(initScripts)) {
                // The script is evaluated after the document was created but before any of its scripts were run.
                // This is useful to amend the JavaScript environment, e.g. to seed Math.random.
                for (String initScript : initScripts) {
                    context.addInitScript(initScript);
                }
            }
            page = context.newPage();
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
