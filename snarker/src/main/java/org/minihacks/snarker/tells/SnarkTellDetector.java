package org.minihacks.snarker.tells;

import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public interface SnarkTellDetector {

	SnarkTell detect(Annotation annotation);
}
