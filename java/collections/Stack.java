public class Stack<T> {
    private static final int DEFAULT_SIZE = 16;

    private Object[] array = new Object[DEFAULT_SIZE];
    private int index = 0, cap = DEFAULT_SIZE;

    public Stack(T el) {
        push(el);
    }

    public Stack() {}

    @SuppressWarnings("unchecked")
    private void growth() {
        var old = array;
        array = new Object[array.length << 1];
        for(int i = 0; i < index; i++) {
            array[i] = old[i];
        }
    }

    @SuppressWarnings("unchecked")
    private void shrink() {
        var old = array;
        array = new Object[array.length >> 1];
        for(int i = 0; i < index; i++) {
            array[i] = old[i];
        }
    }

    public void push(T el) {
        if (cap == index) {
            growth();
        }
        array[index++] = el;
    }

    public boolean isEmpty() {
        return index == 0;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) array[index - 1];
    }

    @SuppressWarnings("unchecked")
    public T pop() {
       T el = (T) array[--index];
       array[index] = null;

       if (index < array.length / 3 && array.length > DEFAULT_SIZE) {
           shrink();
       }

       return el;
    }

    public static void main(String[] args) {
      Stack<Integer> s = new Stack<>();
      for (int i = 0; i < 10; i++) {
          s.push(i);
      }
      while(!s.isEmpty()) {
          System.out.println("Peek => " + s.peek().toString());
          s.pop();
      }
    }
}
