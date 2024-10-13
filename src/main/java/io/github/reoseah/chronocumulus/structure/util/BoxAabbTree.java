package io.github.reoseah.chronocumulus.structure.util;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BoxAabbTree<T extends BoxBounded> implements BoxBounded {
    private final Map<T, LeafNode<T>> objectToNode = new HashMap<>();
    private Node root;

    public BoxAabbTree(T initialObject) {
        LeafNode<T> node = new LeafNode<>(initialObject);
        this.objectToNode.put(initialObject, node);

        this.root = node;
    }

    public Set<T> getObjects() {
        return this.objectToNode.keySet();
    }

    public LeafNode<T> add(T object) {
        LeafNode<T> node = new LeafNode<>(object);
        this.objectToNode.put(object, node);

        this.insert(node);

        return node;
    }

    private void insert(Node node) {
        Box nodeBounds = node.bounds;

        Node other = this.root;
        while (other instanceof BranchNode branch) {
            Node left = branch.left;
            Box leftBounds = left.bounds;
            Node right = branch.right;
            Box rightBounds = right.bounds;

            other = getVolume(encompass(leftBounds, nodeBounds))
                    < getVolume(encompass(rightBounds, nodeBounds))
                    ? left : right;
        }

        BranchNode insertedNode = new BranchNode(node, other);

        insertedNode.parent = other.parent;
        other.parent = insertedNode;
        node.parent = insertedNode;

        if (insertedNode.parent == null) {
            this.root = insertedNode;
        } else {
            if (insertedNode.parent.left == other) {
                insertedNode.parent.left = insertedNode;
            } else {
                assert insertedNode.parent.right == other;
                insertedNode.parent.right = insertedNode;
            }
            this.adjustAncestors(insertedNode);
        }
    }

    public void remove(T object) {
        Node node = this.objectToNode.remove(object);
        if (node == null) {
            return;
        }
        if (node.parent == null) {
            throw new UnsupportedOperationException();
        }
        BranchNode parent = node.parent;
        Node sibling = node == parent.left ? parent.right : parent.left;
        BranchNode grandparent = parent.parent;
        if (grandparent == null) {
            this.root = sibling;
            sibling.parent = null;
            return;
        }
        if (grandparent.left == parent) {
            grandparent.left = sibling;
        } else {
            assert grandparent.right == parent;
            grandparent.right = sibling;
        }
        sibling.parent = grandparent;

        this.adjustAncestors(parent);
    }

    private void adjustAncestors(Node start) {
        for (BranchNode node = start.parent; node != null; node = node.parent) {
            Box leftBounds = node.left.bounds;
            Box rightBounds = node.right.bounds;

            node.bounds = encompass(leftBounds, rightBounds);
        }
    }

    public boolean intersects(Box box) {
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(this.root);
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            if (box.intersects(node.bounds)) {
                if (node instanceof BranchNode branch) {
                    queue.add(branch.left);
                    queue.add(branch.right);
                    continue;
                }
                assert node instanceof LeafNode<?>;
                return true;
            }
        }
        return false;
    }

    public boolean intersectsAny(BoxBounded... objects) {
        for (BoxBounded object : objects) {
            if (this.intersects(object.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public List<T> intersectingObjects(Box box) {
        List<T> collisions = new ArrayList<>();

        Deque<Node> queue = new ArrayDeque<>();
        queue.add(this.root);
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            if (box.intersects(node.bounds)) {
                if (node instanceof BranchNode branch) {
                    queue.add(branch.left);
                    queue.add(branch.right);
                } else {
                    @SuppressWarnings("unchecked")
                    LeafNode<T> leaf = (LeafNode<T>) node;
                    collisions.add(leaf.object);
                }
            }
        }
        return collisions;
    }

    public List<Pair<LeafNode<T>, LeafNode<T>>> intersectingLeaves(BoxAabbTree<T> other) {
        List<Pair<LeafNode<T>, LeafNode<T>>> collisions = new ArrayList<>();

        Deque<Pair<Node, Node>> queue = new ArrayDeque<>();
        queue.add(Pair.of(this.root, other.root));
        while (!queue.isEmpty()) {
            Pair<Node, Node> pair = queue.remove();
            Node left = pair.left();
            Node right = pair.right();

            if (left instanceof BranchNode branch1 && right instanceof BranchNode branch2) {
                if (branch1.bounds.intersects(branch2.bounds)) {
                    queue.add(Pair.of(branch1.left, branch2.left));
                    queue.add(Pair.of(branch1.left, branch2.right));
                    queue.add(Pair.of(branch1.right, branch2.left));
                    queue.add(Pair.of(branch1.right, branch2.right));
                }
            } else if (left instanceof BranchNode branch) {
                if (branch.bounds.intersects(right.bounds)) {
                    queue.add(Pair.of(branch.left, right));
                    queue.add(Pair.of(branch.right, right));
                }
            } else if (right instanceof BranchNode branch) {
                if (branch.bounds.intersects(right.bounds)) {
                    queue.add(Pair.of(left, branch.left));
                    queue.add(Pair.of(left, branch.right));
                }
            } else {
                if (left.bounds.intersects(right.bounds)) {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Pair<LeafNode<T>, LeafNode<T>> leafPair = (Pair) pair;
                    collisions.add(leafPair);
                }
            }
        }
        return collisions;
    }

    @Override
    public Box getBounds() {
        return this.root.bounds;
    }

    public static double getVolume(Box bounds) {
        double width = bounds.maxX - bounds.minX;
        double height = bounds.maxY - bounds.minY;
        double depth = bounds.maxZ - bounds.minZ;

        return width * height * depth;
    }

    public static Box encompass(Box a, Box b) {
        double minX = Math.min(a.minX, b.minX);
        double minY = Math.min(a.minY, b.minY);
        double minZ = Math.min(a.minZ, b.minZ);
        double maxX = Math.max(a.maxX, b.maxX);
        double maxY = Math.max(a.maxY, b.maxY);
        double maxZ = Math.max(a.maxZ, b.maxZ);
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public sealed static class Node permits BranchNode, LeafNode {
        public Box bounds;
        // Null only for the root BranchNode node
        public @Nullable BranchNode parent;

        protected Node(Box bounds) {
            this.bounds = bounds;
        }
    }

    public static final class LeafNode<T extends BoxBounded> extends Node {
        public final T object;

        public LeafNode(T object) {
            super(object.getBounds());
            this.object = object;
        }
    }

    public static final class BranchNode extends Node {
        public Node left, right;

        public BranchNode(Node leftChild, Node rightChild) {
            super(encompass(leftChild.bounds, rightChild.bounds));
            this.left = leftChild;
            this.right = rightChild;
        }
    }
}