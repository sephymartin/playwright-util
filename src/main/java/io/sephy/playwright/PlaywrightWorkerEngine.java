package io.sephy.playwright;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sephy
 * @date 2022-02-11 22:06
 */
@Slf4j
public class PlaywrightWorkerEngine {

    private GenericObjectPool<Playwright> playwrightObjectPool;

    private PlaywrightProperties playwrightProperties;

    public PlaywrightWorkerEngine(GenericObjectPool<Playwright> playwrightObjectPool,
        PlaywrightProperties playwrightProperties) {
        this.playwrightObjectPool = playwrightObjectPool;
        this.playwrightProperties = playwrightProperties;
    }

    public <E> E doWithPlaywright(PlaywrightPageWorker<E> worker) {
        return doWithPlaywright(new DefaultPlaywrightWorker<>(worker), playwrightProperties);
    }

    public <E> E doWithPlaywright(DefaultPlaywrightWorker<E> worker) {
        return doWithPlaywright(worker, playwrightProperties);
    }

    public <E> E doWithPlaywright(DefaultPlaywrightWorker<E> worker, PlaywrightProperties playwrightProperties) {
        Playwright playwright = null;
        try {
            playwright = playwrightObjectPool.borrowObject();
            return worker.doWithPlaywright(playwright, playwrightProperties);
        } catch (Exception e) {
            throw new PlaywrightException("Execute failed.", e);
        } finally {
            if (playwright != null) {
                playwrightObjectPool.returnObject(playwright);
            }
        }
    }

}
