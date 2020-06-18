package com.frameworkium.integration.seleniumhq.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.components.HeaderComponent;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.yandex.qatools.htmlelements.element.Link;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class SeleniumDownloadPage extends BasePage<SeleniumDownloadPage> {

    @Visible
    private HeaderComponent header;

    @Visible
    @FindBy(css = "body > div.split-section.container.dark-background > div.right > p:nth-child(1) > a")
    private Link latestDownloadLink;

    public static SeleniumDownloadPage open() {
        return PageFactory.newInstance(
                SeleniumDownloadPage.class,
                "https://selenium.dev/downloads/");
    }

    public String getLatestVersion() {
        return latestDownloadLink.getText();
    }

    public SeleniumDownloadPage hideContent() {
        executeJS("arguments[0].style.visibility='hidden';", latestDownloadLink);
        wait.until(ExpectedConditions.not(visibilityOf(latestDownloadLink)));
        return this;
    }

    public SeleniumDownloadPage forceVisibleContent() {
        // ensure force visible works
        forceVisible(latestDownloadLink);
        return this;
    }

    public void waitForContent() {
        wait.until(visibilityOf(latestDownloadLink));
    }

    public void log() {
        logger.trace("Using the BasePage logger");
        logger.info("Using the BasePage logger");
    }
}
