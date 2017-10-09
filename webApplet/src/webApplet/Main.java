package webApplet;

import java.util.List;

public class Main {

    public static void main(String[] args)
    {   
    	String startTagAdvanced = "<a href=\"https://secure.ecs.soton.ac.uk/people/([a-z]{2,3}[0-9][a-z][0-9]{2})\">";
    	String endTagAdvanced = "</a>";
    	String urlLinkAdvanced = "https://secure.ecs.soton.ac.uk/people/dem/related_people";
    	
    	String urlLinkBasic = "http://www.ecs.soton.ac.uk/people/dem";
    	String startTagBasic = "property=\"name\">";
    	String endTagBasic = "</h1>";
    	
    	WebpageScraper scraper = new WebpageScraper(urlLinkBasic);
    	scraper.setStartTag(startTagBasic);
    	scraper.setEndTag(endTagBasic);
    	try {
			List<String> results = scraper.GetResults(null, null);
			
			for(int i=0;i<results.size();i++)
			{
				System.out.println(results.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
    		scraper = new WebpageScraper(urlLinkAdvanced);
        	scraper.setStartTag(startTagAdvanced);
        	scraper.setEndTag(endTagAdvanced);
        	scraper.useRegex(true);
			List<String> results = scraper.GetResults("https://secure.ecs.soton.ac.uk/login/?uri=%2F&args=", new String[]{"ecslogin_username","ecslogin_password","Log in...."});
			for(int i=0;i<results.size();i++)
			{
				System.out.println(results.get(i));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
