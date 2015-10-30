package guttmanlab.core.annotation;

import guttmanlab.core.annotationcollection.AnnotationCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class that represent a simple contiguous interval. This is the basis for all features.
 * Interval coordinates are zero-based, left-closed and right-open.
 * @author mguttman
 */
public class SingleInterval extends AbstractAnnotation{

	private String referenceName;
	private int startPos;
	private int endPos;
	private Strand orientation;
	private String featureName;

	/**
	 * Constructor. Start and end coordinates are zero-based, left-closed and right-open.
	 * @param refName The name of the reference
	 * @param start The start coordinate
	 * @param end The end coordinate
	 * @param orientation The strandedness of the SingleInterval
	 * @param featureName The name of this feature
	 */
	public SingleInterval(String refName, int start, int end, Strand orientation, String featureName){
		this.referenceName = refName;
		this.startPos = start;
		this.endPos = end;
		this.orientation = orientation;
		this.featureName = featureName;
	}
	
	/**
	 * Constructor. Start and end coordinates are zero-based, left-closed and right-open. The feature name
	 * is simply the empty string.
	 * @param refName The name of the reference
	 * @param start The start coordinate
	 * @param end The end coordinate
	 * @param orientation The strandedness of the SingleInterval
	 */
	public SingleInterval(String refName, int start, int end, Strand orientation) {
		this(refName, start, end, orientation, "");
	}

	/**
	 * Constructor. Start and end coordinates are zero-based, left-closed and right-open. The feature name
	 * is simply the empty string. The orientation is set to Strand.UNKNOWN
	 * @param refName The name of the reference
	 * @param start The start coordinate
	 * @param end The end coordinate
	 */
	public SingleInterval(String refName, int start, int end) {
		this(refName, start, end, Strand.UNKNOWN, "");
	}

	@Override
	public String getName() {
		return this.featureName;
	}

	@Override
	public String getReferenceName() {
		return this.referenceName;
	}

	@Override
	public int getReferenceStartPosition() {
		return this.startPos;
	}

	@Override
	public int getReferenceEndPosition() {
		return this.endPos;
	}

	@Override
	/**
	 * Returns an Iterator over the contained blocks. (Note: This is a SingleInterval, so
	 * it only has one block. This method is to maintain/unify the interface of other methods.)
	 * @return An Iterator over the one contained block
	 */
	public Iterator<SingleInterval> getBlocks() {
		Collection<SingleInterval> rtrn = new ArrayList<SingleInterval>();
		rtrn.add(this);
		return rtrn.iterator();
	}
	
	@Override
	/**
	 * The size of this interval, measured by simply subtracting the start position from the end position.
	 * @return The size of this interval
	 */
	public int size() {
		return endPos - startPos;
	}

	@Override
	public Strand getOrientation() {
		return this.orientation;
	}

	@Override
	public int getNumberOfBlocks() {
		return 1;
	}

	@Override
	//FIXME This should be merged with BlockedAnnotation
	public int getRelativePositionFrom5PrimeOfFeature(int referenceStart) {
		if(referenceStart>=this.getReferenceEndPosition() || referenceStart<this.getReferenceStartPosition()){return -1;} //This start position is past the feature
		int relative=referenceStart-getReferenceStartPosition();
		if(getOrientation().equals(Strand.NEGATIVE)){
			relative=size()-relative;
		}
		return relative;
	}

	@Override
	public void setOrientation(Strand orientation) {
		this.orientation = orientation;
	}

	/**
	 * Trims this block to the relative start and end position provided. Preserves the reference name
	 * and the orientation. SingleIntervals with an orientation of 'unknown', 'both', or 'invalid' are
	 * trimmed as though they have a positive orientation.
	 * @param relativeStartPosition The new start position, relative to the old
	 * @param relativeEndPosition The new end position, relative to the old
	 * @return A new SingleInterval with the ends appropriately trimmed
	 */
	public SingleInterval trim(int relativeStart, int relativeEnd) {
		if (getOrientation().equals(Strand.NEGATIVE)) {
			int newEnd = getReferenceEndPosition() - relativeStart;
			int newStart = getReferenceEndPosition() - relativeEnd;
			return new SingleInterval(getReferenceName(), newStart, newEnd, getOrientation());
		} else {
			int newEnd = getReferenceStartPosition() + relativeEnd;
			int newStart = getReferenceStartPosition() + relativeStart;
			return new SingleInterval(getReferenceName(), newStart, newEnd, getOrientation());
		}
	}
	
	/**
	 * Determines if this SingleInterval overlaps another. The consensus strand must not be invalid, and the
	 * reference names must match; otherwise this method will return false. No explicit null check.
	 * @param other The other SingleInterval
	 * @return Whether or not this SingleInterval overlaps with another
	 */
	public boolean overlaps(SingleInterval other) {

		int newStart = Math.max(getReferenceStartPosition(), other.getReferenceStartPosition());
		int newEnd = Math.min(getReferenceEndPosition(), other.getReferenceEndPosition());
		Strand consensusStrand = Annotation.Strand.consensusStrand(getOrientation(), other.getOrientation());

		if (newStart < newEnd &&
		    getReferenceName().equalsIgnoreCase(other.getReferenceName()) &&
		    !consensusStrand.equals(Annotation.Strand.INVALID)) {

			return true;
		}
		return false;
	}
	
	/**
	 * Determines if this SingleInterval contains another Annotation. The consensus strand must not be invalid,
	 * and the reference names must match; otherwise this method will return false. No explicit null check.
	 * @param other The other Annotation
	 * @return Whether or not this SingleInterval fully contains the other Annotation
	 */
	public boolean contains(Annotation other) {
		return // Names match
			   getReferenceName().equalsIgnoreCase(other.getReferenceName()) &&
			   // Consensus strand isn't invalid
			   !Annotation.Strand.consensusStrand(getOrientation(), other.getOrientation()).equals(Strand.INVALID) &&
			   // Check 5' bound
			   getReferenceStartPosition() <= other.getReferenceStartPosition() &&
			   // Check 3' bound
			   getReferenceEndPosition() >= other.getReferenceEndPosition();
	}
	
	/**
	 * Returns a SingleInterval representing the merging of this SingleInterval with another. Returns null if the
	 * SingleIntervals do not overlap. See the {@link #overlaps(SingleInterval) overlaps} method for conditions
	 * where SingleIntervals do not overlap.
	 */
	public SingleInterval merge(SingleInterval other) {
		if (!overlaps(other)) {
			return null;
		}
		int newStart = Math.min(getReferenceStartPosition(), other.getReferenceStartPosition());
		int newEnd = Math.max(getReferenceEndPosition(), other.getReferenceEndPosition());
		Strand consensus = Annotation.Strand.consensusStrand(getOrientation(), other.getOrientation());
		return new SingleInterval(getReferenceName(), newStart, newEnd, consensus);
	}
/*
    //9/25/14 @cburghard methods overwritten in AbstractAnnotation
	public int hashCode() {
		String s = referenceName + "_" + featureName + "_" + startPos + "_" + endPos + "_" + orientation.toString();
		return s.hashCode();
	}
	
	public boolean equals(Object o) {
		if(!o.getClass().equals(getClass())) return false;
		SingleInterval other = (SingleInterval) o;
		if(startPos != other.startPos) return false;
		if(endPos != other.endPos) return false;
		if(!orientation.equals(other.orientation)) return false;
		if(!referenceName.equals(other.referenceName)) return false;
		if(!featureName.equals(other.featureName)) return false;
		return true;
	}
*/
	@Override
	//FIXME
	/**
	 * This method doesn't work. Do not use.
	 */
	public AnnotationCollection<DerivedAnnotation<? extends Annotation>> getWindows(
			int windowSize, int stepSize) {
		throw new UnsupportedOperationException();
	}
}
