
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ConcurrentSearcherList<T> {

	/*
	 * Three kinds of threads share access to a singly-linked list:
	 * searchers, inserters and deleters. Searchers merely examine the list;
	 * hence they can execute concurrently with each other. Inserters add
	 * new items to the front of the list; insertions must be mutually exclusive
	 * to preclude two inserters from inserting new items at about
	 * the same time. However, one insert can proceed in parallel with
	 * any number of searches. Finally, deleters remove items from anywhere
	 * in the list. At most one deleter process can access the list at
	 * a time, and deletion must also be mutually exclusive with searches
	 * and insertions.
	 * 
	 * Make sure that there are no data races between concurrent inserters and searchers!
	 */
    
      
     	private static class Node<T>{
		final T item;
		Node<T> next;
		
		Node(T item, Node<T> next){
			this.item = item;
			this.next = next;
		}
	}
	
        private int nr=0;       //No of threads removing
        private int ni=0;	//No of threads inserting
        private int ns=0;	//No of threads searching
        
        private final ReentrantLock lock;
        private final Condition removeCond;
        private final Condition insertCond;
        private final Condition searchCond;
        
	private volatile Node<T> first; 
	

	
	public ConcurrentSearcherList() {
            first = null;
            lock= new ReentrantLock();
            removeCond= lock.newCondition();
            insertCond= lock.newCondition();
            searchCond=lock.newCondition();
	}
	
	/**
	 * Inserts the given item into the list.  
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @throws InterruptedException
	 */
	public void insert(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to insert null";
		start_insert();
		try{
			first = new Node<T>(item, first);
		}
		finally{
			end_insert();
		}
	}
	
	/**
	 * Determines whether or not the given item is in the list
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @return  true if item is in the list, false otherwise.
	 * @throws InterruptedException
	 */
	public boolean search(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to search for null";
		start_search();
		try{
			for(Node<T> curr = first;  curr != null ; curr = curr.next){
				if (item.equals(curr.item)) return true;
			}
			return false;
		}
		finally{
			end_search();
		}
	}
	
	/**
	 * Removes the given item from the list if it exists.  Otherwise the list is not modified.
	 * The return value indicates whether or not the item was removed.
	 * 
	 * Precondition:  item != null.
	 * 
	 * @param item
	 * @return  whether or not item was removed from the list.
	 * @throws InterruptedException
	 */
	public boolean remove(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to remove null";
		start_remove();
		try{
			if(first == null) return false;
			if (item.equals(first.item)){first = first.next; return true;}
			for(Node<T> curr = first;  curr.next != null ; curr = curr.next){
				if (item.equals(curr.next.item)) {
					curr.next = curr.next.next;
					return true;
				}
			}
			return false;			
		}
		finally{
			end_remove();
		}
	}
	
	private void start_insert() throws InterruptedException{	
            // <await (ni==0 && nr==0) ni++>
           
            lock.lock();
            try{
                while(!(ni==0 &&  nr==0)){
                   insertCond.await();
                }
                ni++;
                
            }
            finally{
                lock.unlock();
                
            }
            
    }

	private void end_insert(){
            lock.lock();
            try{
                ni--;
                
                if(ni==0){
                    removeCond.signalAll();
                   insertCond.signalAll();
                    
                }
            }
            finally{
                lock.unlock();
                
            }

	}
	
	private void start_search() throws InterruptedException{
            //<await(nr==0) ns++;>
            lock.lock();
            try{
                while(!(nr==0)){
                    searchCond.await();
                }
                ns++;
                
                
            }
            finally{
                lock.unlock();
            }
            

	}
	
	private void end_search(){
            lock.lock();
            try{
                ns--;
                if(ns==0){
                removeCond.signalAll();
                  
                }
            }
            finally{
                lock.unlock();
            }

	}
	
	private void start_remove() throws InterruptedException{
            //<await (nr==0 && ns==0 && ni==0) nr++>
            lock.lock();
            try{
                while(!(nr==0 && ns==0 && ni==0)){
                    removeCond.await();
                }
                nr++;
                                   
            }
            finally{
                lock.unlock();
            }
            
	}
	
	private void end_remove() {
            lock.lock();
            try{
                nr--;
                if(nr==0){
                    insertCond.signalAll();
                    removeCond.signalAll();
                    searchCond.signalAll();
                    
                }
            }
            finally{
                lock.unlock();
            }
		
	}
                    
        
}
	