package hw22;
import java.util.Random;
import java.util.Scanner;

class AVLTree {
    static class Node {
        int data, height;
        Node left, right;

        public Node(int data) {
            this.data = data;
            this.height = 1;
            this.left = this.right = null;
        }
    }

    Node root;

    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private int balanceFactor(Node node) {
        return height(node.left) - height(node.right);
    }

    private int updateHeight(Node node) {
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = updateHeight(y);
        x.height = updateHeight(x);

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = updateHeight(x);
        y.height = updateHeight(y);

        return y;
    }

    public Node insert(Node root, int key) {
        if (root == null) {
            return new Node(key);
        }

        if (key < root.data) {
            root.left = insert(root.left, key);
        } else if (key > root.data) {
            root.right = insert(root.right, key);
        } else {
            // Duplicate keys are not allowed in AVL trees
            return root;
        }

        root.height = updateHeight(root);

        int balance = balanceFactor(root);

        // Left Left Case
        if (balance > 1 && key < root.left.data) {
            return rightRotate(root);
        }
        // Right Right Case
        if (balance < -1 && key > root.right.data) {
            return leftRotate(root);
        }
        // Left Right Case
        if (balance > 1 && key > root.left.data) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        // Right Left Case
        if (balance < -1 && key < root.right.data) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    public boolean search(Node root, int key) {
        if (root == null || root.data == key) {
            return root != null;
        }

        if (key < root.data) {
            return search(root.left, key);
        }

        return search(root.right, key);
    }

    public Node delete(Node root, int key) {
        if (root == null) {
            return root;
        }

        if (key < root.data) {
            root.left = delete(root.left, key);
        } else if (key > root.data) {
            root.right = delete(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            root.data = minValue(root.right);

            root.right = delete(root.right, root.data);
        }

        root.height = updateHeight(root);

        int balance = balanceFactor(root);

        // Left Left Case
        if (balance > 1 && balanceFactor(root.left) >= 0) {
            return rightRotate(root);
        }
        // Left Right Case
        if (balance > 1 && balanceFactor(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        // Right Right Case
        if (balance < -1 && balanceFactor(root.right) <= 0) {
            return leftRotate(root);
        }
        // Right Left Case
        if (balance < -1 && balanceFactor(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private int minValue(Node root) {
        int minValue = root.data;
        while (root.left != null) {
            minValue = root.left.data;
            root = root.left;
        }
        return minValue;
    }

    public static double convertToSeconds(long milliseconds) {
        return milliseconds / 1000.0;
    }
}

public class AVLTreeMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter dataset size:");
        int dataSize = scanner.nextInt();

        System.out.println("Enter the number of nodes to delete:");
        int deleteCount = scanner.nextInt();

        int min = -1000; // minimum value
        int max = 1000; // maximum value
        int[] datasetSizes = { dataSize };

        for (int size : datasetSizes) {
            testAVLTree(min, max, size, deleteCount);
        }

        scanner.close();
    }

    private static void testAVLTree(int min, int max, int dataSize, int deleteCount) {
        int[] randomNumbers = generateRandomNumbers(min, max, dataSize);

        AVLTree avlTree = new AVLTree();
        long startTime = System.currentTimeMillis();

        for (int num : randomNumbers) {
            avlTree.root = avlTree.insert(avlTree.root, num);
        }

        long endTime = System.currentTimeMillis();
        long creationTimeInMillis = endTime - startTime;
        double creationTimeInSeconds = AVLTree.convertToSeconds(creationTimeInMillis);
        System.out.printf("AVL Tree Insertion Time: %.9f seconds\n", creationTimeInSeconds);

        // Randomly search for a number and measure the time
        int randomSearchNumber = randomNumbers[new Random().nextInt(dataSize)];
        startTime = System.currentTimeMillis();
        boolean found = avlTree.search(avlTree.root, randomSearchNumber);
        endTime = System.currentTimeMillis();
        long searchTimeInMillis = endTime - startTime;
        double searchTimeInSeconds = AVLTree.convertToSeconds(searchTimeInMillis);
        System.out.printf("AVL Tree Search Time: %.9f seconds\n", searchTimeInSeconds);

        if (found) {
            // Randomly delete a number and measure the time
            int[] randomNumbersToDelete = generateRandomNumbers(min, max, deleteCount);

            startTime = System.currentTimeMillis();
            for (int num : randomNumbersToDelete) {
                avlTree.root = avlTree.delete(avlTree.root, num);
            }
            endTime = System.currentTimeMillis();
            long deletionTimeInMillis = endTime - startTime;
            double deletionTimeInSeconds = AVLTree.convertToSeconds(deletionTimeInMillis);
            System.out.printf("AVL Tree Deletion Time: %.9f seconds\n\n", deletionTimeInSeconds);
        } else {
            System.out.printf("Element %d not found in AVL Tree.\n\n", randomSearchNumber);
        }
    }

    private static int[] generateRandomNumbers(int min, int max, int size) {
        int[] randomNumbers = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            // Use the difference between min and max when generating a random number
            int randomNumber = random.nextInt((max - min) + 1) + min;
            randomNumbers[i] = randomNumber;
        }

        return randomNumbers;
    }
}