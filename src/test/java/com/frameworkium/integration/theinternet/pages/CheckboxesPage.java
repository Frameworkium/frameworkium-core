package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.htmlelements.element.CheckBox;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.support.FindBy;


import java.util.List;
import java.util.stream.Stream;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Visible(checkAtMost = 1)
    @FindBy(css = "form input[type='checkbox']")
    private List<CheckBox> allCheckboxes;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {
        allCheckboxes.forEach(CheckBox::select);
        return this;
    }

    @Step("Return the checked status of all the checkboxes")
    public Stream<Boolean> getAllCheckboxCheckedStatus() {
        return allCheckboxes.stream()
                .map(CheckBox::isSelected);
    }

}
