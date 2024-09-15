import java.util.Iterator;

public class AVL<K extends Comparable<? super K>, V> implements Iterable<Entry<K, V>> {

    private static class Node<_K extends Comparable<? super _K>, _V> {
        Entry<_K, _V> entry;
        Node<_K, _V> left, right;
        int height;

        Node(_K key, _V value) {
            entry = new Entry<_K, _V>(key, value);
            left = null;
            right = null;
            height = 0;
        }

        static int getHeight(Node<?, ?> root) {
            if (root == null) {
               return -1;
            }
            return root.height;
        }

        static int getSkew(Node<?, ?> root) {
           return getHeight(root.left) - getHeight(root.right);
        }
    }

    private Node<K, V> root;
    private int size;


    public boolean contains(K key) {
        if (root == null) {
            return false;
        }
        Node<K, V> iter = root;
        while(iter != null) {
           int compare = iter.entry.getKey().compareTo(key);
           if (compare > 0) {
              iter = iter.left;
           } else if (compare < 0) {
              iter = iter.right;
           } else {
               break;
           }
        }
        return iter != null;
    }

    public boolean add(K key, V value) {
        int oldSize = size;
        root = add(root, key, value);
        return size > oldSize;
    }

    public Node<K, V> add(Node<K, V> root, K key, V value) {
        if (root == null) {
            size++;
            return new Node<K, V>(key, value);
        }
        int compare = root.entry.getKey().compareTo(key);
        if (compare > 0) {
            root.left = add(root.left, key, value);
        } else if (compare < 0) {
            root.right = add(root.right, key, value);
        } else {
            // update current value, entry doesn't have sets methods
            var updated = new Node<K, V>(key, value);
            updated.left = root.left;
            updated.right = root.right;

            root.left = null;
            root.right = null;

            return root;
        }

        root.height = 1 + Math.max(Node.getHeight(root.left), Node.getHeight(root.right));

        return fixup(root);
    }

    private Node<K, V> fixup(Node<K, V> root) {
        var skewFactor = Node.getSkew(root);
        if (skewFactor < -1) {
            if (Node.getSkew(root.right) > 0) {
                root.right = rotateClockwise(root.right);
            }
            root = rotateCounterClockwise(root);
        } else if(skewFactor > 1) {
            if (Node.getSkew(root.left) > 0) {
                root.left = rotateCounterClockwise(root.left);
            }
            root = rotateClockwise(root);
        }
        return root;
    }

    private Node<K, V> rotateClockwise(Node<K, V> root) {
        Node<K, V> pivot = root.left;
        root.left = pivot.right;
        pivot.right = root;

        root.height = 1 + Math.max(Node.getHeight(root.left), Node.getHeight(root.right));
        pivot.height = 1 + Math.max(Node.getHeight(pivot.left), Node.getHeight(pivot.right));

        return pivot;
    }

    private Node<K, V> rotateCounterClockwise(Node<K, V> root) {
        Node<K, V> pivot = root.right;
        root.right = pivot.left;
        pivot.left = root;

        root.height = 1 + Math.max(Node.getHeight(root.left), Node.getHeight(root.right));
        pivot.height = 1 + Math.max(Node.getHeight(pivot.left), Node.getHeight(pivot.right));

        return pivot;
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

    public static void main(String[] args) {
        AVL<String, Integer> tree = new AVL<>();
        tree.add("Ciao", 10);
        tree.add("Suca", 1);
        tree.add("pujac", 100);
        tree.add("ciao", -10);

        for(var entry : tree) {
            System.out.println("Key[" + entry.getKey() + "] = value => " + entry.getValue());
        }

        AVL<Integer, Integer> intTree = new AVL<>();
        for(int i = 0; i < 12; i++) {
            intTree.add(i, i);
        }
        for(var entry : intTree) {
            System.out.println("Key[" + entry.getKey() + "] = value => " + entry.getValue());
        }
    }

}
