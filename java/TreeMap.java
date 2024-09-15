import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.Iterator;

public class TreeMap<K extends Comparable<? super K>, V> implements Iterable<Entry<K, V>> {

    private Node<K, V> root = null;
    private int len = 0;

    public static void main(String[] args) {
        TreeMap<Integer, String> tree = new TreeMap<>();
        tree.add(1, "Hello");
        tree.add(3, ", ");
        tree.add(6, "World");
        tree.add(7, "!");

        System.out.println("In order visit on tree");
        for(var entry : tree) {
            System.out.print(entry.getValue());
        }
        System.out.println();

        System.out.println("Pre order visit on tree");
        for(var entry : tree.preOrderIterator()) {
            System.out.print(entry.getValue());
        }
        System.out.println();

        System.out.println("Post order visit on tree");
        for(var entry : tree.postOrderIterator()) {
            System.out.print(entry.getValue());
        }
        System.out.println();

        System.out.println("Consuming tree elements with 'each' method + lambda function");
        tree.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := '" + entry.getValue() + "'"));
        System.out.println();

        System.out.println("Trygin the 'map' function");
        TreeMap<Integer, Integer> treeLen = tree.map(s -> s.length());
        treeLen.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := " + entry.getValue()));

        System.out.println("Trying the 'reduce' function");
        var stringLenSum = treeLen.reduce((entry, acc) -> acc + entry.getValue(), Integer.valueOf(0));
        System.out.println("The sum of all str lengths is := " + stringLenSum);
        System.out.println();

        System.out.println("Trying the 'filterByValue' function");
        var dontNeedTrim = tree.filterByValues(s -> s.equals(s.trim()));
        dontNeedTrim.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := '" + entry.getValue() + "'"));
        System.out.println();

        System.out.println("Trying the 'symmetricalDifference' function");
        TreeMap<Integer, String> other = new TreeMap<>();
        other.add(6, "Whatever");
        other.add(5, "CyberWorld");

        TreeMap<Integer, String> symDiff = tree.symmetricalDifference(other);
        for(var entry : symDiff) {
            System.out.print(entry.getValue());
        }
        System.out.println();

        System.out.println("Trying the 'remove' method");
        TreeMap<Integer, String> copy = tree.map(s -> s);
        System.out.println("Pre remove");
        copy.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := " + entry.getValue()));
        System.out.println("Post remove");
        copy.remove(6);
        copy.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := " + entry.getValue()));
        System.out.println("Post remove root");
        copy.remove(1);
        copy.each(entry -> System.out.println("Key := " + entry.getKey() + "\tValue := " + entry.getValue()));
    }

    private static class Node<_K extends Comparable<? super _K>, _V> {
        Entry<_K, _V> entry;
        Node<_K, _V> left, right;
        public Node(_K key, _V value) {
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
            len++;
            return true;
        }

        var iter = root;
        while(true) {
            int compare = iter.entry.getKey().compareTo(key);
            if (compare > 0) {
                if (iter.left == null) {
                    len++;
                    iter.left = new Node<>(key, value);
                    return true;
                } else {
                    iter = iter.left;
                }
            } else if (compare < 0) {
                if (iter.right == null) {
                    len++;
                    iter.right = new Node<>(key, value);
                    return true;
                } else {
                    iter = iter.right;
                }
            } else {
                return false;
            }
        }
    }

    public boolean remove(K key) {
        if(root == null) {
            return false;
        }

        boolean isRemoved = false;
        Node<K, V> iter = root, parent = null;
        while(iter != null) {
            int compare = iter.entry.getKey().compareTo(key);
            if (compare > 0) {
                parent = iter;
                iter = iter.left;
            } else if (compare < 0) {
                parent = iter;
                iter = iter.right;
            } else {
                break;
            }
        }

        if (iter != null) {
            len--;
            isRemoved = true;
            if (parent == null) {
                root = deleteRoot(root);
            } else if (iter == parent.left) {
                parent.left = deleteRoot(iter);
            } else {
                parent.right = deleteRoot(iter);
            }
        }

        return isRemoved;
    }

    private Node<K, V> deleteRoot(Node<K, V> root) {
        if (root.left == null) {
            return root.right;
        } else if (root.right == null) {
            return root.left;
        } else {
            Node<K, V> minimum = minDeconnection(root, root.right);
            minimum.left = root.left;
            minimum.right = root.right;
            return minimum;
        }
    }

    private Node<K, V> minDeconnection(Node<K, V> parent, Node<K, V> child) {
        if(child.left == null) {
            parent.right = child.right;
            return child;
        }

        while(child.left != null) {
            child = child.left;
            parent = parent.left;
        }
        parent.left = child.right;
        return child;
    }

    public boolean contains(K key) {
        if (root == null) {
            return false;
        }
        var iter = root;
        while(iter != null) {
            int compare = iter.entry.getKey().compareTo(key);
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
            if(other.contains(entry.getKey())) {
                intersection.add(entry.getKey(), entry.getValue());
            }
        }
        return intersection;
    }

    public TreeMap<K, V> union(TreeMap<? extends K, ? extends V> other) {
        TreeMap<K, V> unions = new TreeMap<>();
        for(Entry<K, V> entry : this){
            unions.add(entry.getKey(), entry.getValue());
        }
        for(Entry<? extends K, ? extends V> entry : other){
            unions.add(entry.getKey(), entry.getValue());
        }
        return unions;
    }

    public TreeMap<K, V> difference(TreeMap<? super K, ?> other) {
        TreeMap<K, V> differences = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(!other.contains(entry.getKey())) {
                differences.add(entry.getKey(), entry.getValue());
            }
        }
        return differences;
    }

    // ask Faella
    // Can't generalize TreeMap K any further, I need both '? super' and '? extends' hence I'm stuck with only K
    public TreeMap<K, V> symmetricalDifference(TreeMap<K, ? extends V> other) {
        TreeMap<K, V> symDiff = new TreeMap<K, V>();
        for (var entry : this) {
            if (!other.contains(entry.getKey())) {
                symDiff.add(entry.getKey(), entry.getValue());
            }
        }
        for (var entry : other) {
            if(!contains(entry.getKey())) {
                symDiff.add(entry.getKey(), entry.getValue());
            }
        }

        return symDiff;
    }

    public <U> TreeMap<K, U> map(Function<? super V, ? extends U> f) {
        TreeMap<K, U> mapped = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            mapped.add(entry.getKey(), f.apply(entry.getValue()));
        }
        return mapped;
    }

    public <Z extends Comparable<? super Z>> TreeMap<Z, V> mapKeys(Function<? super K, ? extends Z> f) {
        TreeMap<Z, V> mapped = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            mapped.add(f.apply(entry.getKey()), entry.getValue());
        }
        return mapped;
    }

    public <Z extends Comparable<? super Z>, T> TreeMap<Z, T> mapBoth(Function<? super K, ? extends Z> fnKey, Function<? super V, ? extends T> fnValue) {
       TreeMap<Z, T> mapped = new TreeMap<>();
       for (Entry<K, V> entry : this) {
           mapped.add(fnKey.apply(entry.getKey()), fnValue.apply(entry.getValue()));
       }
       return mapped;
    }

    public TreeMap<K, V> filterByKeys(Predicate<? super K> p) {
        TreeMap<K, V> filtered = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(p.test(entry.getKey())) {
                filtered.add(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    public TreeMap<K, V> filterByValues(Predicate<? super V> p) {
        TreeMap<K, V> filtered = new TreeMap<>();
        for (Entry<K, V> entry : this) {
            if(p.test(entry.getValue())) {
                filtered.add(entry.getKey(), entry.getValue());
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

    public void each(Consumer<? super Entry<? extends K, ? extends V>> consumer) {
        for(Entry<K, V> entry : this) {
            consumer.accept(entry);
        }
    }
}
