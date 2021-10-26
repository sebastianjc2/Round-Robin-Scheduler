// Sebastian Caballero sebastian.caballero@upr.edu

package prj_01;
import java.util.concurrent.ThreadLocalRandom;


class Node {
    public int id;
    public Node next;
    public Node previous;
    public Boolean proccessed_flag;

    public Node (int id) {
        this.id = id;
        proccessed_flag = true;
    }
}

interface RoundRobinCLLInterface {
    abstract void findEmptySlot();
    abstract void findFilledSlot();
}

public class RoundRobinCLL implements RoundRobinCLLInterface {
    private int num_nodes = 5;
    public Node head = null;
    public Node tail = null;
    public Boolean stopLoop = false;
    private int termination_limit;

    private void holdon() {
        try{
            Thread.currentThread().sleep(ThreadLocalRandom.current().nextInt(500, 3000));
        }
        catch(Exception e){
            System.out.println("Something went wrong.");
        }
    }

    @Override
    public String toString () {
        String s = new String(""+ Thread.currentThread().getName() + " ");
        Node node = head;
        s+= "(Node-1: " + node.proccessed_flag + ")";
        s+= " ==> ";

        for (int i=1; i<num_nodes; i++) {
            node = node.next;
            s+= "(Node-"+(i+1)+": "+node.proccessed_flag + ")";
            if (i<num_nodes-1)
                s+= " ==> ";
        }
        return s;
    }

    private synchronized void holdRR(Node node, Boolean set_slot) {
        System.out.println("Thread " + Thread.currentThread().getName() + " Holding Resources");
        node.proccessed_flag = set_slot ;
        System.out.println("Thread " + Thread.currentThread().getName() + " Releasing Resources");
        if (set_slot) holdon();
    }

    public void findEmptySlot() {
        holdon();
        /* PUT YOUR CODE HERE TO FIND AN EMPTY SLOT */
        /* STARTING FROM THE FIRST NODE IN THE LINKED LIST */
        /*** IMPORTANT:: USE THE holdRR() METHODE TO ACCESS THE LINKED LIST ***/
        /*** TO AVOID RACE CONDITION ***/
        Node finder = head; // created node to help check and find the first empty slot.
        // Since it's a circular linked list, whenever finder.next is the head of the linkedlist,
        // it means we have looped through the CLL
        while(finder.next != head){
            if(finder.proccessed_flag) {
                // if proccessed_flag is true, call holdRR to access the linkedList, setting
                // it to unprocessed
                holdRR(finder, false);
                break;
            }
            finder = finder.next; // assign finder.next to finder
        }
    }

    public void findFilledSlot() {
        /* PUT YOUR CODE HERE TO FIND THE FILLED SLOTS */
        /* FOR THE MAIN PROCESS                        */
        /*** IMPORTANT:: USE THE holdRR() METHODE TO ACCESS THE LINKED LIST ***/
        int count = 0 ;
        Node current = head;
        while (!stopLoop) {
            /* PUT YOUR CODE HERE TO FIND THE FILLED SLOTS */
            holdon();
            if (count > termination_limit) break;
            // if proccessed_flag is true, call holdRR to access the linkedList, setting
            // it to processed
            if(current.proccessed_flag == false){
                holdRR(current, true);
            }
            current = current.next; // assigning current to its next node so we dont get an infinite while loop
            System.out.println("Main Move No.: " + count%num_nodes + "\t" + toString());
            count++; // up the count
        }
    }

    private void fillRoundRubin () {
        /* PUT YOUR CODE HERE INITIATE THE CIRCULAR LINKED LIST */
        /* WITH DESIRED NUMBER OF NODES BASED TO THE PROGRAM   */
        head = new Node(0); // creating a new Node with id 0
        Node nextNode = head; // temporary node to use for to initiate the CLL
        for(int i = 1; i < num_nodes; i++){
            Node filler = new Node(i); // create a new node that will help fill the empty slots

            // now we do basic operations for a CLL
            nextNode.next = filler;
            filler.previous = nextNode;
            nextNode = nextNode.next;
        }
        tail = nextNode;
        head.previous = tail;
        tail.next = head;
    }

    public RoundRobinCLL(int num_nodes, int termination_limit) {
        this.num_nodes = num_nodes;
        this.termination_limit = termination_limit;
        fillRoundRubin();
    }
    public RoundRobinCLL(int num_nodes) {
        this.num_nodes = num_nodes;
        fillRoundRubin();
    }

    public RoundRobinCLL() {
        fillRoundRubin();
    }

}
