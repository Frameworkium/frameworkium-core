package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.restassured.RestAssured;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple test of the HTML5 Drag and Drop functionality.
 * <p>
 * Not currently natively supported by Selenium, see:
 * https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/3604
 */
public class DragAndDropPage extends BasePage<DragAndDropPage> {

    private static final String JQUERY_JS_URI = "https://code.jquery.com/jquery-3.2.1.min.js";

    @Visible
    @Name("List of headers")
    @FindBy(css = "header")
    private List<WebElement> boxes;

    // Acts as a cache to prevent multiple fetches of the same libraries from the Internet
    private static String jQueryJS = "";
    // https://gist.githubusercontent.com/rcorreia/2362544/raw/3319e506e204af262d27f7ff9fca311e693dc342/drag_and_drop_helper.js
    private static String dragDropHelperJS = "!function(t){t.fn.simulateDragDrop=function(a)" +
            "{return this.each(function(){new t.simulateDragDrop(this,a)})}," +
            "t.simulateDragDrop=function(t,a){this.options=a,this.simulateEvent(t,a)}," +
            "t.extend(t.simulateDragDrop.prototype,{simulateEvent:function(a,e){var " +
            "n=\"dragstart\",r=this.createEvent(n);this.dispatchEvent(a,n,r)," +
            "n=\"drop\";var i=this.createEvent(n,{});i.dataTransfer=r.dataTransfer," +
            "this.dispatchEvent(t(e.dropTarget)[0],n,i),n=\"dragend\";" +
            "var s=this.createEvent(n,{});s.dataTransfer=r.dataTransfer,this." +
            "dispatchEvent(a,n,s)},createEvent:function(t){var a=document." +
            "createEvent(\"CustomEvent\");return a.initCustomEvent(t,!0,!0,null)," +
            "a.dataTransfer={data:{},setData:function(t,a){this.data[t]=a}," +
            "getData:function(t){return this.data[t]}},a},dispatchEvent:" +
            "function(t,a,e){t.dispatchEvent?t.dispatchEvent(e):t.fireEvent&&t.fireEvent(\"on\"+a,e)}})}(jQuery);";

    /**
     * Fetches Javascript from the Internet used to be able to simulate Drag and Drop.
     *
     * @return a String containing the Javascript for JQuery (if not already present on the page)
     *         and code for simulating drag and drop.
     */
    private String scriptToSimulateDragDrop() {
        if (jQueryJS.isEmpty()) {
            Boolean isJQueryAvailable = (Boolean) executeJS("return !!window.jQuery;");
            if (!isJQueryAvailable) {
                jQueryJS = RestAssured.get(JQUERY_JS_URI).asString();
            }
        }

        return jQueryJS + dragDropHelperJS;
    }

    /**
     * @param from the jQuery selector for the element to initially click and then drag
     * @param to   the jQuery selector for the target element where the from element will be dropped
     */
    private void simulateDragAndDrop(String from, String to) {
        executeJS(scriptToSimulateDragDrop());
        executeJS("$('" + from + "').simulateDragDrop({ dropTarget: '" + to + "'});");
    }

    public DragAndDropPage dragAontoB() {
        simulateDragAndDrop("#column-a", "#column-b");
        return this;
    }

    public List<String> getListOfHeadings() {
        return boxes.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

}
