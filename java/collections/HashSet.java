import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.Iterator;

public class HashSet<T> implements Iterable<T> {
    private static final Object TOMBSTONE = new Object();

    private static final int DEFAULT_SIZE = 16;
    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_SHRINK_THRESHOLD = 0.15;
    private static final int BIG_PRIME = 115249;

    private Object[] array;
    private double threshold = DEFAULT_THRESHOLD;
    private double shrinkThreshold = DEFAULT_SHRINK_THRESHOLD;
    private int len;

    public HashSet() {
        array = new Object[DEFAULT_SIZE];
        len = 0;
    }

    public static void main(String[] args) {
      HashSet<Integer> set = new HashSet<>();

      set.add(1);
      set.add(4);
      set.add(8);

      set.dbg();

      for (var i : set) {
          System.out.println(i);
      }
      System.out.println();
      set.each(i -> System.out.println(i));

      set.remove(1);
      set.each(i -> System.out.println(i));
      System.out.println();

      set.add(1);
      set.each(i -> System.out.println(i));

      exampleWithStrings();

      HashSet<Double> dset = set.map(i -> i / Math.PI);
      dset.each(d -> System.out.println(d));

      var sum = set.reduce((i, acc) -> i + acc, Double.valueOf(.0));
      System.out.println("the sum is -> " + sum);

    }

    private static void exampleWithStrings() {
        HashSet<String> set = new HashSet<>();
        set.add("ciao");
        set.add("miao");
        set.add("suca");
        set.add("asdfa");

        set.add("csdfafeaiao");
        set.add("miaaefaefao");
        set.add("sucrerwlekrjlwekra");
        set.add("asdwerwerwepirfa");

        set.add("cerawerawerawiao");
        set.add("miaerero");
        set.add("sucerea");
        set.add("asdfappaeprpr");

        set.add("cim,.meo");
        set.add("miam.m.m,o");
        set.add("suc][pa");
        set.add("asdfp[p[{PP{a");


        set.add("ciaafecoeimo");
        set.add("miaaefmleio");
        set.add("suclnucha");
        set.add("asdfw3nxlknea");

        set.dbg();

    }

    private static int getIndex(Object o, int i, int size) {
        return Math.floorMod(BIG_PRIME * i ^ o.hashCode(), size);
    } 

    @SuppressWarnings("unchecked")
    private void growth() {
       var old = array;
       array = new Object[array.length << 1];
       len = 0;
       for (var entry : old) {
           if (entry != null && entry != TOMBSTONE) {
               add((T) entry);
           }
       }
    }

    @SuppressWarnings("unchecked")
    private void shrink() {
        var old = array;
        array = new Object[array.length >> 1];
        len = 0;
        for (var entry : old) {
            if (entry != null && entry != TOMBSTONE) {
                add((T) entry);
            }
        }
    }

    public boolean add(T el) {
        for(int i = 0; i < array.length; i++) {
            int index = getIndex(el, i, array.length);
            if (array[index] == null || array[index] == TOMBSTONE) {
                array[index] = el;
                break;
            } else if (array[index].equals(el)) {
                return false; // elem already present
            }
        }
        len++;
        if (len / (double) array.length >= threshold) {
            growth();
        }
        return true;
    }

    public boolean remove(Object o) {
        for(int i = 0; i < array.length; i++) {
            int index = getIndex(o, i, array.length);
            if (array[index] != null && array[index] != TOMBSTONE && array[index].equals(o)) {
                array[index] = TOMBSTONE;
                len--;
                if(len / (double) array.length <= shrinkThreshold && array.length > DEFAULT_SIZE) {
                    shrink();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Object o) {
        for(int i = 0; i < array.length; i++) {
            int index = getIndex(o, i, array.length);
            if (array[index] != null && array[index] != TOMBSTONE && array[index].equals(o)) {
                return true;
            }
        }
        return false;
    }

    public void dbg() {
        System.out.println("PRINT: ");
        for(int i = 0; i < array.length; i++) {
            System.out.print("index{" + i + "} ");
            if(array[i] == null)
                System.out.println("null ");
            else if(array[i] == TOMBSTONE)
                System.out.println("DEAD");
            else
                System.out.println(array[i].toString());
        }
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int seen = 0;
            int cur = 0;

            @Override
            public boolean hasNext() {
                return seen < len;
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                while(true) {
                    if (array[cur] != null && array[cur] != TOMBSTONE) {
                        seen++;
                        T el = (T) array[cur];
                        cur++;
                        return el;
                    }
                    cur++;
                }
            }
        };
    }

    public <U> HashSet<U> map(Function<? super T, ? extends U> f) {
        HashSet<U> mapped = new HashSet<>();
        for(T t : this) {
            mapped.add(f.apply(t));
        }
        return mapped;
    }

    public HashSet<T> filter(Predicate<? super T> p) {
        HashSet<T> filtered = new HashSet<>();
        for(T el : this) {
            if(p.test(el)) {
                filtered.add(el);
            }
        }
        return filtered;
    }

    public <U> U reduce(BiFunction<? super T, ? super U, ? extends U> reducer, U initial) {
        for(T t : this) {
            initial = reducer.apply(t, initial);
        }
        return initial;
    }

    public void each(Consumer<? super T> consumer) {
        for(T t : this) {
            consumer.accept(t);
        }
    }

    public HashSet<T> intersection(HashSet<?> s2) {
        HashSet<T> result = new HashSet<>();
        for (T el : this) {
            if(s2.contains(el)) {
                result.add(el);
            }
        }
        return result;
    }

    public HashSet<T> union(HashSet<? extends T> s2) {
        HashSet<T> result = new HashSet<>();
        for (T el : this) {
            result.add(el);
        }
        for (T el : s2) {
            result.add(el);
        }
        return result;
    }

    public HashSet<T> difference(HashSet<?> s2) {
        HashSet<T> result = new HashSet<>();
        for (T el : this) {
            if(!s2.contains(el)) {
                result.add(el);
            }
        }
        return result;
    }

    public HashSet<T> symmetricalDifference(HashSet<? extends T> s2) {
        HashSet<T> result = new HashSet<>();
        for (T el : this) {
            if(!s2.contains(el)) {
                result.add(el);
            }
        }
        for (T el : s2) {
            if(!contains(el)) {
                result.add(el);
            }
        }
        return result;
    }
}
