package annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import datastructures.Pair;
import annotation.Annotation.Strand;
import annotation.predicate.ReadFlag;
import annotationcollection.AnnotationCollection;

public class PairedMappedFragment<T extends Annotation> extends AbstractAnnotation implements MappedFragment{

	private Pair<T> pair;
	
	public PairedMappedFragment(T v1, T v2){
		this(new Pair<T>(v1, v2));
	}
	
	public PairedMappedFragment(Pair<T> pair){
		this.pair=pair;
		ensureMatch();
	}
	
	private void ensureMatch() {
		if(!pair.getValue1().getName().equalsIgnoreCase(pair.getValue2().getName())){
			throw new IllegalArgumentException("Names of two reads in the pair must be equal");
		}
		
		if(!pair.getValue1().getReferenceName().equalsIgnoreCase(pair.getValue2().getReferenceName())){
			throw new IllegalArgumentException("Reference for two reads must be equal");
		}
		
	}

	/**
	 * @return Return the first of pair read
	 */
	public T getRead1(){return pair.getValue1();}
	
	/**
	 * @return Return the second of pair read
	 */
	public T getRead2(){return pair.getValue2();}
	
	@Override
	public String getName() {
		return pair.getValue1().getName();
	}

	@Override
	public String getReferenceName() {
		return pair.getValue1().getReferenceName();
	}

	@Override
	public int getReferenceStartPosition() {
		return Math.min(pair.getValue1().getReferenceStartPosition(), pair.getValue2().getReferenceStartPosition());
	}

	@Override
	public int getReferenceEndPosition() {
		return Math.max(pair.getValue1().getReferenceEndPosition(), pair.getValue2().getReferenceEndPosition());
	}

	@Override
	public Iterator<SingleInterval> getBlocks() {
		Collection<SingleInterval> rtrn=new ArrayList<SingleInterval>();
		
		Iterator<SingleInterval> iter1=pair.getValue1().getBlocks();
		Iterator<SingleInterval> iter2=pair.getValue2().getBlocks();
		
		while(iter1.hasNext()){rtrn.add(iter1.next());}
		while(iter2.hasNext()){rtrn.add(iter2.next());}
		
		return rtrn.iterator();
	}

	@Override
	public int getNumberOfBlocks() {
		return pair.getValue1().getNumberOfBlocks()+pair.getValue2().getNumberOfBlocks();
	}

	@Override
	public int size() {
		return pair.getValue1().size()+pair.getValue2().size();
	}
	
	/**
	 * @return The length of the fragment
	 */
	public int fragmentLength(){
		return getReferenceEndPosition()-getReferenceStartPosition();
	}

	@Override
	public Strand getOrientation() {
		return pair.getValue1().getOrientation();
	}

	@Override
	public int getRelativePositionFrom5PrimeOfFeature(int referenceStart) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public Collection<? extends ReadFlag> getReadFlags() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public void setOrientation(Strand orientation) {
		// Empty method
	}

	@Override
	public PairedMappedFragment<? extends Annotation> convert(Annotation feature) {
		//Ensure that this overlaps the feature
		if(overlaps(feature)){
			T read1=this.getRead1();
			T read2=this.getRead2();
			Annotation converted1=read1.convert(feature);
			Annotation converted2=read2.convert(feature);
			if(converted1!=null && converted2!=null){
				PairedMappedFragment<? extends Annotation> pair=new PairedMappedFragment<Annotation>(converted1, converted2);
				return pair;
			}
		}
		return null;
	}

	@Override
	public AnnotationCollection<DerivedAnnotation<? extends Annotation>> getWindows(
			int windowSize, int stepSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNumHits() {
		throw new UnsupportedOperationException();
	}

	
}
