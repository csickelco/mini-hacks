package org.minihacks.snarker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.minihacks.snarker.tells.ExcessiveExclamationPoints;
import org.minihacks.snarker.tells.LemmaTellDetector;
import org.minihacks.snarker.tells.OneWordSentence;
import org.minihacks.snarker.tells.SnarkTell;
import org.minihacks.snarker.tells.SnarkTellDetector;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Snarker {

	public static void main(String[] args) throws Exception {
		//Configure Detectors
		LemmaTellDetector superiorityComplex = new LemmaTellDetector();
		superiorityComplex.setName("Superiority Complex");
		Set<String> words = new HashSet<>();
		String[] superiorityWords = new String[]{
				"obviously", "obvious", "unsurprisingly", "unsurprising",
				"clearly", "plainly", "tolerable", "apparently",
				"certainly", "definitely", "evidently", "surely"};
		words.addAll(Arrays.asList(superiorityWords));
		superiorityComplex.setTellWords(words);
		
		//This one probably needs refinement. What if the topic is poison? or garbage?
		//Also, there has to be a ton of words we're missing
		//Would sentiment analysis help?
		LemmaTellDetector hostileMuch = new LemmaTellDetector();
		hostileMuch.setName("Hostile much?");
		Set<String> hostileMuchWords = new HashSet<>();
		String[] hostileMuchWordsArray = new String[]{
				"poison", "garbage", "trash", "junk", "dumb", "dumber", "dumbest",
				"idiot", "idiotic", "terrible", "awful", "horrible",
				"abomination", "tolerable",
				"drab", "boring", "uncool", "lame", "absurd", "bland"};
		hostileMuchWords.addAll(Arrays.asList(hostileMuchWordsArray));
		hostileMuch.setTellWords(hostileMuchWords);
		
		OneWordSentence oneWordSentence = new OneWordSentence();
		ExcessiveExclamationPoints excessiveExclamationPoints = new ExcessiveExclamationPoints();
		
		SnarkTellDetector detectors[] = {excessiveExclamationPoints, oneWordSentence, 
				superiorityComplex, hostileMuch};
		StanfordCoreNLP pipeline;
		ArticleExtractor ae = ArticleExtractor.INSTANCE;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		List<String> urls = getFilesFromPath("/Users/christinasickelco/Documents/snark_content");
		/*
		List<String> urls = getHardcodedUrls();
		List<String> urls = getUrlsFromRssFeed(
				"http://feeds.gawker.com/gawker/full#_ga=1.231192927.1110491708.1425241352", 
				20);
		*/
		
		for (String url : urls) {
			System.out.println("=== Processing " + url + "===");

			String urlLower = url.toLowerCase();
			String text = null;
			if( urlLower.startsWith("http") || urlLower.startsWith("www") ) {
				text = ae.getText(new URL(url));  
			} else {
				text = IOUtils.toString(new FileReader(new File(url)));
			}
			//System.out.println("----------------------");
			//System.out.println("Converted text: " + text);
			
			Annotation annotation = pipeline.process(text);
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			for (SnarkTellDetector detector : detectors) {
				SnarkTell tellResult = detector.detect(sentences);
				System.out.println(tellResult);
			}	
			//System.out.println("----------------------");
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