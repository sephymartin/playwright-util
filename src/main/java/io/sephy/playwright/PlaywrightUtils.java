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

    @Deprecated
    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes) {
        return querySelector(page, selector, tryTimes, false);
    }

    @Deprecated
    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes,
        boolean forceVisible) {
        return querySelector(page, selector, tryTimes, forceVisible ? VISIBLE_CONDITION : NOT_NULL_CONDITION);
    }

    @Deprecated
    public static ElementHandle querySelector(@NonNull Page page, @NonNull String selector, int tryTimes,
        ElementHandleCheckCondition checkCondition) {
        if (checkCondition == null) {
            checkCondition = NOT_NULL_CONDITION;
        }
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素：{}", i + 1, tryTimes, selector);
            ElementHandle elementHandle = page.querySelector(selector);
            if (checkCondition.check(elementHandle)) {
                return elementHandle;
            }
            sleepIgnoreException(TimeUnit.SECONDS, 1L);
        }
        return null;
    }

    @Deprecated
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

    public static Locator locator(@NonNull Page page, @NonNull String selector, int tryTimes) {
        return locator(page, selector, tryTimes, false);
    }

    public static Locator locator(@NonNull Page page, @NonNull String selector, int tryTimes, boolean forceVisible) {
        for (int i = 0; i < tryTimes; i++) {
            log.info("进行第 {}/{} 次尝试，查找元素：{}", i + 1, tryTimes, selector);
            Locator locator = page.locator(selector);
            if (locator != null) {
                if (forceVisible && locator.isVisible()) {
                    return locator;
                } else {
                    return locator;
                }
            }
            sleepIgnoreException(TimeUnit.SECONDS, 1L);
        }
        return null;
    }

    public static void randomClick(@NonNull Page page, @NonNull ElementHandle elementHandle) {
        BoundingBox boundingBox = elementHandle.boundingBox();
        Random random = new Random();
        double deltaX = (boundingBox.width) * (3 + random.nextInt(97)) / 100;
        double deltaY = (boundingBox.height) * (3 + random.nextInt(97)) / 100;
        double targetX = boundingBox.x + deltaX;
        double targetY = boundingBox.y + deltaY;
        log.info("模拟鼠标移动，坐标：{}, {}", targetX, targetY);
        page.mouse().move(targetX, targetY, new Mouse.MoveOptions().setSteps(5));
        log.info("模拟鼠标点击，坐标：{}, {}", targetX, targetY);
        page.mouse().click(targetX, targetY);
    }

    public static void randomClick(@NonNull Page page, @NonNull Locator locator) {
        BoundingBox boundingBox = locator.boundingBox();
        Random random = new Random();
        double deltaX = (boundingBox.width) * (3 + random.nextInt(97)) / 100;
        double deltaY = (boundingBox.height) * (3 + random.nextInt(97)) / 100;
        double targetX = boundingBox.x + deltaX;
        double targetY = boundingBox.y + deltaY;
        log.info("模拟鼠标移动，坐标：{}, {}", targetX, targetY);
        page.mouse().move(targetX, targetY, new Mouse.MoveOptions().setSteps(5));
        log.info("模拟鼠标点击，坐标：{}, {}", targetX, targetY);
        page.mouse().click(targetX, targetY);
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
