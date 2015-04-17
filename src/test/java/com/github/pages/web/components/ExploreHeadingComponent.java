package com.github.pages.web.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.github.pages.web.ExplorePage;

@Name("Explore Page/'Tab' Header")
@FindBy(css = "div.pagehead.explore-head")
public class ExploreHeadingComponent extends HtmlElement {

    @Name("All Link")
    @Visible
    @FindBy(css = "nav.pagehead-nav > a:first-child")
    private Link allLink;

    @Name("Showcases Link")
    @Visible
    @FindBy(css = "nav.pagehead-nav > a:nth-child(2)")
    private Link showcasesLink;

    @Name("Trending Link")
    @Visible
    @FindBy(css = "nav.pagehead-nav > a:nth-child(3)")
    private Link trendingLink;

    @Name("Title text")
    @Visible
    @FindBy(css = "div.pagehead.explore-head h1")
    private WebElement header;

    @Step("Go to the explore 'All' tab")
    public ExplorePage clickExplore() {
        allLink.click();
        return PageFactory.newInstance(ExplorePage.class);
    }

}
