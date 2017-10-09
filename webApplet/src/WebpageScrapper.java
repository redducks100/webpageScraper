import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebpageScrapper {
	
	private String startTag; //String before the result data it can be used as a regex
	private String endTag; //String after the result data
	private String urlLink; //URL web page to scrap from
	private String loginURL; //URL web page if the website needs login
	private URL url; //A reference to the web site
	private URLConnection connection; //A communication link between the application and the URL
	
	private boolean useRegex;
	private boolean requiresLogin;
	
	private String lastResult; //Cache the result
	
	private BufferedReader bReader; //Reads text from a character-input stream
	
	
	//Constructor
	public WebpageScrapper(String urlLink, String startTag, String endTag) 
	{
		this.setStartTag(startTag);
		this.urlLink = urlLink;
		this.setEndTag(endTag);
		
		try {
			url = new URL(urlLink);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		lastResult = "";
	}
	
	public void SetRegex(boolean value)
	{
		useRegex = value;
	}
	
	public void RequiresLogin(boolean value,String loginURL)
	{
		requiresLogin = value;
		this.loginURL = loginURL;
	}
	
	public String GetResult()
	{
		if(lastResult != "")
			return lastResult; //check if we have a previous result for the same settings and return it
		
		try {
			connection = url.openConnection(); //open the connection to the web site
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		String inputLine = ""; //initialise the input data
		
		try {
			
			bReader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //initialise the reader to read from the website source code
			inputLine = bReader.readLine(); //read first line
			while(inputLine != null)
			{
				int index = inputLine.indexOf(startTag) + startTag.length(); //find the index where the data string stars
				
				if(index != getStartTag().length()- 1) //if there is a tag
				{
					inputLine = inputLine.substring(index).trim(); //get the data string and trim the empty spaces
					int indexEnd = inputLine.indexOf(endTag); //find the end of the data string
					lastResult = inputLine.substring(0,indexEnd); // cache the result
					
					bReader.close(); //close the reader (done)
					
					return lastResult; //return it
				}
				
				inputLine = bReader.readLine();
			}
			
			bReader.close(); //close the reader (done)
			
		} catch (IOException e) {
			throw new RuntimeException(e); //Something failed while reading the website source
		}
		
		lastResult = ""; //cache the result
		return lastResult; //return it
	}
	
	public List<String> GetResults()
	{
		List<String> results = new ArrayList<String>();
		
		try {
			connection = url.openConnection(); //open the connection to the web site
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		String inputLine = ""; //initialise the input data
		
		try {
			
			Pattern pattern = null;
			Matcher matcher = null;
			
			if(useRegex)
			{
				pattern = Pattern.compile(startTag);
			}
			
			bReader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //initialise the reader to read from the website source code
			inputLine = bReader.readLine(); //read first line
			while(inputLine != null)//|| currentIndex < amount)
			{
				System.out.println(inputLine);
				if(!useRegex)
				{
					int index = inputLine.indexOf(startTag) + startTag.length(); //find the index where the data string stars
					
					if(index != getStartTag().length()- 1) //if there is a tag
					{
						inputLine = inputLine.substring(index).trim(); //get the data string and trim the empty spaces
						int indexEnd = inputLine.indexOf(endTag); //find the end of the data string
						results.add(inputLine.substring(0,indexEnd));
					}
				}
				else
				{
					if(!inputLine.isEmpty() && inputLine != null)
					{
						inputLine = inputLine.trim();
						matcher = pattern.matcher(inputLine);
						if(matcher.find())
						{
							int index = matcher.end();
							String result = inputLine.substring(index, inputLine.length()-1);
							int indexEnd = result.indexOf(endTag);
							results.add(result.substring(0,indexEnd));
						}
					}
				}
				inputLine = bReader.readLine();
			}
			
			bReader.close(); //close the reader (done)
			
			return results; //returning the first results (amount)
			
		} catch (IOException e) {
			throw new RuntimeException(e); //Something failed while reading the web site source
		}
	}
	
	private String Login(String loginURL, String url)
	{
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		
	    WebClient wb = new WebClient(BrowserVersion.CHROME);
		wb.getCookieManager().setCookiesEnabled(true);
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("This website needs login information.");
		System.out.println("Please enter your username: ");
		
		String username = sc.nextLine();
		System.out.println("Please enter your password: ");
		String password = sc.nextLine();
		
		sc.close();
		
		try {	
			HtmlPage loginPage = wb.getPage("https://secure.ecs.soton.ac.uk/login/?uri=%2F&args=");		
			HtmlForm loginForm = loginPage.getForms().get(1);	
			
			HtmlInput user = loginForm.getInputByName("ecslogin_username");
			user.type("text");
			user.setValueAttribute(username);
			
			HtmlInput pass = loginForm.getInputByName("ecslogin_password");
			pass.type("password");
			pass.setValueAttribute(password);
			
			loginForm.getInputByValue("Log in....").click();
			
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return wb.getPage(url).getWebResponse().getContentAsString();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	//Encapsulation
	public String getStartTag() {
		return startTag;
	}

	public void setStartTag(String startTag) {
		this.startTag = startTag;
		lastResult = "";
	}

	public String getEndTag() {
		return endTag;
	}

	public void setEndTag(String endTag) {
		this.endTag = endTag;
		lastResult = "";
	}
}
