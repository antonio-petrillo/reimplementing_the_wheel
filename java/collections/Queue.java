public class Queue<T> {
    private static final int DEFAULT_SIZE = 8;

    private Object[] array = new Object[DEFAULT_SIZE];
    private int indexPop = 0;
    private int indexPush = 0;
    private int len = 0;

    public Queue(){}

    public Queue(T el){
        enqueue(el);
    }

    public static void main(String[] args) {
        Queue<Integer> q = new Queue<>();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);

        System.out.println("Try 'peek' => " + q.peek());
        while(!q.isEmpty()) {
            System.out.println("Dequed => " + q.dequeue());
        }

        System.out.println("is empty => " + q.isEmpty());
        for(int i = 0; i < 14; i++) {
            q.enqueue(i);
        }
        while(!q.isEmpty()) {
            System.out.println("Dequed => " + q.dequeue());
        }
        System.out.println("is empty => " + q.isEmpty());

    }

    @SuppressWarnings("unchecked")
    private void growth() {
        var old = array;
        array = new Object[array.length << 1];
        indexPush = 0;
        for(int i = 0; i < old.length; i++) {
            int access = Math.floorMod(indexPop + i, old.length);
            if (old[access] != null) {
                array[indexPush++] = old[access];
            }
        }
        indexPop = 0;
    }

    @SuppressWarnings("unchecked")
    private void shrink() {
        var old = array;
        array = new Object[array.length >> 1];
        indexPush = 0;
        for(int i = 0; i < old.length; i++) {
            int access = Math.floorMod(indexPop + i, old.length);
            if (old[access] != null) {
                array[indexPush++] = old[access];
            }
        }
        indexPop = 0;
    }

    public boolean isEmpty() {
       return len == 0;
    }

    public void enqueue(T elem){
        if (len == array.length) {
            growth();
        }
        len++;
        array[indexPush] = elem;
        indexPush = Math.floorMod(indexPush + 1, array.length);
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) array[indexPop];
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        T el = (T) array[indexPop];
        array[indexPop] = null;
        indexPop = Math.floorMod(indexPop + 1, array.length);
        len--;
        if (len < array.length / 3 && array.length > DEFAULT_SIZE) {
            shrink();
        }
        return el;
    }


}
