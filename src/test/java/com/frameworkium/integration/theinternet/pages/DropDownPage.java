package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.htmlelements.element.Select;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class DropDownPage extends BasePage<DropDownPage> {

    @Visible
    @FindBy(css = "select#dropdown")
    private Select select;

    public List<String> getAllOptions() {
        return select
                .getOptions().stream()
                .map(WebElement::getText)
                .collect(toList());
    }

    public void select(String option) {
        select.selectByVisibleText(option);
    }

    public String getCurrentSelection() {
        return select.getFirstSelectedOption().getText();
    }
}
