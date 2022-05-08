package io.sephy.playwright;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sephy
 * @date 2022-02-26 15:11
 */
@Slf4j
public abstract class PlaywrightUtils {

    public static final ElementHandleCheckCondition NOT_NULL_CONDITION = elementHandle -> elementHandle != null;

    public static final ElementHandleCheckCondition VISIBLE_CONDITION =
        elementHandle -> elementHandle != null && elementHandle.isVisible();

    public static ElementHandle waitForSelector(@NonNull Page page, @NonNull String selector, Double timeout) {
        return waitForSelector(page, selector, timeout, WaitForSelectorState.VISIBLE, Boolean.TRUE);
    }

    public static ElementHandle waitForSelector(@NonNull Page page, @NonNull String selector, Double timeout,
        WaitForSelectorState state) {
        return waitForSelector(page, selector, timeout, state, Boolean.TRUE);
    }

    public static ElementHandle waitForSelector(@NonNull Page page, @NonNull String selector, Double timeout,
        WaitForSelectorState state, Boolean strict) {
        return waitForSelector(page, selector,
            new Page.WaitForSelectorOptions().setTimeout(timeout).setState(state).setStrict(strict));
    }

    public static ElementHandle waitForSelector(@NonNull Page page, @NonNull String selector,
        Page.WaitForSelectorOptions options) {
        log.info("等待元素：{}, 超时时间: {} ms, ", selector, options.timeout);
        return page.waitForSelector(selector, options);
    }

    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes) {
        return querySelector(page, selector, tryTimes, false);
    }

    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes,
        boolean forceVisible) {
        return querySelector(page, selector, tryTimes, forceVisible ? VISIBLE_CONDITION : NOT_NULL_CONDITION);
    }

    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes,
        ElementHandleCheckCondition checkCondition) {
        if (checkCondition == null) {
            checkCondition = NOT_NULL_CONDITION;
        }
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素：{}", i + 1, tryTimes, selector);
            try {
                ElementHandle elementHandle = page.querySelector(selector);
                if (checkCondition.check(elementHandle)) {
                    return elementHandle;
                }
            } catch (Exception e) {
                // ignore
            }
            sleepIgnoreException(TimeUnit.SECONDS, 1L);
        }
        return null;
    }

    public static ElementHandle querySelector(@NonNull ElementHandle parent, @NonNull String selector, int tryTimes,
        ElementHandleCheckCondition checkCondition) {
        if (checkCondition == null) {
            checkCondition = NOT_NULL_CONDITION;
        }
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素：{}", i + 1, tryTimes, selector);
            ElementHandle elementHandle = parent.querySelector(selector);
            if (checkCondition.check(elementHandle)) {
                return elementHandle;
            }
            sleepIgnoreException(TimeUnit.SECONDS, 1L);
        }
        return null;
    }

    public static Locator locator(Page page, String selector, int tryTimes) {
        return locator(page, selector, null, null, tryTimes);
    }

    public static Locator locator(Page page, String selector, Page.LocatorOptions locatorOptions, int tryTimes) {
        return locator(page, selector, locatorOptions, null, 1);
    }

    public static Locator locator(Page page, String selector, WaitForSelectorState state, int tryTimes) {
        return locator(page, selector, null, state, 1);
    }

    public static Locator locator(Page page, String selector, Page.LocatorOptions locatorOptions,
        WaitForSelectorState state, int tryTimes) {
        return locator(page, selector, locatorOptions, state, tryTimes, 1000D);
    }

    public static Locator locator(@NonNull Page page, @NonNull String selector, Page.LocatorOptions locatorOptions,
        WaitForSelectorState state, int tryTimes, double timeout) {
        if (state == null) {
            state = WaitForSelectorState.VISIBLE;
        }
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素: selector: {}, state: {}, timeout: {}", i + 1, tryTimes, selector, state,
                timeout);
            try {
                Locator locator = page.locator(selector, locatorOptions);
                if (locator != null && locator.count() > 0) {
                    locator.waitFor(new Locator.WaitForOptions().setState(state).setTimeout(timeout));
                    return locator;
                } else {
                    page.waitForTimeout(timeout);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        log.warn("查找元素失败：{}", selector);
        return null;
    }

    public static Locator locatorChildren(@NonNull Page page, @NonNull Locator parent, @NonNull String selector,
        int tryTimes) {
        return locatorChildren(page, parent, selector, null, null, tryTimes, 1000D);
    }

    public static Locator locatorChildren(@NonNull Page page, @NonNull Locator parent, @NonNull String selector,
        Locator.LocatorOptions locatorOptions, int tryTimes) {
        return locatorChildren(page, parent, selector, locatorOptions, null, tryTimes, 1000D);
    }

    public static Locator locatorChildren(@NonNull Page page, @NonNull Locator parent, @NonNull String selector,
        WaitForSelectorState state, int tryTimes) {
        return locatorChildren(page, parent, selector, null, state, tryTimes, 1000D);
    }

    public static Locator locatorChildren(@NonNull Page page, @NonNull Locator parent, @NonNull String selector,
        Locator.LocatorOptions locatorOptions, WaitForSelectorState state, int tryTimes) {
        return locatorChildren(page, parent, selector, locatorOptions, state, tryTimes, 1000D);
    }

    public static Locator locatorChildren(@NonNull Page page, @NonNull Locator parent, @NonNull String selector,
        Locator.LocatorOptions locatorOptions, WaitForSelectorState state, int tryTimes, double timeout) {
        if (state == null) {
            state = WaitForSelectorState.VISIBLE;
        }
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素: selector: {}, state: {}, timeout: {}", i + 1, tryTimes, selector, state,
                timeout);
            try {
                Locator locator = parent.locator(selector, locatorOptions);
                if (locator != null && locator.count() > 0) {
                    locator.waitFor(new Locator.WaitForOptions().setState(state).setTimeout(timeout));
                    return locator;
                } else {
                    page.waitForTimeout(timeout);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        log.warn("查找元素失败：{}", selector);
        return null;
    }

    public static void typeString(@NonNull Locator locator, String input) {
        locator.fill("");
        log.info("模拟键盘输入: {}", input);
        locator.fill(input);
    }

    public static void randomClick(@NonNull Page page, @NonNull ElementHandle elementHandle) {
        BoundingBox boundingBox = elementHandle.boundingBox();
        Random random = new Random();
        double deltaX = (boundingBox.width) * (random.nextDouble() * 4 + 2);
        double deltaY = (boundingBox.height) * (random.nextDouble() * 4 + 2);
        double targetX = boundingBox.x + deltaX;
        double targetY = boundingBox.y + deltaY;
        log.info("模拟鼠标移动，坐标：{}, {}", targetX, targetY);
        page.mouse().move(targetX, targetY, new Mouse.MoveOptions().setSteps(5));
        // log.info("模拟鼠标点击，坐标：{}, {}", targetX, targetY);
        elementHandle.click();
        // page.mouse().click(targetX, targetY);
    }

    public static void randomClick(@NonNull Page page, @NonNull Locator locator) {
        BoundingBox boundingBox = locator.boundingBox();
        Random random = new Random();
        double deltaX = (boundingBox.width) * (random.nextDouble() * 4 + 2);
        double deltaY = (boundingBox.height) * (random.nextDouble() * 4 + 2);
        double targetX = boundingBox.x + deltaX;
        double targetY = boundingBox.y + deltaY;
        log.info("模拟鼠标移动，坐标：{}, {}", targetX, targetY);
        page.mouse().move(targetX, targetY, new Mouse.MoveOptions().setSteps(5));
        // log.info("模拟鼠标点击，坐标：{}, {}", targetX, targetY);
        locator.click();
        // page.mouse().click(targetX, targetY);
    }

    public static void randomClickAndType(@NonNull Page page, @NonNull Locator locator, String textToType) {
        randomClick(page, locator);
        locator.fill("");
        log.info("模拟键盘输入：{}", textToType);
        locator.type(textToType);
    }

    public static void sleepIgnoreException(TimeUnit unit, long duration) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            log.error("sleepIgnoreException", e);
        }
    }

    @FunctionalInterface
    public interface ElementHandleCheckCondition {

        boolean check(ElementHandle elementHandle);
    }
}
