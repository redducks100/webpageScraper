package webApplet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
    	String startTag = "<h1 class=\"uos-page-title uos-main-title uos-page-title-compressed\" property=\"name\">";
    	String endTag = "</h1>";
    	
    	WebpageScrapper scrapper = new WebpageScrapper("http://www.ecs.soton.ac.uk/people/pll", startTag, endTag);
		String print = scrapper.GetResult();
		print = scrapper.GetResult();
		System.out.println(print);
    	
    }
}
