import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Assignment2Part1AVLTree {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		AVLTree tree = new AVLTree();
		//SplayTree tree = new SplayTree();
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader("book.txt"));

			while (true) {
				//Read in records from the File
				// BookNode book = readFile(fileReader);
				String ISBN = fileReader.readLine();
				String title = fileReader.readLine();
				String author = fileReader.readLine();
				if(ISBN == null) {
					break;
				}
				BookNode book = new BookNode(ISBN, title, author);
				System.out.printf("\n....Inserting ISBN: %s\n", book.ISBN);
				tree.root = tree.insert(tree.root, book, tree);
				tree.printTree(tree.root, "", false);
			}
			//Final Print After file has been read
			System.out.print("\nIn Order Traversal: ");
			tree.inOrder(tree.root);
			System.out.println("\nFinal Tree");
			tree.printTree(tree.root, "", false);
			
		//General Exception Handlin & Closing FIle
		} catch(Exception e) {
			e.printStackTrace();
		}
		// close the file
		if (fileReader != null) {
			try {
				fileReader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class BookNode {
	String ISBN, title, author;
	BookNode left, right;
	int height;

	BookNode (String ISBN, String title, String author) {
		this.ISBN = ISBN;
		this.title = title;
		this.author = author;
		this.height = 0;
		this.left= null;
		this.right = null;
	}
}


class AVLTree {
	static int count;
	static int rCall;
	static boolean isRight = true;
	static BookNode parent = null;
	BookNode root;

	public BookNode insert(BookNode node, BookNode book, AVLTree tree) {
		/* 1. Perform the normal BST insertion */
		if (node == null) {
			if (count == 0) {
				System.out.println("Root is null");
				System.out.println("Inserting book object ISBN " + book.ISBN + " at root level");
			} else if (!isRight) {
				System.out.printf("Found null, inserting Book object ISBN %s as left child of ISBN %s\n", book.ISBN, parent.ISBN);
				System.out.println("Adjusting heights");
				System.out.print("Detecting Potential Imbalance,");
			} else {
				System.out.printf("Found null, inserting Book object ISBN %s as right child of ISBN %s\n", book.ISBN, parent.ISBN);
				System.out.println("Adjusting heights");
				System.out.print("Detecting Potential Imbalance,");
			}
			count++;
			return book;
		}

		if (book.ISBN.compareTo(node.ISBN) < 0) {
			System.out.printf("Book object ISBN %s < ISBN %s, Looking at left subtree\n", book.ISBN, node.ISBN);
			parent = node;
			isRight = false;
			rCall++;
			node.left = insert(node.left, book, tree);
		}
		else if (book.ISBN.compareTo(node.ISBN) > 0) {
			System.out.printf("Book object ISBN %s > ISBN %s, Looking at right subtree\n", book.ISBN, node.ISBN);
			parent = node;
			isRight = true;
			rCall++;
			node.right = insert(node.right, book, tree);
		}
		else // Duplicate keys not allowed
			return node;

		rCall--; //1
		/* 2. Update height of this ancestor node */
		node.height = 1 + max(getHeight(node.left), getHeight(node.right));
		/* 3. Get the balance factor of this ancestor node to check whether
		   this node became unbalanced */
		int balance = getBalance(node);


		if (parent == tree.root && rCall < 1) {
			System.out.print(" AVL property is not violated\n");
		}
		
		//When imbalanced, perform rotations and display imbalance + rotation message
		if (balance > 1 || balance < -1) {
			int a = 0;
			// Left Left Case
			if (balance > 1 && book.ISBN.compareTo(node.left.ISBN) < 0) {
				System.out.print(" AVL property is violated\n");
				System.out.printf("Calling single roation for a left-left case on ISBN %s, and ISBN %s\n", parent.ISBN, node.ISBN);
				a = 1;
				return rightRotate(node);
			}
			// Right Right Case
			if (balance < -1 && book.ISBN.compareTo(node.right.ISBN) > 0) {
				System.out.print(" AVL property is violated\n");
				System.out.printf("Calling single roation for a right-right case on ISBN %s, and ISBN %s\n", parent.ISBN, node.ISBN);
				a = 1;
				return leftRotate(node);
			}
			// Left Right Case
			if (balance > 1 && book.ISBN.compareTo(node.left.ISBN) > 0) {
				System.out.print(" AVL property is violated\n");
				System.out.println("Calling double roation for a left-right case");
				node.left = leftRotate(node.left);
				a = 1;
				return rightRotate(node);
			}
			// Right Left Case
			if (balance < -1 && book.ISBN.compareTo(node.right.ISBN) < 0) {
				System.out.print(" AVL property is violated\n");
				System.out.println("Calling double roation for a right-left case");
				node.right = rightRotate(node.right);
				a = 1;
				return leftRotate(node);
			}
		}
		rCall = 0;
		return node;
	}

	int getHeight(BookNode N) {
		if (N == null) return -1;
		return N.height;
	}

	public int max(int a, int b) {
		return (a > b ? a : b);
	}

	public int getBalance(BookNode N) {
		if (N == null) return 0;

		return getHeight(N.left) - getHeight(N.right);
	}

	// A utility function to left rotate subtree rooted with x
	BookNode leftRotate(BookNode x) {
		BookNode y = x.right;
		BookNode T2 = y.left;

		// Perform rotation
		y.left = x;
		x.right = T2;
		System.out.printf("Rotate left: ISBN %s, and ISBN %s\n", x.ISBN, y.ISBN);
		// Update heights
		x.height = max(getHeight(x.left), getHeight(x.right)) + 1;
		y.height = max(getHeight(y.left), getHeight(y.right)) + 1;

		// Return new root
		return y;
	}

	// A utility function to right rotate subtree rooted with y
	BookNode rightRotate(BookNode y) {
		BookNode x = y.left;
		BookNode T2 = x.right;
		System.out.printf("Rotate right: ISBN %s, and ISBN %s\n", x.ISBN, y.ISBN);
		// Perform rotation
		x.right = y;
		y.left = T2;

		// Update heights
		y.height = max(getHeight(y.left), getHeight(y.right)) + 1;
		x.height = max(getHeight(x.left), getHeight(x.right)) + 1;

		// Return new root
		return x;
	}

	public void printTree(BookNode node, String prefix, boolean isLeft) {
		if (node != null) {
			System.out.printf("%s%s%s(b: %d, h: %d)\n", prefix, (isLeft ? "L " : "R "), node.ISBN, getBalance(node), getHeight(node));
			printTree(node.left, prefix + (isLeft ? "|  " : "   "), true);
			printTree(node.right, prefix + (isLeft ? "|  " : "   "), false);

		}
	}

	public void inOrder(BookNode node) {
		if (node != null) {
			inOrder(node.left);
			System.out.print(node.ISBN + " ");
			inOrder(node.right);
		}
	}
}

