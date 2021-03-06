import java.util.HashMap;

public class LFUCache {

    private NodeQueue tail;
    private int capacity;
    private HashMap<Integer, Node> map;

    public LFUCache(int capacity){
        this.capacity = capacity;
        map = new HashMap<Integer, Node>(capacity);
    }

    private void oneStepUp(Node n){
        n.frequency++;//访问次数+1
        boolean singleNodeQ = false;
        if(n.nq.head == n.nq.tail){
            singleNodeQ = true;
        }
        if(n.nq.next != null){
            if(n.nq.next.tail.frequency == n.frequency){
                removeNode(n);
                n.prev = n.nq.next.head;
                n.nq.next.head.next = n;
                n.nq.next.head = n;
                n.nq = n.nq.next;
            }else if(n.nq.next.tail.frequency > n.frequency){
                if(!singleNodeQ){
                    removeNode(n);
                    NodeQueue nnq = new NodeQueue(n.nq.next, n.nq, n, n);
                    n.nq.next.prev = nnq;
                    n.nq.next = nnq;
                    n.nq = nnq;
                }
            }
        }else {
            if(!singleNodeQ){
                removeNode(n);
                NodeQueue nnq = new NodeQueue(null, n.nq, n, n);
                n.nq.next = nnq;
                n.nq = nnq;
            }
        }

    }

    private Node removeNode(Node n){
        if(n.nq.head == n.nq.tail){
            removeNQ(n.nq);
            return n;
        }
        if(n.prev != null){
            n.prev.next = n.next;
        }
        if(n.next != null){
            n.next.prev = n.prev;
        }
        if(n.nq.head == n){
            n.nq.head = n.prev;
        }
        if(n.nq.tail == n){
            n.nq.tail = n.next;
        }
        n.prev = null;
        n.next = null;
        return n;
    }

    private void removeNQ(NodeQueue nq){
        if(nq.prev != null){
            nq.prev.next = nq.next;
        }
        if(nq.next != null){
            nq.next.prev = nq.prev;
        }
        if(this.tail == nq){
            this.tail = nq.next;
        }
    }

    public int get(int key){
        Node n = map.get(key);
        if(n == null)
            return -1;
        oneStepUp(n);
        return n.value;
    }

    public void put(int key, int value){
        if(capacity == 0){
            return;
        }
        Node cn = map.get(key);
        if(cn != null){
            cn.value = value;
            oneStepUp(cn);
            return;
        }
        if(map.size() == capacity){
            map.remove(removeNode(this.tail.tail).key);
        }

        Node n = new Node(key, value);
        if(this.tail == null){
            NodeQueue nq = new NodeQueue(null, null, n, n);
            this.tail = nq;
            n.nq = nq;
        }else if(this.tail.tail.frequency == 0){
            n.prev = this.tail.head;
            this.tail.head.next = n;
            n.nq = this.tail;
            this.tail.head = n;
        }else{
            NodeQueue nq = new NodeQueue(this.tail, null, n, n);
            this.tail.prev = nq;
            this.tail = nq;
            n.nq = nq;
        }
        map.put(key, n);

    }


    class Node{
        int key;
        int value;
        int frequency = 0;
        Node next;
        Node prev;
        NodeQueue nq;

        Node(int key, int value){
            this.key = key;
            this.value = value;
        }
    }

    class NodeQueue{
        Node tail;
        Node head;
        NodeQueue next;
        NodeQueue prev;

        NodeQueue(NodeQueue next, NodeQueue prev, Node tail, Node head){
            this.prev = prev;
            this.next = next;
            this.tail = tail;
            this.head = head;
        }
    }

    public static void main(String[] args){
        LFUCache cache = new LFUCache(3);
        cache.put(2,5);
        cache.put(3,5);
        cache.put(4,9);
        cache.put(5,6);
        System.out.println(cache.get(2));
    }
}
