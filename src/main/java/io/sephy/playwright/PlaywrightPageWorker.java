package io.sephy.playwright;

import com.microsoft.playwright.Page;

/**
 * @author sephy
 * @date 2022-02-11 22:46
 */
public interface PlaywrightPageWorker<T> {

    T doWithPage(Page page);
}
