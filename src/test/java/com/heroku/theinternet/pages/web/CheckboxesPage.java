package com.heroku.theinternet.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.CheckBox;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Name("All checkboxes")
    @FindBy(css = "form input[type='checkbox']")
    private List<CheckBox> allCheckboxes;

    //This is required as we can't check
    //visibility of a List<CheckBox> yet
    @Visible
    @Name("Unchecked checkbox")
    @FindBy(css = "form input[type='checkbox']")
    private CheckBox uncheckedCheckbox;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {

        for (CheckBox checkbox : allCheckboxes) {
            checkbox.set(true);
        }

        return this;
    }

    @Step("Return the checked status of all the checkboxes")
    public List<Boolean> getAllCheckboxCheckedStatus() {

        List<Boolean> checkedness = new ArrayList<Boolean>();

        for (CheckBox checkbox : allCheckboxes) {
            checkedness.add(checkbox.isSelected());
        }

        return checkedness;
    }

}
