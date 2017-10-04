package webApplet;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebpageScrapper {
	
	private String startTag; //String before the result data it can be used as a regex
	private String endTag; //String after the result data
	private String urlLink; //URL web page to scrap from
	private URL url; //A reference to the web site
	private URLConnection connection; //A communication link between the application and the URL
	
	private boolean useRegex;
	
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
				int index = inputLine.indexOf(getStartTag()) + getStartTag().length(); //find the index where the data string stars
				
				if(index != getStartTag().length()- 1) //if there is a tag
				{
					inputLine = inputLine.substring(index).trim(); //get the data string and trim the empty spaces
					int indexEnd = inputLine.indexOf(getEndTag()); //find the end of the data string
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
					int index = inputLine.indexOf(getStartTag()) + getStartTag().length(); //find the index where the data string stars
					
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
