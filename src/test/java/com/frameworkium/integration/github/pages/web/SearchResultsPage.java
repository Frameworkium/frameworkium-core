package com.frameworkium.integration.github.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsPage extends BasePage<SearchResultsPage> {

    @Visible
    @Name("Repository Links")
    @FindBy(css = "ul.repo-list h3 > a")
    private List<Link> repoLinks;

    @Step("Get the list of code repository names")
    public List<String> getRepoNames() {

        return repoLinks.stream()
                .map(Link::getText)
                .collect(Collectors.toList());
    }
}
