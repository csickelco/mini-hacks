package org.minihacks.snarker.tells;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class NegativeSentimentTellDetector implements SnarkTellDetector {

	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		
		String[] sentimentText = { "Very Negative", "Negative", "Neutral", "Positive", "Very Positive" };
		for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
	        int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	        System.out.println( sentence.toString() + ": " + sentiment + "-" + sentimentText[sentiment]);
		}
		
		retval.setName("Negative sentiment detector");
		//retval.setOffenders(offenders);
		return retval;
	}

	public static void main(String[] args) {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);

		Annotation annotation = pipeline.process("Just days after two members of Oklahoma University's defunct SAE chapter were expelled from the school, the fraternity is lawyering up: \"They should not be tarred and feathered as racists,\" their attorney says. Good luck to this guy. " +
				"OU Expels Two Students Involved in Racist Frat Chant OU Expels Two Students Involved in Racist Frat Chant OU Expels Two Students Involved in Racist Frat Cha" +
				"The Oklahoma Kappa chapter of Sigma Alpha Epsilon is down two members today after they were caught… Read more Read more" +
				"KFOR News in Oklahoma City reports the chapter, which doesn't even technically exist as a fraternity and is basically now just an informal association of racist young men, has tapped Stephen Jones, the lawyer whose most notable client was Oklahoma City bomber Timothy McVeigh:" + 
				"Jones says the two students who were expelled because of the incident have apologized sincerely for their remarks, and now the incident is being exploited. " +
				"He said they lacked judgment in a social setting, but they should not be tarred and feathered as racists. " +
				"Proving that a bus filled with white men singing isn't racist will be only slightly less challenging than defending McVeigh in court. " +
				"Update 3:54 PM ET: At a press conference this afternoon, Jones clarified that the disbanded chapter and its alumni are only considering legal action at this point, but have not yet sued the university or its president.");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		NegativeSentimentTellDetector d = new NegativeSentimentTellDetector();
		SnarkTell t = d.detect(sentences);
		System.out.println(t);
	}

}
