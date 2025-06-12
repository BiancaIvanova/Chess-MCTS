package project.chess;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.datastructures.*;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		//SpringApplication.run(ChessApplication.class, args);

		// Testing the tree data structure
		Tree<String> tree = new Tree<>();
		tree.setRoot("A");

		Node<String> a = tree.getRoot();
		Node<String> b = new Node<>("B");
		Node<String> c = new Node<>("C");
		Node<String> d = new Node<>("D");

		a.addChild(b);
		a.addChild(c);
		c.addChild(d);

		System.out.println("Tree structure:");
		tree.displayTreeStructure();

		System.out.println("\nPre-order:");
		tree.displayPreOrder();

		System.out.println("\n\nPost-order:");
		tree.displayPostOrder();

		System.out.println("\n\nLevel-order:");
		tree.displayLevelOrder();

		System.out.println("\n\nAs array:");
		System.out.println(tree.asArray());

//		Chessboard board = new Chessboard();
//		board.importBasicFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1");
//
//		System.out.println("Initial Position:");
//		board.printBoard();
//
//		long  startTime = System.currentTimeMillis();
//
//		for (int depth = 1; depth <= 10; depth++) {
//			long nodes = Perft.perft(board, depth, Piece.Colour.WHITE);
//			long elapsedMillis = System.currentTimeMillis() - startTime;
//			System.out.printf("Depth %d: %d nodes  \t\tTime elapsed: %.3f seconds\n", depth, nodes, elapsedMillis / 1000.0);
//		}
	}
}
