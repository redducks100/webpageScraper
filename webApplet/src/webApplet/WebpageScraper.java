package webApplet;

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

public class WebpageScraper {
	
	private String startTag; //String before the result data it can be used as a regex
	private String endTag; //String after the result data
	private String urlLink; //URL web page to scrap from
	private URL url; //A reference to the web site
	
	private boolean useRegex = false; //by default is set to false
	
	//Constructor
	public WebpageScraper(String urlLink) 
	{
		this.urlLink = urlLink;
		
		try {
			url = new URL(urlLink);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void useRegex(boolean value)
	{
		useRegex = value;
	}
	
	private URLConnection ConnectToWebsite()
	{
		URLConnection connection = null;
		try {
			connection = url.openConnection(); //open the connection to the web site
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return connection;
	}
	
	private List<String> GetSource(URLConnection connection)
	{
		List<String> source = new ArrayList<String>();
		
		int index = 0;
		String inputLine = "";
	
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //initialise the reader to read from the website source code
			inputLine = bReader.readLine(); //read first line
			while(inputLine != null)
			{
				source.add(new String(inputLine));
				inputLine = bReader.readLine();
			}			
			bReader.close(); //close the reader (done)
		} catch (IOException e) {
			throw new RuntimeException(e); //Something failed while reading the website source
		}
		
		return source;
	}
	
	private List<String> ParseSource(List<String> source)
	{
		List<String> results = new ArrayList<String>();
		
		for(int i=0;i<source.size();i++)
		{
			String currentLine = source.get(i);
			
			int index = currentLine.indexOf(startTag) + startTag.length(); //find the index where the data string stars
			
			if(index != getStartTag().length()- 1) //if there is a tag
			{
				currentLine = currentLine.substring(index).trim(); //get the data string and trim the empty spaces
				int indexEnd = currentLine.indexOf(endTag); //find the end of the data string
				currentLine = currentLine.substring(0,indexEnd);
								
				results.add(new String(currentLine)); //return it
			}
		}
		
		return results;
	}
	
	private List<String> ParseSourceRegex(List<String> source)
	{
		List<String> results = new ArrayList<String>();
		
		Pattern pattern = null;
		Matcher matcher = null;
		pattern = Pattern.compile(startTag);
		
		for(int i=0;i<source.size();i++)
		{
			String currentLine = source.get(i);
			
			if(!currentLine.isEmpty() && currentLine != null)
			{
				currentLine = currentLine.trim();
				matcher = pattern.matcher(currentLine);
				while(matcher.find())
				{
					int index = matcher.end();
					String result = currentLine.substring(index, currentLine.length()-1);
					int indexEnd = result.indexOf(endTag);
					result = result.substring(0,indexEnd);
					results.add(new String(result));
				}
			}
		}
		
		return results;
	}
	
	public List<String> GetResults(String loginURL, String[] inputs) throws Exception
	{
		if(startTag.isEmpty() || startTag == null || endTag.isEmpty() || endTag == null)
		{
			throw new Exception("Tags can't be empty or null.");
		}
		
		List<String> source = null;
		
		if(loginURL != null)
		{
			String content = Login(loginURL, urlLink, inputs);
			source = new ArrayList<String>();
			source.add(new String(content));
		}
		else
		{
			source = GetSource(ConnectToWebsite());
		}
		
		if(useRegex)
		{
			return ParseSourceRegex(source);
		}
		
		return ParseSource(source);
	}
	
	private String Login(String loginURL, String url , String[] inputs) throws Exception
	{
		if(inputs.length < 3)
		{
			throw new Exception("Inputs can't be less than 3 (username, password, login button");
		}
		
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	    
		WebClient wb = new WebClient(BrowserVersion.CHROME);
		wb.getCookieManager().setCookiesEnabled(true);
		
		System.out.println("This website needs login information.");
		System.out.println("Please enter your username: ");
		
		Scanner sc = new Scanner(System.in);
		String username = sc.nextLine();
		
		System.out.println("Please enter your password (WARNING! IT IS NOT HIDDEN FROM THE CONSOLE): ");
		String password = sc.nextLine();
		
		try {	
			HtmlPage loginPage = wb.getPage(loginURL);		
			HtmlForm loginForm = loginPage.getForms().get(1);	
			
			HtmlInput user = loginForm.getInputByName(inputs[0]);
			user.type("text");
			user.setValueAttribute(username);
			
			HtmlInput pass = loginForm.getInputByName(inputs[1]);
			pass.type("password");
			pass.setValueAttribute(password);
			
			loginForm.getInputByValue(inputs[2]).click();
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
	}

	public String getEndTag() {
		return endTag;
	}

	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}
}
