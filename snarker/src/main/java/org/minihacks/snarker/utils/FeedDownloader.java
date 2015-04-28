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
		//String rssUrl = "http://feeds.gawker.com/gawker/full#_ga=1.231192927.1110491708.1425241352";
		//String rssUrl = "http://topics.nytimes.com/top/opinion/editorialsandoped/oped/contributors/index.html?rss=1";
		//String rssUrl = "http://www.wonkette.com/feed";
		//String rssUrl = "http://www.usnews.com/rss/opinion";
		//String rssUrl = "http://fulltextrssfeed.com/www.neatorama.com/feed";
		//String rssUrl = "http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305";
		//String rssUrl = "http://feeds.reuters.com/reuters/topNews";
		String rssUrl = "http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&topic=b&output=rss";
		String filePrefix = filePath + "googlenews-" + System.currentTimeMillis() + "-";
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
