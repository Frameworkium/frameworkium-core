package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.CheckBox;

import java.util.List;
import java.util.stream.Collectors;

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

        allCheckboxes.stream().forEach(CheckBox::select);

        return this;
    }

    @Step("Return the checked status of all the checkboxes")
    public List<Boolean> getAllCheckboxCheckedStatus() {

        return allCheckboxes.stream()
                .map(CheckBox::isSelected)
                .collect(Collectors.toList());
    }

}
