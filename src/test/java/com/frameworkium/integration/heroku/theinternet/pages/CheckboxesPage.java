package com.frameworkium.integration.heroku.theinternet.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.CheckBox;

import java.util.List;
import java.util.stream.Collectors;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Visible(checkAtMost = 2)
    @Name("All checkboxes")
    @FindBy(css = "form input[type='checkbox']")
    private List<CheckBox> allCheckboxes;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {
        allCheckboxes.forEach(CheckBox::select);
        return this;
    }

    @Step("Return the checked status of all the checkboxes")
    public List<Boolean> getAllCheckboxCheckedStatus() {

        return allCheckboxes.stream()
                .map(CheckBox::isSelected)
                .collect(Collectors.toList());
    }

}
