package org.minihacks.snarker.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class FeedDownloader {

	public static void main(String[] args) throws Exception {
		String filePath="/Users/christinasickelco/Documents/snark_content/";
		String rssUrl = "http://feeds.gawker.com/gawker/full#_ga=1.231192927.1110491708.1425241352";
		String filePrefix = filePath + "gawker-" + System.currentTimeMillis() + "-";
        int limit=50;
        ArticleExtractor ae = ArticleExtractor.INSTANCE;
        
		URL url = new URL(rssUrl);
        HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
        // Reading the feed
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(httpcon));
        List<SyndEntry> entries = feed.getEntries();
        Iterator<SyndEntry> itEntries = entries.iterator();

        Scanner in = new Scanner(System.in);
        int counter=0;
        while (itEntries.hasNext() && counter < limit) {
            SyndEntry entry = itEntries.next();
            String link=entry.getLink();
       
            System.out.println("=== Processing " + link + "===");

			String text = ae.getText(new URL(link)); 
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        try {
		            desktop.browse(new URI(link));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		    
		   

		       // Reads a single line from the console 
		       // and stores into name variable
		    String isSnark  = in.nextLine();
		    
			File f = new File(filePrefix + counter + "-" + isSnark + ".txt");
			PrintWriter p = new PrintWriter(f);
			p.println(text);
			p.close();
            counter++;
        }
        in.close();
	}
}