package org.minihacks.snarker;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.QuotationsAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class QuoteTest {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("annotators", "quote");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation annotation = pipeline.process("This sentence has no quotes. But \"this sentence does.\" And 'so does this one.' But then this doesn't. A \"one\" word quote");
		List<CoreMap> quotations = annotation.get(QuotationsAnnotation.class);
		for (CoreMap coreMap : quotations) {
			System.out.println(coreMap.toString());
			System.out.println(coreMap.get(CharacterOffsetBeginAnnotation.class) + "-" + coreMap.get(CharacterOffsetEndAnnotation.class));
		}
	}

}
