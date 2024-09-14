import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.Iterator;

public class TreeMap<K extends Comparable<? super K>, V> implements Iterable<TreeMap.Entry<K, V>> {

    private Node<K, V> root = null;
    private int size = 0;

    public static void main(String[] args) {
        TreeMap<Integer, String> tree = new TreeMap<>();
        tree.add(1, "Hello");
        tree.add(3, ", ");
        tree.add(6, "World");
        tree.add(7, "!");

        for(var entry : tree) {
            System.out.print(entry.value);
        }
        System.out.println();

        for(var entry : tree.preOrderIterator()) {
            System.out.print(entry.value);
        }
        System.out.println();

        for(var entry : tree.postOrderIterator()) {
            System.out.print(entry.value);
        }
        System.out.println();

        tree.each(entry -> System.out.println("Key := " + entry.key + "\tValue := '" + entry.value + "'"));

        System.out.println();
        TreeMap<Integer, Integer> treeLen = tree.map(s -> s.length());
        treeLen.each(entry -> System.out.println("Key := " + entry.key + "\tValue := " + entry.value));

        System.out.println();
        var stringLenSum = treeLen.reduce((entry, acc) -> acc + entry.value, Integer.valueOf(0));
        System.out.println("The sum of all str lengths is := " + stringLenSum);

        System.out.println();
        var dontNeedTrim = tree.filterByValues(s -> s.equals(s.trim()));
        dontNeedTrim.each(entry -> System.out.println("Key := " + entry.key + "\tValue := '" + entry.value + "'"));

        System.out.println();
        TreeMap<Integer, String> other = new TreeMap<>();
        other.add(6, "Whatever");
        other.add(5, "CyberWorld");

        TreeMap<Integer, String> symDiff = tree.symmetricalDifference(other);
        for(var entry : symDiff) {
            System.out.print(entry.value);
        }
        System.out.println();
    }

    public static class Entry<_K, _V> {
        private _K key;
        private _V value;

        public Entry(_K key, _V value) {
            this.key = key;
            this.value = value;
        }

        public _K getKey() {
            return key;
        }

        public _V getValue() {
            return value;
        }
    }

    private static class Node<__K extends Comparable<? super __K>, __V> {
        Entry<__K, __V> entry;
        Node<__K, __V> left, right;
        public Node(__K key, __V value) {
            entry = new Entry<>(key, value);
        }
    }

    // in order
    @Override
    public Iterator<Entry<K, V>> iterator() {

        return new Iterator<Entry<K, V>>() {
            Stack<Node<K,V>> stack = new Stack<Node<K,V>>();
            Node<K, V> current = root;

            @Override
            public boolean hasNext() {
                return !stack.isEmpty() || current != null;
            }

            @Override
            public Entry<K, V> next() {
                while(current != null) {
                    stack.push(current);
                    current = current.left;
                }

                current = stack.pop();
                var nextEntry = current.entry;

                current = current.right;

                return nextEntry;
            }
        };
    }

    public Iterable<Entry<K,V>> postOrderIterator() {

        return new Iterable<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {

                return new Iterator<Entry<K, V>>() {
                    Stack<Node<K,V>> stack = new Stack<Node<K,V>>();
                    Node<K, V> current = root, last = null;

                    @Override
                    public boolean hasNext() {
                        return !stack.isEmpty() || current != null;
                    }

                    @Override
                    public Entry<K, V> next() {
                        Entry<K,V> nextEntry = null;
                        while(hasNext()) {
                            if (current != null) {
                                stack.push(current);
                                current = current.left;
                            } else {
                                current = stack.peek();
                                if(current.right != last && current.right != null) {
                                    current = current.right;
                                } else {
                                    last = current;
                                    nextEntry = current.entry;
                                    stack.pop();
                                    current = null;
                                    break;
                                }
                            }
                        }
                        return nextEntry;
                    }
                };
            }

        };
    }

    public Iterable<Entry<K,V>> preOrderIterator() {
        return new Iterable<Entry<K, V>>() {
            Stack<Node<K,V>> stack =  root != null ? new Stack<Node<K,V>>(root) : new Stack<Node<K,V>>();

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {

                    @Override
                    public boolean hasNext() {
                        return !stack.isEmpty();
                    }


                    @Override
                    public Entry<K, V> next() {
                        Node<K, V> pop = stack.pop();
                        if (pop.right != null) {
                            stack.push(pop.right);
                        }
                        if (pop.left != null) {
                            stack.push(pop.left);
                        }
                        return pop.entry;
                    }
                };
            }
        };
    }

    public boolean add(K key, V value) {
        if (root == null) {
            root = new Node<K, V>(key, value);
            return true;
        }

        var iter = root;
        while(true) {
            int compare = iter.entry.key.compareTo(key);
            if (compare > 0) {
                if (iter.left == null) {
                    iter.left = new Node<>(key, value);
                } else {
                    iter = iter.left;
                }
            } else if (compare < 0) {
                if (iter.right == null) {
                    iter.right = new Node<>(key, value);
                } else {
                    iter = iter.right;
                }
            } else {
                return false;
            }
        }
    }

    public boolean remove(K key) {
        return false;
    }

    public boolean contains(K key) {
        if (root == null) {
            return false;
        }
        var iter = root;
        while(iter != null) {
            int compare = iter.entry.key.compareTo(key);
            if (compare > 0) {
                iter = iter.left;
            } else if (compare < 0) {
                iter = iter.right;
            } else {
                return true;
            }
        }
        return false;
    }

    public TreeMap<K, V> intersect(TreeMap<? super K, ?> other) {
        TreeMap<K, V> intersection = new TreeMap<>();
        for(Entry<K, V> entry : this){
            if(other.contains(entry.key)) {
                intersection.add(entry.key, entry.value);
            }
        }
        return intersection;
    }

    public TreeMap<K, V> union(TreeMap<? extends K, ? extends V> other) {
        TreeMap<K, V> unions = new TreeMap<>();
        for(Entry<K, V> entry : this){
            unions.add(entry.key, entry.value);
        }
        for(Entry<? extends K, ? extends V> entry : other){
            unions.add(entry.key, entry.value);
        }
        return unions;
    }

    public TreeMap<K, V> difference(TreeMap<? super K, ?> other) {
        TreeMap<K, V> differences = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(!other.contains(entry.key)) {
                differences.add(entry.key, entry.value);
            }
        }
        return differences;
    }

    // ask Faella
    public TreeMap<K, V> symmetricalDifference(TreeMap<K, ? extends V> other) {
        TreeMap<K, V> symDiff = new TreeMap<K, V>();
        for (var entry : this) {
            if (!other.contains(entry.key)) {
                symDiff.add(entry.key, entry.value);
            }
        }
        for (var entry : other) {
            if(!contains(entry.key)) {
                symDiff.add(entry.key, entry.value);
            }
        }

        return symDiff;
    }

    public <U> TreeMap<K, U> map(Function<? super V, ? extends U> f) {
        TreeMap<K, U> mapped = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            mapped.add(entry.key, f.apply(entry.value));
        }
        return mapped;
    }

    public TreeMap<K, V> filterByKeys(Predicate<? super K> p) {
        TreeMap<K, V> filtered = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(p.test(entry.key)) {
                filtered.add(entry.key, entry.value);
            }
        }
        return filtered;
    }

    public TreeMap<K, V> filterByValues(Predicate<? super V> p) {
        TreeMap<K, V> filtered = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(p.test(entry.value)) {
                filtered.add(entry.key, entry.value);
            }
        }
        return filtered;
    }

    public <U> U reduce(BiFunction<? super Entry<? extends K, ? extends V>, ? super U, ? extends U> reducer, U initial ) {
        for(Entry<K, V> entry : this) {
            initial = reducer.apply(entry, initial);
        }
        return initial;
    }

    public void each(Consumer<Entry<? super K, ? extends V>> consumer) {
        for(Entry<K, V> entry : this) {
            consumer.accept(entry);
        }
    }
}
