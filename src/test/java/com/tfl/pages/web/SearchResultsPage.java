package com.tfl.pages.web;

import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;
import com.tfl.pages.web.components.NavBar;

public class SearchResultsPage extends BasePage<SearchResultsPage> {

    @Name("Nav Bar")
    @Visible
    private NavBar navBar;

}
