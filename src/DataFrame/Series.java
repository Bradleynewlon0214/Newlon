package DataFrame;

import java.util.Arrays;

public class Series<E> {

	private int size; //current number of elements in the list
	E[] list;
	private int INITIAL_CAPACITY = 8;
	
	/**
	 * Initializes the generic array with the size stored in the field 'INITIAL_CAPACITY' and initilizes the size field to 0
	 */
	public Series(){
		list = (E[]) new Object[INITIAL_CAPACITY];
		size = 0;
	}
	
	public Series(E[] items) {
		list = (E[]) new Object[INITIAL_CAPACITY];
		size = 0;
		addAll(items);
	}
	
	/**
	 * void add(Object e) - accepts an element to be added to the end of the list
	 * but if adding the element to the end of the array is not possible because the array is too small,
	 * the array will be resized
	 * if the element will fill up the array, the array will also be resized.
	 * Resizing is done by creating a new array with double the size and copy all the values into the new array  
	 * the size field is also incremented by one when the element is added
	 * @param Object e - generic type element that is going to be added to the list
	 * 
	 */
	public void add(Object e) {
		if(size == list.length) {
			
			E[] copy = (E[]) new Object[list.length * 2];
			for (int i = 0; i < list.length; i++) {
				copy[i] = list[i];
			}
			list = copy;
			list[size] = (E) e;
			size++;
		} else {
			list[size] = (E) e;
			size++;
		}
	}
	
	public void addAll(E[] elements) {
		int newSize = size + elements.length;

		E[] copy = (E[]) new Object[newSize * 2];
		for (int i = 0; i < size; i++) {
			copy[i] = list[i];
		}
		
		for(int i = size, j = 0; i < newSize; i++, j++) {
			copy[i] = elements[j];
		}
		
		list = copy;
		size = newSize++;
	}

	/**
	 * void clear() - clears the list by setting the underlying array equal to a new array with size 1,
	 * and resets the size field to 0;
	 */
	public void clear() {
		list = (E[]) new Object[1];
		size = 0;
	}

	/**
	 * boolean contains(Object e) - performs a linear search on the list and if the given element is in the list, returns true
	 * @param Object e - the element to be searched for
	 */
	public boolean contains(Object e) {
		for(int i = 0; i < size; i++) {
			if(list[i] == (E) e)
				return true;
		}
		return false;
	}

	/**
	 * Object get(int index) - returns the item located at the given index else throws illegalArgumentException
	 * @param int index - index to get item from
	 */
	public Object get(int index) {
		if(index >= size || index < 0)
			throw new IllegalArgumentException();
		return list[index];
	}

	/**
	 * int indexOf(Object e) - accepts an element and performs a linear search on the list to check if the element is in the list
	 * if the element is in the list, it's index is returned, else -1 is returned
	 */
	public int indexOf(Object e) {
		for(int i = 0; i < size; i++) {
			if(list[i] == (E) e)
				return i;
		}
		return -1;
	}


	/**
	 * void remove() - removes the last item in the list and decrements the size field
	 */
	public void remove() {
		list[size - 1] = null;
		size--;
	}

	/**
	 * void set(int index, Object element) - accepts an index and an element so that index inside of the list can be set to the given element
	 * the new element overwrites the old element
	 * if the index is less than 0 or greater than the size, throws an illegal argument exception
	 * @param int index - the location to place the given element
	 * @param Object e - the element to be placed into the list
	 */
	public void set(int index, Object element) {
		if(index < 0 || index > size) {
			throw new IllegalArgumentException();
		}
		list[index] = (E) element;
		
	}

	/**
	 * int size() - returns the size of the list 
	 */
	public int size() {
		return size;
	}
	
	
	public double sum() {
		double sum = 0;
		for(int i = 0; i < size; i++) {
			sum += (double)list[i];
		}
		return sum;
	}
	
	public double mean() {
		return sum() / size;
	}
	
	public double variance() {
		double variance = 0;
		double xBar = mean();
		
		for(int i = 0; i < size; i++) {
			variance += Math.pow((double)list[i] - xBar, 2);
		}
		
		return variance / (size - 1);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < size - 1; i++) {
			sb.append(list[i] + ", ");
		}
		sb.append(list[size - 1]);
		sb.append("]");
		return sb.toString();
	}
	
	public double[] toArray(){
		double[] results = new double[size];
		for (int i = 0; i < size; i++) {
			results[i] = Double.parseDouble(list[i].toString());
		}
		return results;
	}
}
