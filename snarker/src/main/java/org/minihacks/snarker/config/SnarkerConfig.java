package org.minihacks.snarker.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.minihacks.snarker.Snarker;
import org.minihacks.snarker.tells.ExcessiveExclamationPoints;
import org.minihacks.snarker.tells.FileLemmaTellDetector;
import org.minihacks.snarker.tells.FilePhraseDetector;
import org.minihacks.snarker.tells.OneWordSentence;
import org.minihacks.snarker.tells.RegexAllDetector;
import org.minihacks.snarker.tells.RegexSentenceDetector;
import org.minihacks.snarker.tells.SnarkTellDetector;
import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@Configuration
public class SnarkerConfig {
	
	@Bean
	public Snarker snarker() throws IOException {
		Snarker snarker = new Snarker();
		snarker.setDetectors(detectors());
		snarker.setPipeline(pipeline());
		return snarker;
	}
	
	@Bean
	public StanfordCoreNLP pipeline() {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		return pipeline;
	}
	
	@Bean
	public List<SnarkTellDetector> detectors () throws IOException {
		List<SnarkTellDetector> retval = new LinkedList<SnarkTellDetector>();
		retval.add(knowingFileLemmaDetector());
		retval.add(hostileFileLemmaDetector());
		retval.add(conversationalDetector());
		retval.add(oneWordSentence());
		retval.add(excessiveExclamationPoints());
		retval.add(knowingPhraseDetector());
		retval.add(hostileVerbDetector());
		retval.add(pawpDetector());
		retval.add(conversationalSentencesDetector());
		retval.add(sarcasmDetector1());
		retval.add(fakeEnthusiasmDetector());
		retval.add(profanityDetector());
		return retval;
	}

	@Bean
	public FileLemmaTellDetector knowingFileLemmaDetector() throws IOException {
		FileLemmaTellDetector d = new FileLemmaTellDetector();
		d.setName("Knowing Detector");
		d.setFile("knowing-lemmas.txt");
		d.setDimension(SnarkDimension.KNOWING);
		return d;
	}
	
	@Bean
	public FileLemmaTellDetector hostileFileLemmaDetector() throws IOException {
		FileLemmaTellDetector d = new FileLemmaTellDetector();
		d.setName("Hostile Detector");
		d.setFile("hostile-lemmas.txt");
		d.setDimension(SnarkDimension.HOSTILE);
		return d;
	}
	
	@Bean
	public FilePhraseDetector conversationalDetector() throws IOException {
		FilePhraseDetector d = new FilePhraseDetector();
		d.setName("Conversational Phrases Detector");
		d.setFile("conversational-phrases.txt");
		d.setDimension(SnarkDimension.IRREVERENT);
		return d;
	}
	
	@Bean
	public OneWordSentence oneWordSentence() {
		return new OneWordSentence();
	}
	
	@Bean
	public ExcessiveExclamationPoints excessiveExclamationPoints() {
		return new ExcessiveExclamationPoints();
	}
	
	@Bean
	public RegexSentenceDetector knowingPhraseDetector() {
		RegexSentenceDetector knowingPhraseDetector = new RegexSentenceDetector();
		knowingPhraseDetector.setName("KnowingPhrases");
		knowingPhraseDetector.setDimension(SnarkDimension.KNOWING);
		Set<String> phrases = new HashSet<>();
		String[] knowingWords = new String[]{
				".*not that hard.*", 
				".*of course.*", 
				".*should make that .* clear.*", 
				".*you might think.*", 
				".*you'd be wrong.*", 
				".*you would be .* wrong.*",
				".*type of .* person.*",
				".*if this is your .*",
				".*you probably.*",
				".*('s)|(is)|(was) (\\b){0,4} (right)|(wrong)|(bad)|(good).*",
				".*in real life.*",
				".*now you know.*"
		};
		phrases.addAll(Arrays.asList(knowingWords));
		knowingPhraseDetector.setTellExpressions(phrases);
		return knowingPhraseDetector;
	}
	
	@Bean
	public RegexSentenceDetector hostileVerbDetector() {
		Set<String> phrases = new HashSet<>();
		RegexSentenceDetector hostileVerbDetector = new RegexSentenceDetector();
		hostileVerbDetector.setName("Hostile Verbs");
		hostileVerbDetector.setDimension(SnarkDimension.HOSTILE);
		Set<String> hostileVerbPhrases = new HashSet<>();
		String[] hostilePhrases = new String[]{
				".*dream(ed)*(s)* up.*",
				".*trying to be.*",
				".*tries to be.*",
				".*getting their way.*",
				".*getting his way.*",
				".*getting her way.*",
				".*getting your way.*",
				".*\\, please\\."
		};
		phrases.addAll(Arrays.asList(hostilePhrases));
		hostileVerbDetector.setTellExpressions(hostileVerbPhrases);
		return hostileVerbDetector;
	}
	
	@Bean
	public RegexSentenceDetector pawpDetector() {
		RegexSentenceDetector pawpDetector = new RegexSentenceDetector();
		pawpDetector.setName("Passive Agressive Wishy-Washy Phrases");
		pawpDetector.setDimension(SnarkDimension.HOSTILE);
		Set<String> pawpExpressions = new HashSet<>();
		String[] pawpArray = new String[]{
				".*we're not saying.*",
				".*i'm not saying.*",
				".*we are not saying.*",
				".*i am not saying.*",
				".*not (quite)* in the way.*",
				".*quasi.*"
		};
		pawpExpressions.addAll(Arrays.asList(pawpArray));
		pawpDetector.setTellExpressions(pawpExpressions);
		return pawpDetector;
	}
	
	@Bean
	public RegexSentenceDetector conversationalSentencesDetector() {
		RegexSentenceDetector excessivelyConversationalPhraseDetector = new RegexSentenceDetector();
		excessivelyConversationalPhraseDetector.setName("Excessively conversational phrase detector");
		excessivelyConversationalPhraseDetector.setDimension(SnarkDimension.IRREVERENT);
		Set<String> conversationalPhrases = new HashSet<>();
		String[] conversationalPhraseArray = new String[]{
				".*\\, you know.*",
				".*or whatever.*",
				".*sure are.*",
				".*\\, you know.*\\?",
				".*\\bsuper\\b.*",
				"because .*", //starting a sentence with because
				"but .",	  //starting a sentence with but
				".*\\.\\.\\.(\\b\\w+\\b){1,3}" //which is...something.
		};
		conversationalPhrases.addAll(Arrays.asList(conversationalPhraseArray));
		excessivelyConversationalPhraseDetector.setTellExpressions(conversationalPhrases);
		return excessivelyConversationalPhraseDetector;
	}
	
	@Bean
	public RegexAllDetector sarcasmDetector1() {
		RegexAllDetector sarcasmDetector1 = new RegexAllDetector();
		sarcasmDetector1.setName("SarcasmDetector1");
		Set<String> phrases2 = new HashSet<>();
		String[] pattern2 = new String[]{
				"(\\b\\w+\\b){1,5}\\?\\s*(\\b\\w+\\b){1,3}",
				"\\, (\\b\\w+\\b){1,4}\\!"
		};
		phrases2.addAll(Arrays.asList(pattern2));
		sarcasmDetector1.setDimension(SnarkDimension.HOSTILE);
		sarcasmDetector1.setTellExpressions(phrases2);
		return sarcasmDetector1;
	}
	
	@Bean
	public RegexAllDetector fakeEnthusiasmDetector() {
		RegexAllDetector fakeEnthusiasmDetector = new RegexAllDetector();
		fakeEnthusiasmDetector.setName("Fake enthusiasm");
		Set<String> phrases3 = new HashSet<>();
		String[] pattern3 = new String[]{
				"((\\b\\w+\\b){1,7}\\!\\s*(\\b\\w+\\b){1,7}\\!)"
		};
		phrases3.addAll(Arrays.asList(pattern3));
		fakeEnthusiasmDetector.setDimension(SnarkDimension.HOSTILE);
		fakeEnthusiasmDetector.setTellExpressions(phrases3);
		return fakeEnthusiasmDetector;
	}
	
	@Bean
	public FilePhraseDetector profanityDetector() throws IOException {
		FilePhraseDetector profanityDetector = new FilePhraseDetector();
		profanityDetector.setDimension(SnarkDimension.IRREVERENT);
		profanityDetector.setName("Profanity Detector");
		profanityDetector.setFile("profanity.txt");
		return profanityDetector;
	}
}
