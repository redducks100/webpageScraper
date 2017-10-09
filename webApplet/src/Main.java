

import java.util.List;

public class Main {

    public static void main(String[] args)
    {    	
    	String startTag = "<a href=\"https://secure.ecs.soton.ac.uk/people/([a-z]{2,3}[0-9][a-z][0-9]{2})\">";
    	String endTag = "</a>";
    	
    	
    	
    	WebpageScrapper scrapper = new WebpageScrapper("https://secure.ecs.soton.ac.uk/people/dem/related_people", startTag, endTag);
    	/*scrapper.SetRegex(true);
		List<String> print = scrapper.GetResults();
		for(int i=0;i<print.size();i++)
		{
			System.out.println(print.get(i));
		}*/	
    	try {
			//String s = scrapper.Login("https://secure.ecs.soton.ac.uk/login/?uri=%2F&args=","https://secure.ecs.soton.ac.uk/people/dem/related_people");
			//System.out.println(s);
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
