package capture;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;

//import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import static com.jayway.restassured.RestAssured.given;

import capture.Command;
import selenium.SessionStorage;

public class Capture {

	private final WebDriver driver;
	private JSONObject command;
	private String[] tags;
	private String stacktrace;
	
	public Capture(final WebDriver driver, final Command command, final String[] tags)
	{
		this.driver = driver;
		this.tags = tags;
		
		try
		{
			this.command = getCommand(command);
			send(constructObject());
		}
		catch(final JSONException e)
		{
			e.printStackTrace();
		}
		catch(final ConnectException e)
		{
			System.err.println("Capture connection exception");
		}
	}
	
	public Capture(final WebDriver driver, final String action, final String[] tags)
	{
		this.driver = driver;
		this.tags = tags;
		
		try
		{
			this.command = getCommand(new Command(action, null, null));
			send(constructObject());
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
		catch (final ConnectException e)
		{
			System.err.println("Capture connection exception");
		}
	}
	
	public Capture(final WebDriver driver, final ITestResult iTestResult)
	{
		this.driver = driver;
		try
		{
			String status=null;
			
			switch(iTestResult.getStatus())
			{
			case ITestResult.FAILURE:
			{
				status = "fail";
				
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				iTestResult.getThrowable().printStackTrace(pw);
				this.stacktrace = sw.toString();
			}
				break;
			case ITestResult.SKIP:
			{
				status = "skip";
				
				final ITestNGMethod method = iTestResult.getMethod();
				this.stacktrace = (String) iTestResult.getTestContext().getAttribute("SKIP" + method.getId());
			}
				break;
			case ITestResult.SUCCESS:
			{
				status = "pass";
			}
				break;
			default:
				break;
			}
			
			this.command = getCommand(new Command(status, null, null));
			send(constructObject());
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
		catch (final ConnectException e)
		{
			System.err.println("Capture connection exception");
		}
	}
	
	private JSONObject constructObject() throws JSONException
	{
		final JSONObject obj = new JSONObject();
		
		final String testID = getTestID();
		if  (null != testID)
		{
			obj.put("testID",  testID);
		}
		
		final String executionID = getExecutionID();
		if  (null != executionID)
		{
			obj.put("executionID",  executionID);
		}
		
		final String user = getUser();
		if  (null != user)
		{
			obj.put("user",  user);
		}
		
		if  (null != command)
		{
			obj.put("cmd",  command);
		}
		
		obj.put("sut", getSUTDetails());
		
		obj.put("browser", getBrowserDetails());
		
		obj.put("url", getURL());
		
		final String node = getNode();
		if(null != node)
		{
			obj.put("node",  node);
		}
		
		if (null != tags)
		{
			obj.put("tags", tags);
		}
		
		if (null != stacktrace)
		{
			obj.put("stacktrace", stacktrace);
		}
		
		obj.put("ss", getScreenshot());
		
		return obj;
	}
	
	private JSONObject getBrowserDetails() throws JSONException
	{
		final Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		
		final JSONObject obj = new JSONObject();
		obj.put("name", caps.getBrowserName());
		obj.put("ver", caps.getVersion());
		
		return obj;
	}
	
	private String getScreenshot()
	{
		final TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
		return screenshotDriver.getScreenshotAs(OutputType.BASE64);
	}
	
	private String getTestID()
	{
		return new SessionStorage(driver).getTestName();
	}
	
	private String getUser()
	{
		return new SessionStorage(driver).getUser();
	}
	
	private JSONObject getSUTDetails() throws JSONException
	{
		final JSONObject obj = new JSONObject();
		obj.put("name", "AppUnderTest");
		
		final String version = new SessionStorage(driver).getSystemVersion();
		
		if (null != version && !version.isEmpty())
		{
			obj.put("ver", version);
		}
		
		return obj;
	}
	
	
	private JSONObject getCommand(final Command command) throws JSONException
	{
		final JSONObject obj = new JSONObject();
		obj.put("action", command.action);
		
		if(null != command.value && !command.value.isEmpty())
		{
			obj.put("using", command.using);
			obj.put("value", command.value);
		}
		return obj;
	}
	
	
	
	private String getExecutionID()
	{
		return new SessionStorage(driver).getExecutionID();
	}
	
	private String getURL()
	{
		return driver.getCurrentUrl();
	}
	
	private String getNode() throws JSONException
	{
		return new SessionStorage(driver).getNode();
	}
	
	private void send(final JSONObject obj) throws ConnectException
	{
	//TODO all commented out as capture isn't here yet
	//	RestAssured.reset();
	//	RestAssured.baseURI = "http://captureUrl:5000";
		
	//	given().contentType(ContentType.JSON).and().body(obj.toString()).then().post("screenshot");
	}
}
