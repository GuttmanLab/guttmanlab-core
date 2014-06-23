package annotationcollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections15.Predicate;

import net.sf.samtools.util.CloseableIterator;
import annotation.Annotation;
import annotation.BlockedAnnotation;
import annotation.ContiguousWindow;
import annotation.SingleInterval;
import annotation.Window;
import annotation.Annotation.Strand;
import coordinatespace.CoordinateSpace;
import datastructures.IntervalTree;

public abstract class AbstractAnnotationCollection<T extends Annotation> implements AnnotationCollection<T>{

	private Collection<Predicate<T>> filters;
	
	public AbstractAnnotationCollection(){
		filters=new ArrayList<Predicate<T>>();
	}
	
	@Override
	public void addFilter(Predicate<T> filter) {
		filters.add(filter);
	}
	
	@Override
	public Collection<Predicate<T>> getFilters(){
		return filters;
	}
	
	@Override
	public AnnotationCollection<T> merge() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public boolean overlaps(Annotation other) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}
	
	@Override
	public CloseableIterator<BlockedAnnotation> convertCoordinates(CloseableIterator<? extends Annotation> annotations, CoordinateSpace referenceSpaceForAnnotations){
		//TODO Should check the coordinate space and convert appropriately
		return convertFromReferenceSpace(annotations);
	}
	
	private CloseableIterator<BlockedAnnotation> convertFromReferenceSpace(CloseableIterator<? extends Annotation> iterator){
		return new CoordinateConverterIterator(iterator, this);
	}
	
	public CloseableIterator<? extends Window<T>> getWindows(Annotation region, int windowLength){
		CloseableIterator<T> iter=iterator(region, false);
		return new WindowIterator<T>(iter, windowLength);
	}

	public class WindowIterator<T extends Annotation> implements CloseableIterator<Window<T>>{

		IntervalTree<Window<T>> windows;
		CloseableIterator<T> iter;
		Iterator<Window<T>> fullyFormedWindows;
		int windowLength;
		boolean hasNext;
		
		public WindowIterator(CloseableIterator<T> iter, int windowLength){
			this.iter=iter;
			this.windowLength=windowLength;
			this.windows=new IntervalTree<Window<T>>();
			this.hasNext=false;
		}
		
		@Override
		public boolean hasNext() {
			if(fullyFormedWindows!=null && fullyFormedWindows.hasNext()){return true;}
			else if(iter.hasNext()){updateWindows(); return hasNext();}
			return false;
		}

		@Override
		public Window<T> next() {
			return fullyFormedWindows.next();
		}
		
		private void updateWindows(){
			T read=iter.next();
			//all windows with an end position before the start of this window
			fullyFormedWindows=removeFullyFormedWindows(read).iterator();
			addReadToWindows(read);
		}
		
		private void addReadToWindows(T read){
			//Make all windows overlapping read blocks
			Iterator<SingleInterval> interval=read.getBlocks();
			while(interval.hasNext()){
				SingleInterval block=interval.next();
				int start=Math.max(0, block.getReferenceStartPosition()-windowLength);
				for(int i=start; i<block.getReferenceEndPosition(); i++){
					Window<T> window=windows.remove(i, i+windowLength);
					if(window==null){
						//make a window
						window=new ContiguousWindow<T>(read.getReferenceName(), i, i+windowLength, Strand.BOTH);
					}
					window.addAnnotation(read);
					windows.put(window.getReferenceStartPosition(), window.getReferenceEndPosition(), window);
				}
			}
		}

		private Collection<Window<T>> removeFullyFormedWindows(T read) {
			Iterator<Window<T>> iter=windows.getNodesBeforeInterval(read.getReferenceStartPosition(), read.getReferenceStartPosition());
			Collection<Window<T>> rtrn=new ArrayList<Window<T>>();
			while(iter.hasNext()){
				Window<T> w=iter.next();
				if((!w.getReferenceName().equalsIgnoreCase(read.getReferenceName())) || (w.getReferenceEndPosition()<read.getReferenceStartPosition())){
					rtrn.add(w);
					windows.remove(w.getReferenceStartPosition(), w.getReferenceEndPosition());
				}
			}
			this.hasNext=!rtrn.isEmpty();
			return rtrn;
		}

		@Override
		public void remove() {
			this.iter.remove();
		}

		@Override
		public void close() {
			this.iter.close();
		}
		
		
	}
	
	
	public class CoordinateConverterIterator implements CloseableIterator<BlockedAnnotation>{
		
		CloseableIterator<? extends Annotation> iter;
		Iterator<BlockedAnnotation> next;
		boolean started;
		AnnotationCollection<? extends Annotation> mapping;
		
		
		public CoordinateConverterIterator(CloseableIterator<? extends Annotation> iterator, AnnotationCollection<? extends Annotation> mapping){
			this.iter=iterator;
			this.started=false;
			this.mapping=mapping;
		}
		
		@Override
		public boolean hasNext() {
			if((!started || !next.hasNext()) && iter.hasNext()){
				started=true;
				findNext();
				return hasNext();
			}
			if(next.hasNext()){
				return true;
			}
			
			return false;
		}

		private void findNext() {
			Annotation annotation=iter.next();
			this.next=convert(annotation).iterator();
		}

		private Collection<BlockedAnnotation> convert(Annotation annotation) {
			Collection<BlockedAnnotation> rtrn=new ArrayList<BlockedAnnotation>();
			
			//Find features overlapping the annotation
			CloseableIterator<? extends Annotation> iter=mapping.iterator(annotation, false);
			
			//Adjust the coordinates of the feature as needed in featureSpace (ie as distance from start and end)
			while(iter.hasNext()){
				Annotation feature=iter.next();
				BlockedAnnotation intersect=feature.intersect(annotation); 
				if(intersect.size()>0){
					BlockedAnnotation interval=feature.convertToFeatureSpace(new SingleInterval(intersect.getReferenceName(), intersect.getReferenceStartPosition(), intersect.getReferenceEndPosition()));
					rtrn.add(interval);
				}
			}
			return rtrn;
		}

		@Override
		public BlockedAnnotation next() {
			return next.next();
		}

		@Override
		public void remove() {
			this.iter.remove();
		}

		@Override
		public void close() {
			this.iter.close();
			
		}}
	
}
