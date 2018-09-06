package com.frameworkium.integration.seleniumhq.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.*;
import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.seleniumhq.components.HeaderComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.yandex.qatools.htmlelements.element.Link;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class SeleniumDownloadPage extends BasePage<SeleniumDownloadPage> {

    @Visible
    private HeaderComponent header;

    @Visible
    @FindBy(css = "#mainContent")
    private WebElement mainContentDiv;

    @FindBy(css = "#mainContent > p:nth-child(5) > a")
    private Link latestDownloadLink;

    public static SeleniumDownloadPage open() {
        return PageFactory.newInstance(
                SeleniumDownloadPage.class,
                "https://www.seleniumhq.org/download/");
    }

    public String getLatestVersion() {
        return latestDownloadLink.getText();
    }

    public SeleniumDownloadPage hideContent() {
        BaseUITest.getDriver().executeScript(
                "arguments[0].style.visibility='hidden';", mainContentDiv);
        wait.until(ExpectedConditions.not(visibilityOf(mainContentDiv)));
        return this;
    }

    public SeleniumDownloadPage forceVisibleContent() {
        // ensure force visible works
        new Visibility().forceVisible(mainContentDiv);
        return this;
    }

    public void waitForContent() {
        wait.until(visibilityOf(mainContentDiv));
    }

    public void log() {
        logger.trace("Showing example use of the logger in BasePage");
        logger.info("Showing example use of the logger in BasePage");
    }
}
