package com.frameworkium.core.common.reporting.allure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;

public class AllureLogger {

    private static final Logger logger = LogManager.getLogger();

    @Step("{0}")
    public static void logToAllure(String log) {
        logger.debug("Logged to allure: " + log);
    }

    public static void __stepStart(String stepName) {
        Allure.LIFECYCLE.fire(new StepStartedEvent(stepName));
    }

    public static void __stepFinish() {
        Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }

}
