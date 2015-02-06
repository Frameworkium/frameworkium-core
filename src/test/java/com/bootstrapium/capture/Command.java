package capture;

import org.openqa.selenium.WebElement;

public class Command {

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
		
		String using = null;
		String value = null;
		String id;
		String text;
		if(!(id = element.getAttribute("id")).isEmpty())
		{
			using = "id";
			value = id;
		}
		else if (!(text = element.getText()).isEmpty())
		{
			using = "linktext";
			value = text;
		}
		else
		{
			using = "css";
			value = element.getTagName() + "." + element.getAttribute("class");
		}
		
		this.using = using;
		this.value = value;
	}
}
