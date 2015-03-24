package com.heroku.theinternet.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.CacheLookup;
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

    @Visible
    @Name("Unchecked checkbox")
    @CacheLookup
    @FindBy(css = "form input[type='checkbox']")
    private CheckBox uncheckedCheckbox;

    @Visible
    @Name("Checked checkbox")
    @CacheLookup
    @FindBy(css = "form input[type='checkbox']:checked")
    private CheckBox checkedCheckbox;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {

        for (CheckBox checkbox : allCheckboxes) {
            checkbox.set(true);
        }

        // Question - what happens if you don't use the CacheLookup above?
        uncheckedCheckbox.set(true);
        uncheckedCheckbox.set(false);
        uncheckedCheckbox.set(true);

        checkedCheckbox.set(true);
        checkedCheckbox.set(false);
        checkedCheckbox.set(true);

        return this;
    }

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes2() {

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
