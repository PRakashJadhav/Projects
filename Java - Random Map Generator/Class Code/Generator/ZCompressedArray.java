package Generator;

/*  CLASS ZCompressedArray
 *  @Zanice
 * 
 * 	This is my own customized array object that is designed
 * 	to offer the same benefits of an array object (like 
 * 	constant-time index lookup), while automatically adjusting
 * 	the elements of the array to fill holes made by removing
 * 	elements.
 */

public class ZCompressedArray<T> {
	//Array variables.
	private T[] array;
	private int size;

	public ZCompressedArray(int startsize) {
		//Initialize the variables.
		array = (T[]) new Object[startsize];
		size = 0;
	}

    //Returns the object at the specified index of the array.
    public T get(int i) {
    	//If the index is in bounds, return the object at the index. Otherwise, return null.
        if ((i >= 0)&&(i < size))
            return array[i];
        else return null;
    }

    //Returns the index of the specified object.
    public int find(T t) {
    	//Starting at index 0, search the array for the specified object.
        int i = 0;
        while (i < size) {
            if (array[i] == t)
                break;
            i++;
        }
        //If the object was not found, return -1. Otherwise, return the index.
        if (i == size)
        	return -1;
        return i;
    }

    //Returns the current size of the array.
    public int size() {
        return size;
    }

    //Returns the maximum capacity that the array currently has.
    public int capacity() {
        return array.length;
    }
	
    //Adds an object to the end of the array.
	public void add(T t) {
		//If the array is at maximum capacity, resize it.
		if (array.length == size) {
			resize(size + 1);
		}
		
		//Add the object to the end of the array and increase the size.
		array[size] = t;
		size++;
	}
	
	//Adds an object at the specified index of the array.
	public void add(int i, T t) {
		//If the array is at maximum capacity, resize it.
		if (array.length == size) {
			resize(size + 1);
		}
		
		//Adjust the index to either the value of 'size' or 0 if the index is out of bounds.
        if (i > size)
            i = size;
        else if (i < 0)
        	i = 0;
        
        //Sift up the elements above the index.
		for (int j = array.length - 1; j > i; j--) {
			array[j] = array[j - 1];
		}
		
		//Place the object at the index and increase the size.
		array[i] = t;
		size++;
	}
	
    //Removes the specified object by first finding its element.
	public boolean remove(T t) {
        return remove(find(t));
	}

	//Removes the object at the specified element.
    public boolean remove(int i) {
    	//If the index is in bounds, remove the element at the index. Otherwise, return false.
        if ((i >= 0)&&(i < size)) {
            array[i] = null;
            
            //Sift the elements above the index down to fill the gap.
            while (i < size - 1) {
            	if (i == size - 1) {
                	break;
                }
            	else {
	            	array[i] = array[i + 1];
	                i++;
            	}
            }
            
            //Decrement the size and return true.
            size--;
            return true;
        }
        else return false;
    }

    //Increases the capacity of the array by recreating it.
	private void resize(int i) {
		//Save the array to a pointer, reinitialize 'array', and copy back to 'array' from the pointer.
		T[] temp = array;
		array = (T[]) new Object[i];
		int j = 0;
		while ((j < i)&&(j < temp.length)) {
			array[j] = temp[j];
			j++;
		}
		
		//Adjust the size, if the new array is smaller than the old array.
		if (size >= array.length)
			size = array.length - 1;
	}
}
