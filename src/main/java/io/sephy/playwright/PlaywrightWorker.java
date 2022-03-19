package io.sephy.playwright;

import com.microsoft.playwright.Playwright;

/**
 * @author sephy
 * @date 2022-02-11 22:09
 */
public interface PlaywrightWorker<T> {

    T doWithPlaywright(Playwright playwright);
}
