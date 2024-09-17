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

	private Node<_K, _V> rotateClockwise() {
	    Node<_K, _V> pivot = left;
	    left = pivot.right;
	    pivot.right = this;

	    height = 1 + Math.max(Node.getHeight(left), Node.getHeight(right));
	    pivot.height = 1 + Math.max(Node.getHeight(pivot.left), Node.getHeight(pivot.right));

	    return pivot;
	}

	private Node<_K, _V> rotateCounterClockwise() {
	    Node<_K, _V> pivot = right;
	    right = pivot.left;
	    pivot.left = this;
	    
	    height = 1 + Math.max(Node.getHeight(left), Node.getHeight(right));
	    pivot.height = 1 + Math.max(Node.getHeight(pivot.left), Node.getHeight(pivot.right));
	    
	    return pivot;
	}

    }

    private Node<K, V> root;
    private int size;

    public int size() {
	return size;
    }

    public boolean isEmpty() {
	return size == 0;
    }

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
            var updated = new Node<K, V>(key, value);
            updated.left = root.left;
            updated.right = root.right;

            root.left = null;
            root.right = null;
            return root;
        }
        return fixup(root);
    }

    private Node<K, V> fixup(Node<K, V> root) {
	if(root == null) {
	    return null;
	}
	root.height = 1 + Math.max(Node.getHeight(root.left), Node.getHeight(root.right));
        var skewFactor = Node.getSkew(root);
        if (skewFactor < -1) {
            if (Node.getSkew(root.right) > 0) {
                root.right = root.right.rotateClockwise();
            }
            root = root.rotateCounterClockwise();
        } else if(skewFactor > 1) {
            if (Node.getSkew(root.left) > 0) {
                root.left = root.left.rotateCounterClockwise();
            }
            root = root.rotateClockwise();
        }
        return root;
    }

    public boolean remove(K key) {
	int oldSize = size;
	root = remove(root, key);
	return size < oldSize;
    }

    private Node<K, V> remove(Node<K, V> root, K key) {
	if (root == null) {
	    return null;
	}
	int compare = root.entry.getKey().compareTo(key);
	if (compare > 0) {
	    root.left = remove(root.left, key);
	} else if (compare < 0) {
	    root.right = remove(root.right, key);
	} else {
	    size--;
	    if (root.left == null) {
		root = root.right;
	    } else if (root.right == null) {
		root = root.left;
	    } else {
		var min = minimumDeconnection(root, root.right);
		min.left = root.left;
		min.right = root.right;
		root = min;
	    }
	}
	return fixup(root);
    }

    private Node<K, V> minimumDeconnection(Node<K, V> parent, Node<K, V> child) {
	 if(child.left == null){
	     if (child == parent.left) {
	 	parent.left = child.right;
	     } else {
	 	parent.right = child.right;
	     }
	     return child;
	 }
	 var min = minimumDeconnection(child, child.left);
	 if (child == parent.left) {
	     parent.left = fixup(child);
	 } else {
	     parent.right = fixup(child);
	 }
	 return min;
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

    private static boolean assertAVLProperties(Node<?, ?> root) {
	if (root == null) {
	    return true;
	}
	int skew = Node.getSkew(root);
	return assertAVLProperties(root.left) && assertAVLProperties(root.right) && skew >= -1 && skew <= 1;
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
        for(int i = 0; i < 30; i++) {
            intTree.add(i, i);
        }
        for(var entry : intTree) {
            System.out.println("Key[" + entry.getKey() + "] = value => " + entry.getValue());
        }
	System.out.println();
	for(int i = 0; i < 30; i++) {
	    intTree.remove(i);
	    System.out.println("Removing: " + i);
	    for(var entry : intTree) {
		System.out.println("Key[" + entry.getKey() + "] = value => " + entry.getValue());
	    }
	    System.out.println("Props Holds? => " + assertAVLProperties(intTree.root) + "\n" );
	}
	System.out.println();
	AVL<Integer, Integer> biggerTree = new AVL<>();
	for (int i = 0; i < 1e5; i++) {
	    biggerTree.add(i, i);
	}
	System.out.println("Props Holds on bigger tree? => " + assertAVLProperties(biggerTree.root) + "\n" );
	for (int i = 0; i < 1e5; i++) {
	    if (!assertAVLProperties(biggerTree.root)) {
		System.out.println("Props don't hold after delete");
	    }
	    biggerTree.remove(i);
	}
	System.out.println("Is empty? => " + biggerTree.isEmpty());
    }

}
