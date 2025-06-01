package project.chess;

public class Chessboard
{
    private IHashDynamic<Integer, Piece> boardMap;

    public Chessboard()
    {
        boardMap = new HashingDynamic<>();
    }

    public Piece getPiece(int position)
    {
        if (boardMap.contains(position))
        {
            return boardMap.item(position);
        }
        return null;
    }

    public void setPiece(int position, Piece piece)
    {
        if (piece == null)
        {
            boardMap.delete(position);
        }
        else
        {
            boardMap.add(position, piece);
        }
    }

    public boolean isOccupied(int position)
    {
        return boardMap.contains(position);
    }
}
