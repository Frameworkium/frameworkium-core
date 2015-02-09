package com.frameworkium.capture;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.jayway.restassured.http.ContentType;
import static com.jayway.restassured.RestAssured.given;

public class Capture {
/*
  public static String CaptureUrl = "http://captureUrl:5000";

  public static void newInstance(Command cmd, WebDriver webdriver) {
    newInstance(cmd, webdriver, null);
  }
  
  public static void newInstance(Command cmd, WebDriver webdriver, final String stacktrace) {
    Driver driver = webdriver instanceof Driver ? (Driver) webdriver
        : new Driver(webdriver);
        
    final String url = driver.getCurrentUrl();
    final String node = driver.getNodeAddress();
    final String screenshot = driver.captureScreenShot();
    
    final Properties props = Context.get();
    
    try {
      new Capture(props.getTestName(), props.getUniqueExecutionID(),
                  props.getUser(), cmd, props.getApplicationName(),
                  props.getApplicationVersion(), props.getBrowserName(),
                  props.getBrowserVersion(), irl, node, screenshot, stacktrace);
    }
    catch (JSONException e )
    {
      e.printStackTrace();
    }
  }
  
  public Capture(final String testID, final String exID, final String user,
                  final Command cmd, final String sutName, final String sutVer,
                  final String browserName, final String browserVer,
                  final String url, final String node, final String screenshot,
                  final String stacktrace) throws JSONException {
              final JSONObject obj = new JSONObject();
              
              obj.put("testID", testID);
              obj.put("exID", exID);
              obj.put("user", user);
              obj.put("cmd", cmdObj(cmd));
              obj.put("sut", sutObj(sutName, sutVer));
              obj.put("browser", browserObj(browserName, browserVer));
              obj.put("url", url);
              obj.put("node", node);
              obj.put("ss", screenshot);
              
              if (null != stacktrace) {
                obj.put("stacktrace", stacktrace);
              }
              
              send(obj);
    }
    
    private JSONObject browserObj(String browserName, String browserVer)
          throws JSONException {
      final JSONObject obj = new JSONObject();
      
      obj.put("name", browserName);
      obj.put("ver", browserVer);
      
      return obj;
    }
    
    private JSONObject sutObj(String sutName, String sutVer)
          throws JSONException {
      final JSONObject obj = new JSONObject();
      
      obj.put("name", sutName);
      obj.put("ver", sutVer);
      
      return obj;
    }
    
        private JSONObject cmdObj(final Command cmd)
          throws JSONException {
      final JSONObject obj = new JSONObject();
      
      obj.put("action", cmd.getAction());
      obj.put("action", cmd.getUsing());
      obj.put("action", cmd.getValue());
            
      return obj;
    }
    
    private void send(JSONObject obj) {
      given().contentType(ContentType.JSON).and().body(obj.toString()).then()
        .post(CaptureUrl + "/screenshot");
    }
    
    */
}    
