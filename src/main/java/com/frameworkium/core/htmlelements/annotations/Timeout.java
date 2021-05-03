package com.frameworkium.core.htmlelements.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is used for setting waiting timeout value,
 * which will be used for waiting an element to appear.
 * <p>
 * For example:
 * <p/>
 * <pre class="code">
 * &#64;FindBy(css = "my_form_css")
 * &#64;Timeout(3)
 * public class MyForm extends HtmlElement {
 * &#64;FindBy(css = "text_input_css")
 * &#64;Timeout(3)
 * private TextInput textInput;
 * <p/>
 * // Other elements and methods here
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Timeout {
  int value();
}
