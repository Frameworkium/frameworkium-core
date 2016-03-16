package com.frameworkium.core.api.tests;

import com.frameworkium.core.common.listeners.MethodInterceptor;
import com.frameworkium.core.common.listeners.ResultLoggerListener;
import com.frameworkium.core.common.listeners.TestListener;
import com.frameworkium.core.common.reporting.allure.AllureProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;

@Listeners({MethodInterceptor.class,
        TestListener.class, ResultLoggerListener.class})

public abstract class BaseTest {

    private static Logger logger = LogManager.getLogger(BaseTest.class);


    /**
     * Creates the allure properties for the report, after the test run
     */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

//    public void __stepStart(String stepName){
//        Allure.LIFECYCLE.fire(new StepStartedEvent(stepName));
//}
//
//    public void __stepFinish(){
//        Allure.LIFECYCLE.fire(new StepFinishedEvent());
//    }
}
