package org.minihacks.snarker.tells;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class LemmaTellDetector implements SnarkTellDetector {

	private String name;
	private Set<String> tellWords = new HashSet<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<String> getTellWords() {
		return tellWords;
	}
	public void setTellWords(Set<String> tellWords) {
		this.tellWords = tellWords;
	}
	
	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		for (CoreMap sentence : sentences) {
	        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        	String lemma = token.lemma();
	        	if( tellWords.contains(lemma) ) {
	        		offenders.add(token.value());
	        	}
	        }
		}
		
		retval.setName(name);
		retval.setOffenders(offenders);
		return retval;
	}

	public static void main(String[] args) {
		StanfordCoreNLP pipeline;
		ArticleExtractor ae = ArticleExtractor.INSTANCE;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		Annotation annotation = pipeline.process("It was, plainly, music criticism");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		LemmaTellDetector d = new LemmaTellDetector();
		d.setName("Superiority Complex");
		Set<String> words = new HashSet<>();
		String[] superiorityWords = new String[]{
				"obviously", "unsurprisingly", "of course", 
				"clearly", "plainly", "tolerable",
				"certainly", "definitely", "evidently", "surely"};
		words.addAll(Arrays.asList(superiorityWords));
		d.setTellWords(words);
		SnarkTell t = d.detect(sentences);
		System.out.println(t);
		/*
		 * Knowing - “Unsurprisingly”, “obviously”, “clearly”, “plainly”, “apparently”
“Unsurprisingly, there was a lot of finger-wagging on social media about the sketch”. Dakota Johnson Joined ISIS on SNL and People Sure Are Upset About It
It’s all unsurprising but still entertaining. Jet-Setting Freeloader Chris Christie is Ready to Lead America. 
Writers who take money from Red Bull are, obviously, aware of this dynamic” - Gawker
“It was, plainly, music criticism” - Gawker
		 */

	}

}
