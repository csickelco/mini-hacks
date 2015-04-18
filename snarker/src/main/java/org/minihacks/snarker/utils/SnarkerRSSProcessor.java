package org.minihacks.snarker.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.SnarkReport;
import org.minihacks.snarker.Snarker;
import org.minihacks.snarker.config.SnarkerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import ch.qos.logback.classic.Level;

public class SnarkerRSSProcessor {

	public static void main(String[] args) throws Exception {
		String rssFeed = "http://feeds.gawker.com/gawker/full";
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SnarkerConfig.class);
		ctx.registerShutdownHook();
		
		Snarker snarker = ctx.getBean(Snarker.class);
		
		List<String> urls = getUrlsFromRssFeed(rssFeed, 20);
		
		for (String url : urls) {
			String urlLower = url.toLowerCase();
			SnarkReport report = snarker.processUrl(urlLower, urlLower);
			System.out.println(report.toString());
		}
	}

	public static List<String> getUrlsFromRssFeed(String rssUrl, int limit) throws IOException, IllegalArgumentException, FeedException {
		List<String> retval = new LinkedList<>();
		int counter = 0;
		
		URL url = new URL(rssUrl);
        HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(httpcon));
        List<SyndEntry> entries = feed.getEntries();
        Iterator<SyndEntry> itEntries = entries.iterator();
 
        while (itEntries.hasNext() && counter < limit) {
            SyndEntry entry = itEntries.next();
            retval.add(entry.getLink());
            counter++;
        }
        
        return retval;
	}
}
