package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.model.*;
import project.chess.modelanalysis.*;
import project.chess.piece.Piece;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ChessApplication.class, args);

		Game game = new Game();
		game.importFEN("8/1p1R4/8/8/8/8/K3R3/2k4n w - - 0 1 ");

        double load = CognitiveLoad.computeLoad(game.getBoard(), Piece.Colour.WHITE);

        System.out.println(load);

        /*
		int maxDepth = 3;

		Tree<MCTSData> tree = MCTSTreeGenerator.generateTree(game, maxDepth);

		System.out.println("Generated MCTS Tree:");
		tree.displayTreeStructure(false);
		*/
	}
}
