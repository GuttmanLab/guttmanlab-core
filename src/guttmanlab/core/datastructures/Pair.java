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

		// No need for null check. The instanceof operator returns false if (other == null).
		if (!(other instanceof Pair)) {
			return false;
		}

		// OK to cast this. Class was explicitly checked above
		@SuppressWarnings("unchecked")
		Pair<T> o = (Pair<T>)other;
		
		boolean cond1 = (value1 == null ? o.value1 == null : value1.equals(o.value1));
		boolean cond2 = (value2 == null ? o.value2 == null : value2.equals(o.value2));
		return cond1 && cond2;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = value1 == null ? 31 * hashCode : 31 * hashCode + value1.hashCode();
		hashCode = value2 == null ? 31 * hashCode : 31 * hashCode + value2.hashCode();
		return hashCode;
	}
	
	/**
	 *  Returns a String representation of this Pair by recursively calling toString()
	 *  on its members. The exact details of this representation are unspecified and
	 *  subject to change, but the following may be regarded as typical:
	 *  
	 *  "(value1, value2)"
	 */
	@Override
	public String toString() {
		if (isComplete()) {
			return "(" + value1.toString() + ", " + value2.toString() + ")";
		}
		if (isEmpty()) {
			return "(null, null)";
		}
		if (!hasValue1()) {
			return "(null, " + value2.toString() + ")";
		}
		// else value2 == null
		return "(" + value1.toString() + ", null)";
	}
}
