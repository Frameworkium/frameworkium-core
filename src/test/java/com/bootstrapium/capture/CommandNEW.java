package capture;

import org.openqa.selenium.WebElement;

public class CommandNEW {

	public String action;
	public String using;
	public String value;
	
	public Command(final String action, final String using, final String value)
	{
		this.action = action;
		this.using = using;
		this.value = value;
	}
	
	public Command(final String action, final WebElement element)
	{
		this.action = action;
		
		String id;
		String text;
		if(!(id = element.getAttribute("id")).isEmpty())
		{
			this.using = "id";
			this.value = id;
		}
		else if (!(text = element.getText()).isEmpty())
		{
			this.using = "linktext";
			this.value = text;
		}
		else
		{
			this.using = "css";
			this.value = element.getTagName() + "." + element.getAttribute("class");
		}
	}
	
	public String getAction() {
	 return action;
	}
	 
	public String getUsing() {
	 return using;
	}
	   
	public String getValue() {
	 return value;
	}
}
