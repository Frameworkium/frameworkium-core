package com.frameworkium.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;
import com.jayway.restassured.RestAssured;

/**
 * Simple test of the HTML5 Drag and Drop functionality.
 *
 * Not currently natively supported by Selenium, see:
 * https://code.google.com/p/selenium/issues/detail?id=3604
 */
public class DragAndDropPage extends BasePage<DragAndDropPage> {

    private static final String JQUERY_JS_URI = "http://code.jquery.com/jquery-1.11.2.min.js";
    private static final String DRAG_DROP_HELPER_JS_URI = "https://gist.githubusercontent.com/"
            + "rcorreia/2362544/raw/3319e506e204af262d27f7ff9fca311e693dc342/"
            + "drag_and_drop_helper.js";

    @Visible
    @Name("Box A")
    @FindBy(id = "column-a")
    private WebElement boxA;

    @Visible
    @Name("Box B")
    @FindBy(id = "column-b")
    private WebElement boxB;

    @Visible
    @Name("List of headers")
    @FindBy(css = "header")
    private List<WebElement> boxes;

    // Acts as a cache to prevent multiple fetches of the same libraries from the Internet
    private String jQueryJS = "";
    private String dragDropHelperJS = "";

    /**
     * Fetches Javascript from the Internet used to be able to simulate Drag and Drop.
     *
     * @return a String containing the Javascript for JQuery (if not already present on the page)
     *         and code for simulating drag and drop.
     */
    private String javascriptToSumiulateDragDrop() {
        if (dragDropHelperJS.isEmpty()) {
            dragDropHelperJS = RestAssured.get(DRAG_DROP_HELPER_JS_URI).asString();
        }
        Boolean isJQueryAvailable = (Boolean) executeJS("return typeof $ !== 'undefined';");
        if (!isJQueryAvailable && jQueryJS.isEmpty()) {
            jQueryJS = RestAssured.get(JQUERY_JS_URI).asString();
        }

        return jQueryJS + dragDropHelperJS;
    }

    /**
     * @param from the JQuery selector for the element to initially click and then drag
     * @param to the JQuery selector for the target element where the from element will be dropped
     */
    private void simulateDragAndDrop(String from, String to) {
        executeJS(javascriptToSumiulateDragDrop());
        executeJS("$('" + from + "').simulateDragDrop({ dropTarget: '" + to + "'});");
    }

    @Step("Drag A onto B")
    public DragAndDropPage dragAontoB() {
        simulateDragAndDrop("#column-a", "#column-b");
        return this;
    }

    @Step("Drag B onto A")
    public DragAndDropPage dragBontoA() {
        simulateDragAndDrop("#column-b", "#column-a");
        return this;
    }

    @Step("Get order of headers")
    public List<String> getListOfHeadings() {
        List<String> headings = new ArrayList<String>();
        for (WebElement box : boxes) {
            headings.add(box.getText());
        }
        return headings;
    }

}
