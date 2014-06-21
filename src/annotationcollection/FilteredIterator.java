package annotationcollection;

import java.util.Collection;
import java.util.Iterator;

import net.sf.samtools.util.CloseableIterator;

import org.apache.commons.collections15.Predicate;

import annotation.Annotation;

public class FilteredIterator<T extends Annotation> implements CloseableIterator<T>{

		CloseableIterator<T> iter;
		Collection<Predicate<T>> filters;
		T next;
		boolean started;
		
		public FilteredIterator(CloseableIterator<T> iter, Collection<Predicate<T>> filters){
			this.iter=iter;
			this.filters=filters;
		}
		
		public FilteredIterator(Iterator<T> iter, Collection<Predicate<T>> filters){
			this.iter=new CloseableWrapper(iter);
			this.filters=filters;
		}
		
		@Override
		public boolean hasNext() {
			if(!started){next=findNext(); started=true;}
			if(next!=null){return true;}
			return false;
		}

		@Override
		public T next() {
			T rtrn=next;
			next=findNext();
			return rtrn;
		}

		private T findNext() {
			boolean passesFilters=false;
			T fragment=null;
			while(!passesFilters && iter.hasNext()){
				boolean passesAll=true;
				T record=iter.next();
				for(Predicate<T> filter: filters){
					if(!filter.evaluate(record)){passesAll=false; break;}
				}
				if(passesAll){fragment=record; passesFilters=true;}
			}
			return fragment;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			iter.close();
		}
	
	public class CloseableWrapper implements CloseableIterator<T>{
		Iterator<T> iter;
		
		public CloseableWrapper(Iterator<T> iter){
			this.iter=iter;
		}
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return iter.next();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}}
		
		
}
