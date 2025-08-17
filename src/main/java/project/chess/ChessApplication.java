package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.model.Game;
import project.chess.datastructures.*;
import project.chess.mcts.*;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ChessApplication.class, args);

		Game game = new Game();
		game.importFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1 ");

		int maxDepth = 3;

		Tree<MCTSData> tree = MCTSTreeGenerator.generateTree(game, maxDepth);

		System.out.println("Generated MCTS Tree:");
		tree.displayTreeStructure(false);
	}
}
