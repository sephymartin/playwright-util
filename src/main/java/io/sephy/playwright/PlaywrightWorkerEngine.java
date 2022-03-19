package io.sephy.playwright;

import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.Proxy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sephy
 * @date 2022-02-11 22:06
 */
@Slf4j
public class PlaywrightWorkerEngine implements BeanFactoryAware {

    private BeanFactory beanFactory;

    /**
     * 初始化脚本
     */
    private List<String> defaultInitScripts;

    private GenericObjectPool<Playwright> playwrightObjectPool;

    public PlaywrightWorkerEngine(List<String> defaultInitScripts, GenericObjectPool<Playwright> playwrightObjectPool) {
        this.defaultInitScripts = defaultInitScripts;
        this.playwrightObjectPool = playwrightObjectPool;
    }

    public <E> E doWithPlaywright(PlaywrightWorker<E> worker) {
        Playwright playwright = null;
        try {
            playwright = playwrightObjectPool.borrowObject();
            return worker.doWithPlaywright(playwright);
        } catch (Exception e) {
            throw new PlaywrightException("Execute failed.", e);
        } finally {
            if (playwright != null) {
                playwrightObjectPool.returnObject(playwright);
            }
        }
    }

    public <E> E doWithDefaultPlaywrightOptions(PlaywrightPageWorker<E> pageWorker) {
        return doWithDefaultPlaywrightOptions(pageWorker, null);
    }

    public <E> E doWithDefaultPlaywrightOptions(PlaywrightPageWorker<E> pageWorker, Proxy proxy) {
        BrowserType.LaunchOptions launchOptions = null;
        Browser.NewContextOptions contextOptions = null;
        if (beanFactory != null) {
            launchOptions = beanFactory.getBeanProvider(BrowserType.LaunchOptions.class).getIfAvailable();
            contextOptions = beanFactory.getBeanProvider(Browser.NewContextOptions.class).getIfAvailable();
            if (proxy != null) {
                contextOptions.setProxy(proxy);
            }
        }
        CustomOptionsPlaywrightWorker<E> playwrightWorker =
            new CustomOptionsPlaywrightWorker(launchOptions, contextOptions, defaultInitScripts, pageWorker);
        return doWithPlaywright(playwrightWorker);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
