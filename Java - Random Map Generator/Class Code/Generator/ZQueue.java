package Generator;

/*  CLASS ZQueue
 *  @Zanice
 * 
 * 	This is my own customized queue object using a linked list.
 * 	The queue allows for access to both the front and back
 * 	elements. Only what is needed for the random map generator
 * 	is currently written.
 */

public class ZQueue<T> {
	//Linked list variables.
    Node<T> front;
    Node<T> back;
    int size;

    public ZQueue() {
    	//Initialize the variables.
        front = null;
        back = null;
        size = 0;
    }

    //Returns the size of the queue.
    public int size() {
        return size;
    }

    //Adds a node to the back of the queue.
    public void add(T t) {
    	//Create a new node that holds the object.
    	Node<T> node = new Node(t);
    	
    	//Special Case: If the queue is empty, the node becomes both the front and the back.
        if (front == null) {
            front = node;
            back = node;
        }
        //Otherwise, set the back node's next pointer to the new node and set the new node as the back.
        else {
            back.next = node;
            back = node;
        }
        
        //Increment the size.
        size++;
    }

    //Returns the element of the node at the front of the queue.
    public T peek() {
    	//If the front is not null, return the front node's element. Otherwise, return null.
    	if (front != null)
    		return front.element;
    	return null;
    }

    //Pull the element of the node at the front of the queue and step the queue forward.
    public T next() {
    	//If the queue has at least one element, pull from the front. Otherwise return null.
    	if (size != 0) {
	        Node n;
	        
	        //Special Case: If the queue contains only one node, set the front and back to null
	        //while still returning the element of the front node.
	        if (front == back) {
	            n = front;
	            front = null;
	            back = null;
	            size--;
	            return (T) n.element;
	        }
	        //Otherwise, adjust the front while still returning the element of the original front node.
	        else {
	            n = front;
	            front = front.next;
	            size--;
	            return (T) n.element;
	        }
    	}
    	return null;
    }

    /*	SUBCLASS Node
	 * 	@Zanice
	 * 
	 * 	This is the underlying object for the queue. Node objects store
	 * 	the element they hold along with a pointer to the next Node in the
	 * 	queue.
	 */
    
    public class Node<T> {
    	//Linked list variables.
        public T element;
        public Node<T> next;

        public Node(T elem) {
        	//Initialize 'element'.
            element = elem;
        }

        //Returns the object that the node is carrying.
        public T getElement() {
            return element;
        }

        //Returns the Node that is positioned after this node in the queue.
        public Node<T> getNext() {
            return next;
        }
    }
}
