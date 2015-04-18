package org.minihacks.snarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.minihacks.snarker.config.SnarkerConfig;
import org.minihacks.snarker.tells.SnarkTell;
import org.minihacks.snarker.tells.SnarkTellDetector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ch.qos.logback.classic.Level;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Snarker {
	
	private List<SnarkTellDetector> detectors;
	StanfordCoreNLP pipeline;

	public List<SnarkTellDetector> getDetectors() {
		return detectors;
	}
	public void setDetectors(List<SnarkTellDetector> detectors) {
		this.detectors = detectors;
	}
	public StanfordCoreNLP getPipeline() {
		return pipeline;
	}
	public void setPipeline(StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
	}

	public SnarkReport processText(String articleName, String text) {
		SnarkReport retval = new SnarkReport();
		
		retval.setArticle(articleName);
		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (SnarkTellDetector detector : detectors) {
			SnarkTell tellResult = detector.detect(sentences);
			if( tellResult.getOffenders().size() > 0 ) {
				retval.addDimension(tellResult.getDimension());
			}
		}	
		
		return retval;
	}
	
	public SnarkReport processUrl(String articleName, String url) throws MalformedURLException, BoilerpipeProcessingException {
		ArticleExtractor ae = ArticleExtractor.INSTANCE;		
		String text = ae.getText(new URL(url));
		return processText(articleName, text);
	}
	
	public SnarkReport processFile(String articleName, String filepath) throws FileNotFoundException, IOException {	
		String text = IOUtils.toString(new FileReader(new File(filepath)));
		return processText(articleName, text);
	}

	public static void main(String[] args) throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SnarkerConfig.class);
		ctx.registerShutdownHook();
		
		Snarker snarker = ctx.getBean(Snarker.class);
		List<String> urls = getFilesFromPath("/Users/christinasickelco/Documents/snark_content");
		/*
		List<String> urls = getHardcodedUrls();
		List<String> urls = getUrlsFromRssFeed(
				"http://feeds.gawker.com/gawker/full#_ga=1.231192927.1110491708.1425241352", 
				20);
		*/
		
		for (String url : urls) {
			String urlLower = url.toLowerCase();
			SnarkReport report;
			if( urlLower.startsWith("http") || urlLower.startsWith("www") ) {
				report = snarker.processUrl(urlLower, urlLower);
			} else {
				report = snarker.processFile(urlLower, url);
			}

			if( report.getDimensions().size() == 3 ) {
				System.out.println("!!!" + report.toString());
			} else {
				System.out.println(report.toString());
			}
		}

	}
	
	public static List<String> getFilesFromPath(String path) {
		List<String> retval = new LinkedList<>();
		File folder = new File(path);
		if( folder.isDirectory() ) {
			File[] files = folder.listFiles();
			for (File file : files) {
				retval.add(file.getAbsolutePath());
			}
		} else {
			retval.add(path);
		}
		return retval;
	}

	public static List<String> getHardcodedUrls() {
		List<String> retval = new LinkedList<>();
		retval.add("http://tktk.gawker.com/politicos-dylan-byers-works-for-fox-news-pr-1687509182/+LeahBeckmann");
		retval.add("http://justice.gawker.com/jet-setting-freeloader-chris-christie-is-ready-to-lead-1683535337");
		return retval;
	}
	
	public static List<String> getUrlsFromRssFeed(String rssUrl, int limit) throws IOException, IllegalArgumentException, FeedException {
		List<String> retval = new LinkedList<>();
		int counter = 0;
		
		URL url = new URL(rssUrl);
        HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
        // Reading the feed
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
