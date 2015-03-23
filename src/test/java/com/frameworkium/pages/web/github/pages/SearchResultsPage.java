package com.frameworkium.pages.web.github.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.frameworkium.pages.web.github.components.HeaderComponent;

public class SearchResultsPage extends BasePage<SearchResultsPage> {

    @Name("Header")
    @Visible
    private HeaderComponent header;

    @Name("Repository Links")
    @Visible
    @FindBy(css = "h3 > a")
    private List<Link> repoLinks;

    public HeaderComponent theHeader() {
        return header;
    }

    @Step("Navigate to the Github homepage")
    public static SearchResultsPage open() {
        return PageFactory.newInstance(SearchResultsPage.class, "http://github.com");
    }

    @Step("Get the list of code repository names")
    public List<String> getRepoNames() {

        List<String> names = new ArrayList<String>();
        for (Link link : repoLinks) {
            names.add(link.getText());
        }
        return names;
    }
}
