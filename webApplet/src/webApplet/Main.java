package webApplet;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {   
    	
    	String email = "";
    	String user = "";
    	Scanner scanner = new Scanner(System.in);
    	
    	System.out.println("Enter a valid soton email adress: ");
    	
    	//reading the email address from the user
    	email = scanner.nextLine();
    	
    	//formatting the email address to get the username
    	int index = email.indexOf("@");
    	
    	while(index == -1)
    	{
    		System.out.println("Print enter a valid soton email adress!");
    	}
		user = email.substring(0,index);
    	
		//where to look for the information
    	String urlLinkBasic = "http://www.ecs.soton.ac.uk/people/" + user;
    	//where the information starts
    	String startTagBasic = "property=\"name\">";
    	//Where it should stop looking
    	String endTagBasic = "</h1>";
    	
    	//setting up the scraper for a basic scrap without regex or login required
    	WebpageScraper scraper = new WebpageScraper(urlLinkBasic);
    	scraper.setStartTag(startTagBasic);
    	scraper.setEndTag(endTagBasic);
    	String fullName = "";
    	try {
			List<String> results = scraper.GetResults(null, null);
			fullName = results.get(0);
			System.out.println("The full name of the email's address owner is: " + fullName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	System.out.println('\n');
    	System.out.println("Getting related people of " + fullName + ": ");
    	
    	try {
    		//Setting up the regex to look for the related people's web-site addresses
        	String startTagAdvanced = "<a href='https://secure.ecs.soton.ac.uk/people/(([a-z]{2,4}[0-9]{1,2}[a-z][0-9]{2})|([a-z]{3}))'>";
        	String loginURL = "https://secure.ecs.soton.ac.uk/login/?uri=%2F&args=";
        	//Where it should stop looking
        	String endTagAdvanced = "</a>";
        	//where it should scrap from after login in
        	String urlLinkAdvanced = "https://secure.ecs.soton.ac.uk/people/"+user+"/related_people";
        	
        	//Setting up the scraper for a regex scrap
    		scraper = new WebpageScraper(urlLinkAdvanced);
        	scraper.setStartTag(startTagAdvanced);
        	scraper.setEndTag(endTagAdvanced);
        	scraper.useRegex(true);
        	//informing the scraper that it needs to log in and what it should look for when logging in 
			List<String> results = scraper.GetResults(loginURL, new String[]{"ecslogin_username","ecslogin_password","Log in...."}); //(0-usernameInput,1-passwordInput,2-Log in button)
			//Print all the results we get from the scraper
			if(results.size() <= 0)
			{
				System.out.println("Couldn't find any related people.");
			}
			else
			{
				System.out.println("Related people: \n");
			}
			for(int i=2;i<results.size();i++)
			{
				System.out.println(results.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
}
