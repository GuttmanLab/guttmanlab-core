package guttmanlab.core.annotation.predicate;

import guttmanlab.core.annotation.Annotation;

import org.apache.commons.collections15.Predicate;

public class MaximumLengthFilter<T extends Annotation> implements Predicate<T>{

	int maxSize;
	
	public MaximumLengthFilter(int maxSize){
		this.maxSize=maxSize;
	}
	
	@Override
	public boolean evaluate(T annot) {
		if(annot.getReferenceEndPosition()-annot.getReferenceStartPosition() < maxSize){return true;}
		return false;
	}

}
