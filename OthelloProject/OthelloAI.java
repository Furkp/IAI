import java.util.ArrayList;

public class OthelloAI implements IOthelloAI {

    int maxDepth = 5;

	/**
	 * Finds and returns the move that leads to the highest expected utility after maxDepth moves.
	 */
	public Position decideMove(GameState s){
		ArrayList<Position> moves = s.legalMoves();
        if (!moves.isEmpty()) {
            Position move = moves.get(0);
            int v = Integer.MIN_VALUE;
			for (Position pos : moves) {
                int temp = minValue(result(s, pos), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
                if(v < temp) {
                    v = temp;
                    move = pos;
                }
            }
            return move;
        }
		else
			return new Position(-1,-1);
    }	
    
    /**
     * Returns the utility of a maximised move. The move is found by recursively investigating all possible moves in the current state. 
     * Uses alpha-beta pruning to ignore paths that an opposing algorithm would prevent from being reached.
     * The recursion is mutual with minValue.
     */
    private int maxValue(GameState s, int alpha, int beta, int depth) {
        if (cutoffTest(s, depth)) return utility(s);
        int v = Integer.MIN_VALUE;
        for (Position pos : s.legalMoves()) {
            v = Integer.max(v, minValue(result(s, pos), alpha, beta, depth++));
            if (v >= beta) return v;
            alpha = Integer.max(alpha, v);
        }
        return v;
    }

    /**
     * Returns the utility of a minimised move.
     * The recursion is mutual with maxValue.
     */
    private int minValue(GameState s, int alpha, int beta, int depth) {
        if (cutoffTest(s, depth)) return utility(s);
        int v = Integer.MAX_VALUE;
        for (Position pos : s.legalMoves()) {
            v = Integer.min(v, maxValue(result(s, pos), alpha, beta, depth++));
            if (v <= alpha) return v;
            beta = Integer.min(beta, v);
        }
        return v;
    }

    /**
     * Returns true if the search has reached maxDepth or if the game is finished.
     * Otherwise returns false.
     */
    private boolean cutoffTest(GameState s, int depth) {
        return (depth >= maxDepth) || s.isFinished();
    }

    /**
     * Counts tokens as points an returns a score for the GameState.
     * Tokens from player 1 (black) increase the score while tokens from player 2 (white) decrease it.
     * These multipliers are added to represent the strategic values of the different positions:
     * Corners:             x4
     * Edges:               x3
     * Adjacent to an edge: x1
     * Other:               x2
     */
    private int utility(GameState s) {
        int score = 0;
        int multiplier;
        int[][] board = s.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if ((i == 0 || i == board.length-1) && (j == 0 || j == board.length-1)) // Corner piece
                    multiplier = 4;
                else if (i == 0 || i == board.length-1 || j == 0 || j == board.length-1) // Edge piece
                    multiplier = 3;
                else if (i == 1 || i == board.length-2 || j == 1 || j == board.length-2) // Edge adjacent piece
                    multiplier = 1;
                else
                    multiplier = 2;

                if ( board[i][j] == 1 )
    				score += multiplier;
    			else if ( board[i][j] == 2 )
    				score -= multiplier;
            }
        }
        return score;
    }

    /**
     * Returns a new GameState that is the GameState s with the move pos applied to it
     */
    private GameState result(GameState s, Position pos) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        newState.insertToken(pos);
        return newState;
    }
}
