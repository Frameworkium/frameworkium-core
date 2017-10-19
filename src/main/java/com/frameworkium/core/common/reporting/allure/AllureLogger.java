package com.frameworkium.core.common.reporting.allure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;

public class AllureLogger {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Uses the @Step annotation to log the given log message to Allure.
     *
     * @param message the message to log to the allure report
     */
    @Step("{0}")
    public static void logToAllure(String message) {
        logger.debug("Logged to allure: " + message);
    }

    /**
     * Logs the start of a step to your allure report.
     * Other steps will be sub-steps until you call stepFinish.
     *
     * @param stepName the name of the step
     */
    public static void stepStart(String stepName) {
        Allure.LIFECYCLE.fire(new StepStartedEvent(stepName));
    }

    /**
     * Logs the end of a step. Ensure it matches a {@link #stepStart(String)}.
     **/
    public static void stepFinish() {
        Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }

}
