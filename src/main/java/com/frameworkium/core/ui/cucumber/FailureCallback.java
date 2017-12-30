package com.frameworkium.core.ui.cucumber;

import com.frameworkium.core.ui.tests.BaseTest;
import org.openqa.selenium.OutputType;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.cucumberjvm.callback.OnFailureCallback;

public class FailureCallback implements OnFailureCallback {

    @Override
    public Object call() {
        failureScreenshot();
        return null;
    }

    @Attachment(type = "image/png")
    public byte[] failureScreenshot() {
        return BaseTest.getDriver().getScreenshotAs(OutputType.BYTES);
    }
}
