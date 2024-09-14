import java.lang.ArrayIndexOutOfBoundsException;

import java.util.Iterator;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ArrayList<T> implements Iterable<T>, RandomAccess {
    private static final int DEFAULT_SIZE = 16;

    private Object[] array;
    private int cap, len;

    public ArrayList() {
        array = new Object[DEFAULT_SIZE];
        cap = DEFAULT_SIZE;
        len = 0;
    }

    private ArrayList(Object[] array, int len, int cap) {
        this.array = array;
        this.len = len;
        this.cap = cap;
    }

    public static void main(String[] args) {
      ArrayList<Integer> arr = new ArrayList<>();

      arr.add(10); arr.add(11); arr.add(12);
      for(Integer i : arr){
          System.out.println(i);
      }
      System.out.println();
      for(Integer i : arr.reverseIterable()){
          System.out.println(i);
      }
      System.out.println();
      arr.each(i -> System.out.println(i));

      System.out.println();
      arr.remove(10);
      arr.each(i -> System.out.println(i));

      ArrayList<Double> darr = arr.map(n -> n / Math.PI);
      System.out.println();
      darr.each(d -> System.out.println(d));

      ArrayList<Integer> evens = arr.filter(n -> (n & 1) == 0);
      System.out.println();
      evens.each(i -> System.out.println(i));

      System.out.println();
      var sumI = arr.reduce((el, acc) -> el + acc, Integer.valueOf(0));
      var sumD = arr.reduce((el, acc) -> el + acc, Double.valueOf(.0));
      System.out.println("Sum as integer => " + sumI + "\tSum as double => " + sumD);

      System.out.println();
      var mapReduce = arr
          .map(el -> el << 1)
          .reduce((el, acc) -> {acc.add(el); return acc;}, new ArrayList<Integer> ());
      mapReduce.each(i -> System.out.println(i));
    }

    private void growth() {
        var bigger = new Object[cap << 1];
        for(int i = 0; i < len; i++) {
            bigger[i] = array[i];
        }
        array = bigger;
        cap <<= 1;
    }

    private void shrink() {
       var smaller = new Object[cap >> 1];
        for(int i = 0; i < len; i++) {
            smaller[i] = array[i];
        }
        array = smaller;
        cap >>= 1;
    }

    public void add(T el) {
        if (cap == len) {
            growth();
        }
        array[len++] = el;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index >= len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (T) array[index];
    }

    public boolean contains(Object o) {
        for(int i = 0; i < len; i++) {
            if(array[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(Object o) {
        for(int i = 0; i < len; i++) {
            if(array[i].equals(o)) {
                for (; i < len - 1; i++) {
                    array[i] = array[i + 1];
                }
                array[len - 1] = null;
                len--;

                if(len < cap / 3 && cap > DEFAULT_SIZE) {
                    shrink();
                }

                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int curr = 0;

            @Override
            public boolean hasNext() {
                return curr < len;
            }

            @Override
            public T next() {
               return (T) array[curr++];
            }
        };
    }

    public Iterable<T> reverseIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int curr = len - 1;

                    @Override
                    public boolean hasNext() {
                        return curr >= 0;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public T next() {
                        return (T) array[curr--];
                    }
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <U> ArrayList<U> map(Function<? super T, ? extends U> f) {
        var mapped = new Object[cap];
        for(int i = 0; i < len; i++) {
            mapped[i] = f.apply((T) array[i]);
        }
        return new ArrayList<U>(mapped, len, cap);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> filter(Predicate<? super T> p) {
        var filtered = new Object[cap];
        int index = 0;
        for(int i = 0; i < len; i++) {
            if(p.test((T) array[i])) {
                filtered[index++] = array[i];
            }
        }
        ArrayList<T> out = new ArrayList<>(filtered, index, cap);
        if (index < cap / 3 && cap > DEFAULT_SIZE) {
           out.shrink();
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public <U> U reduce(BiFunction<? super T, ? super U, ? extends U> reducer, U initial) {
        for (int i = 0; i < len; i++) {
            initial = reducer.apply((T) array[i], initial);
        }
        return initial;
    }

    @SuppressWarnings("unchecked")
    public void each(Consumer<? super T> consumer) {
        for (int i = 0; i < len; i++) {
            consumer.accept((T) array[i]);
        }
    }
}
