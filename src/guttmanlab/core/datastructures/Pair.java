package guttmanlab.core.datastructures;

public class Pair<T>{

	private T value1;
	private T value2;
	
	/**
	 * An empty constructor
	 */
	public Pair(){}
	
	public Pair(T v1, T v2) {
		value1 = v1;
		value2 = v2;
	}
	
	public void setValue1(T v1) {
		value1 = v1;
	}
	
	public void setValue2(T v2){
		value2 = v2;
	}
	
	public T getValue1() {
		return value1;
	}
	
	public T getValue2() {
		return value2;
	}

	public boolean hasValue1(){
		return value1 != null;
	}
		
	public boolean hasValue2() {
		return value2 != null;
	}
	
	public boolean isEmpty() {
		return value1 == null && value2 == null;
	}
	
	public boolean isComplete() {
		return value1 != null && value2 != null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Since null values within the Pair may be meaningful, two Pairs will be considered
	 * equal if the first elements are both null and/or the second elements are both null.
	 * Note that if the Pair itself is null (as opposed to one of its elements), this method
	 * will return false.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (!other.getClass().equals(Pair.class)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Pair<T> o = (Pair<T>)other;  // OK to cast this class;
		
		boolean cond1 = compareValues(this.value1, o.value1);
		boolean cond2 = compareValues(this.value2, o.value2);
		return cond1 && cond2;
	}

	/**
	 * Simple helper method for Pair<T>.equals(). Compares two Ts,
	 * taking into account that null == null.
	 * @param a T 1
	 * @param b T 2
	 * @return If the two Ts are equal.
	 */
	private boolean compareValues(T a, T b) {
		if (a == null || b == null) {
			return a == null && b == null;
		}
			return a.equals(b);
	}
	
	public int hashCode() {
		if(value1==null){return value2.hashCode();}
		if(value2==null){return value1.hashCode();}
		String h = Integer.valueOf(value1.hashCode()).toString() + "_" + Integer.valueOf(value2.hashCode()).toString();
		return h.hashCode();
	}
}
