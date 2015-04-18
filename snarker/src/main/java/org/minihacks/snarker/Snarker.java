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

		String urlLower = "http://tktk.gawker.com/politicos-dylan-byers-works-for-fox-news-pr-1687509182/+LeahBeckmann".toLowerCase();
		
		SnarkReport report = snarker.processUrl(urlLower, urlLower);	
		System.out.println(report.toString());
	}
}
