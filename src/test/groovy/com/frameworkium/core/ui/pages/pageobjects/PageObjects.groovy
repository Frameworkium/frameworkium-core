package com.frameworkium.core.ui.pages.pageobjects

import com.frameworkium.core.ui.annotations.ForceVisible
import com.frameworkium.core.ui.annotations.Invisible
import com.frameworkium.core.ui.annotations.Visible
import com.frameworkium.core.ui.pages.BasePage
import groovy.transform.InheritConstructors
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.CacheLookup
import org.openqa.selenium.support.FindBy
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.annotations.Timeout
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.HtmlElement
import ru.yandex.qatools.htmlelements.element.TextInput

class PageObjects {

    @InheritConstructors
    static class SingleVisibleElement extends BasePage<SingleVisibleElement> {

        @Visible
        WebElement visibleElement
    }

    @InheritConstructors
    static class SubClassedPage extends SingleVisibleElement {

        @Visible
        WebElement subClassedVisibleWebElement
    }

    @InheritConstructors
    static class SingleInvisibleElement extends BasePage<SingleInvisibleElement> {

        @Invisible
        TextInput invisibleTextInput
    }

    @InheritConstructors
    static class SingleForceVisibleElement extends BasePage<SingleForceVisibleElement> {

        @ForceVisible
        WebElement forceVisibleElement
    }

    @InheritConstructors
    static class ListOfElements extends BasePage<ListOfElements> {

        @Visible
        List<WebElement> visibles

        @Invisible
        List<WebElement> invisibles

        @Invisible
        List<WebElement> emptyInvisible

        @ForceVisible
        List<WebElement> forceVisibles
    }

    @InheritConstructors
    static class ListOfElementsCheckAtMost extends BasePage<ListOfElements> {

        @Visible(checkAtMost = 2)
        List<WebElement> visibles

        @Invisible(checkAtMost = 2)
        List<WebElement> invisibles

        @ForceVisible(checkAtMost = 2)
        List<WebElement> forceVisibles
    }

    @InheritConstructors
    static class VisibleComponent extends BasePage<VisibleComponent> {

        @Visible
        Component visibleComponent
    }

    @InheritConstructors
    static class VisibleComponents extends BasePage<VisibleComponents> {

        @Visible
        List<Component> visibleComponents
    }

    @InheritConstructors
    static class Component extends HtmlElement {

        @Visible
        WebElement myVisibleWebElement
    }



    // Invalid Page Objects:

    @InheritConstructors
    static class MultiVisibilityTypifiedElement extends BasePage<MultiVisibilityTypifiedElement> {

        @Visible
        @ForceVisible
        @Invisible
        @Timeout(42)
        @Name("named element")
        @FindBy(css = "html")
        @CacheLookup
        CheckBox myTypifiedElement
    }

    @InheritConstructors
    static class MultiVisibilityWebElement extends BasePage<MultiVisibilityWebElement> {

        @Visible
        @Invisible
        @FindBy(css = "body")
        WebElement myWebElement
    }

    @InheritConstructors
    static class UnsupportedFieldType extends BasePage<UnsupportedFieldType> {

        @Visible
        String aStringIsNotAWebElement
    }
}
