package com.frameworkium.tests.web;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.util.Arrays;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.pages.web.BasicAuthSuccessPage;
import com.frameworkium.pages.web.CheckboxesPage;
import com.frameworkium.pages.web.DragAndDropPage;
import com.frameworkium.pages.web.DropdownPage;
import com.frameworkium.pages.web.DynamicLoadingExamplePage;
import com.frameworkium.pages.web.FileDownloadPage;
import com.frameworkium.pages.web.FileUploadPage;
import com.frameworkium.pages.web.FileUploadSuccessPage;
import com.frameworkium.pages.web.FormAuthenticationPage;
import com.frameworkium.pages.web.FormAuthenticationSuccessPage;
import com.frameworkium.pages.web.HoversPage;
import com.frameworkium.pages.web.JQueryUIMenuPage;
import com.frameworkium.pages.web.JQueryUIPage;
import com.frameworkium.pages.web.JavaScriptAlertsPage;
import com.frameworkium.pages.web.KeyPressesPage;
import com.frameworkium.pages.web.SecureFileDownloadPage;
import com.frameworkium.pages.web.WelcomePage;
import com.frameworkium.tests.internal.BaseTest;

public class TheInternetHerokuWebTest extends BaseTest {

	@Issue("HEROKU-1")
	@Test(description = "Basic Auth")
	public final void basicAuth() {

        // Navigate to the basic auth page
		BasicAuthSuccessPage basicAuthSuccess = WelcomePage.open().then().clickBasicAuth("admin", "admin");

        // Assert that the returned page has the text present
		assertThat(basicAuthSuccess.getSource()).contains("Congratulations! You must have the proper credentials.");
	}

	@Issue("HEROKU-2")
	@Test(description = "Checkboxes")
	public final void checkBoxes() {

		// Navigate to the checkboxes page
		CheckboxesPage checkboxesPage = WelcomePage.open().then().clickCheckboxesLink();

		// Set both checkboxes to checked
		//checkboxesPage.checkAllCheckboxes();

		// Set all checkboxes to checked via alternative method
		checkboxesPage.checkAllCheckboxes2();

		// Assert that all checkboxes are checked
        assertThat(checkboxesPage.getAllCheckboxCheckedStatus())
                .named("check status of checkboxes")
                .doesNotContain(false);
	}

	@Issue("HEROKU-3")
	@Test(description = "Drag and Drop")
	public final void dragAndDrop() {

		// Navigate to the checkboxes page
		DragAndDropPage dragAndDropPage = WelcomePage.open().then().clickDragAndDropLink();

        // Drag A onto B
		dragAndDropPage.dragAontoB();

        // Assert on the order of the headings
        assertThat(dragAndDropPage.getListOfHeadings()).named("Order of headings")
                .containsSequence(Arrays.asList("B", "A"));
	}

	@Issue("HEROKU-4")
	@Test(description = "Dropdown")
	public final void dropdown() {

		// Navigate to the checkboxes page
		DropdownPage dropdownPage = WelcomePage.open().then().clickDropdownLink();

        // Drag A onto B
		dropdownPage.selectFromDropdown("Option 1");

        // Assert selected
		assertThat(dropdownPage.getSelectedOptionText()).named("selected option in dropdown").isEqualTo("Option 1");
	}

	@Issue("HEROKU-5")
	@Test(description = "Dynamic loading")
	public final void dynamicLoading() {

		// Navigate to the dynamic loading hidden element page
		DynamicLoadingExamplePage dynamicLoadingExamplePage = WelcomePage.open().then().clickDynamicLoading()
				.then().clickExample1();

		// Assert that the element is hidden
		assertThat(dynamicLoadingExamplePage.isElementDisplayed()).named("element visibility").isFalse();

		// Click start and wait for element to be displayed
		dynamicLoadingExamplePage.clickStart().then().waitForElementToBeDisplayed();

		// Assert that the element is indeed displayed
		assertThat(dynamicLoadingExamplePage.isElementDisplayed()).named("element visibility").isTrue();
	}

	@Issue("HEROKU-6")
	@Test(description = "File Download")
	public final void fileDownload() {

		// Navigate to the download page
		FileDownloadPage downloadPage = WelcomePage.open().then().clickFileDownloadLink();

		//If you have the file
		//File testFile = new File("/Users/robgates55/avatar.jpg");
		//FileInputStream f = new FileInputStream(testFile.getAbsolutePath());
		//int size = IOUtils.toByteArray(f).length;
		//IOUtils.closeQuietly(f);

        // Confirm that the avatar.jpg file is first in the list
		assertThat(downloadPage.getListOfDownloadableFiles().get(0)).isEqualTo("avatar.jpg");

        // If you know the size to expect
		int size = 7996;

		// Confirm size of the downloaded file is as expected
		assertThat(downloadPage.getSizeOfFirstFile()).isEqualTo(size);

	}

	@Issue("HEROKU-7")
	@Test(description = "File Upload")
	public final void fileUpload() {

		// Navigate to the upload page
		FileUploadPage fileUploadPage = WelcomePage.open().then().clickFileUploadLink();

        // Pick a local file we're going to upload
        File fileToUpload = new File("textfile.txt");

        // Upload the file and confirm we land on the success page
		FileUploadSuccessPage successPage = fileUploadPage.uploadFile(fileToUpload);

        // Confirm that the uploaded files list contains our filename
		assertThat(successPage.getUploadedFiles()).contains(fileToUpload.getName());
	}

	@Issue("HEROKU-8")
	@Test(description = "Form Authentication")
	public final void formAuthentication() {

		// Navigate to the form authentication page
		FormAuthenticationPage formAuthenticationPage = WelcomePage.open().then().clickFormAuthenticationLink();

        // Log in with the username password provided
		FormAuthenticationSuccessPage successPage = formAuthenticationPage.validLogin("tomsmith", "SuperSecretPassword!");

        // Confirm that we're on the success page
		assertThat(successPage.getSource()).contains("Welcome to the Secure Area");
	}

	@Issue("HEROKU-9")
	@Test(description = "Hovers")
	public final void hovers() {

		// Navigate to the hovers page
		HoversPage hoversPage = WelcomePage.open().then().clickHoversLink();

		// Confirm that the caption under the first figure contains expected text
		assertThat(hoversPage.getFirstFigureCaption()).contains("name: user1");
	}

	@Issue("HEROKU-10")
	@Test(description = "JQuery UI")
	public final void jqueryUI() {

        // Navigate to the jQuery UI page
		JQueryUIMenuPage jqueryUIMenuPage = WelcomePage.open().then().clickJQueryUILink();

        // Browse to the UI page
		JQueryUIPage UIPage = jqueryUIMenuPage.clickBackToUI();

        // Click the menu link to return to the menu page
		jqueryUIMenuPage = UIPage.clickMenuLink();

        // Check that the excel file link matches the string
		assertThat(jqueryUIMenuPage.getExcelFileURLAsString()).isEqualTo("http://the-internet.herokuapp.com/download/jqueryui/menu/menu.xls");
	}

	@Issue("HEROKU-11")
	@Test(description = "Javascript Alerts")
	public final void javascriptAlerts() {

		// Navigate to the javascript alerts page
		JavaScriptAlertsPage javascriptAlerts = WelcomePage.open().then().clickjavascriptAlertsLink();

		javascriptAlerts.clickAlertButtonAndAccept();
		assertThat(javascriptAlerts.getResultText()).isEqualTo("You successfuly clicked an alert");

		javascriptAlerts.clickAlertButtonAndDismiss();
		assertThat(javascriptAlerts.getResultText()).isEqualTo("You successfuly clicked an alert");

		javascriptAlerts.clickConfirmButtonAndAccept();
		assertThat(javascriptAlerts.getResultText()).isEqualTo("You clicked: Ok");

		javascriptAlerts.clickConfirmButtonAndDismiss();
		assertThat(javascriptAlerts.getResultText()).isEqualTo("You clicked: Cancel");

		javascriptAlerts.clickPromptButtonAndEnterPrompt("Blah blah blah");
		assertThat(javascriptAlerts.getResultText()).isEqualTo("You entered: Blah blah blah");
	}

	@Issue("HEROKU-12")
	@Test(description = "Key Presses")
	public final void keypresses() {

		// Navigate to the key presses page
		KeyPressesPage keyPressesPage = WelcomePage.open().then().clickKeyPressesLink();

		keyPressesPage.enterKeyPress(Keys.ENTER);

		assertThat(keyPressesPage.getResultText()).isEqualTo("You entered: " + Keys.ENTER.name());
	}

	@Issue("HEROKU-13")
	@Test(description = "Secure file Download")
	public final void secureFileDownload() {

		// Navigate to the secure file downloads page
		SecureFileDownloadPage secureFileDownloadPage = WelcomePage.open().then().clickSecureFileDownloadsLink("admin","admin");

        // Assert that the page contains the text
        assertThat(secureFileDownloadPage.getHeadingText()).isEqualTo("Secure File Downloader");
	}

}
