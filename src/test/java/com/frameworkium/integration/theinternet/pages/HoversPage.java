package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.ui.annotations.Invisible;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

public class HoversPage extends BasePage<HoversPage> {

    @Visible
    @Name("First Figure")
    @FindBy(css = "div.figure:nth-of-type(1)")
    private WebElement firstFigure;

    @Invisible
    @Name("First Figure Caption")
    @FindBy(css = "div.figure:nth-of-type(1) div.figcaption")
    private WebElement firstFigureCaption;

    public String getFirstFigureCaption() {

        // Move mouse over the first figure to make caption visible
        (new Actions(driver)).moveToElement(firstFigure).perform();

        // Return text from the now-visible caption
        return firstFigureCaption.getText();
    }

}
