package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece{

	public Pawn(Board board, Color color,ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString() {
		
		return "P";
	}
	
	private ChessMatch chessMatch;
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0,0);
		
		if(getColor() == Color.WHITE) {
			
			//above -1
			p.setValues(position.getRow()-1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//above -2
			p.setValues(position.getRow()-2, position.getColumn());
			Position p2 = new Position(position.getRow()-1, position.getColumn()) ;//posição a frente do peão
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount() == 0 && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)) {
				//se a posição que ele vai (duas à frente) existe e n tem peça, se a casa à frente (uma) existe e não tem peça
				mat[p.getRow()][p.getColumn()] = true;
			}
			//nw-eat
			p.setValues(position.getRow()-1, position.getColumn()-1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//ne-eat
			p.setValues(position.getRow()-1, position.getColumn()+1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//specialmove enpassant - white
			if(position.getRow()==3) {
				Position left = new Position(position.getRow(),position.getColumn()-1);//verifica a linha inteira
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left)==chessMatch.getEnPassantVulnerable()) {
					//posição exite, se tem oponente, se o movimento da peça foi de 2 (enpassantvulnerable)
					mat[left.getRow()-1][left.getColumn()]=true;
				}
				
				Position right = new Position(position.getRow(),position.getColumn()+1);//verifica a linha inteira
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right)==chessMatch.getEnPassantVulnerable()) {
					//posição exite, se tem oponente, se o movimento da peça foi de 2 (enpassantvulnerable)
					mat[right.getRow()-1][right.getColumn()]=true;
				}
				
			}
			
		} else {//BLACK MOVES
			
			//bellow -1
			p.setValues(position.getRow()+1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//bellow -2
			p.setValues(position.getRow()+2, position.getColumn());
			Position p2 = new Position(position.getRow()+1, position.getColumn()) ;//posição a frente do peão
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount() == 0 && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)) {
				//se a posição que ele vai (duas à frente) existe e n tem peça, se a casa à frente (uma) existe e não tem peça
				mat[p.getRow()][p.getColumn()] = true;
			}
			//sw-eat
			p.setValues(position.getRow()+1, position.getColumn()-1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//se-eat
			p.setValues(position.getRow()+1, position.getColumn()+1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//specialmove enpassant - black
			if(position.getRow()==4) {
				Position left = new Position(position.getRow(),position.getColumn()-1);//verifica a linha inteira
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left)==chessMatch.getEnPassantVulnerable()) {
					//posição exite, se tem oponente, se o movimento da peça foi de 2 (enpassantvulnerable)
					mat[left.getRow()+1][left.getColumn()]=true;
				}
				
				Position right = new Position(position.getRow(),position.getColumn()+1);//verifica a linha inteira
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right)==chessMatch.getEnPassantVulnerable()) {
					//posição exite, se tem oponente, se o movimento da peça foi de 2 (enpassantvulnerable)
					mat[right.getRow()+1][right.getColumn()]=true;
				}
				
			}
			
			
		}
		
		return mat;

	}

}
