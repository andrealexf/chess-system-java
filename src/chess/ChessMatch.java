package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;//inicia com false
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {//construtor da partida
		board = new Board(8,8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}

	public ChessPiece[][]getPieces(){
		ChessPiece[][]mat = new ChessPiece[board.getRows()][board.getColumns()];
		
		for(int i=0;i<board.getRows();i++) {
			
			for(int j=0;j<board.getColumns();j++) {
				mat[i][j]= (ChessPiece) board.piece(i,j);
			}
		}
		return mat;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		
		board.placePiece(piece, new ChessPosition(column,row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source,target);
	
		if(testCheck(currentPlayer)) {
			undoMove(source,target,capturedPiece);
			throw new ChessException("You cannot put yourself in check");
		}
		
		check = (testCheck(opponent(currentPlayer)))? true : false;//se o opponente entrou em check, retorna verdadeiro, caso contrário, falso
		
		if(testCheckMate(opponent(currentPlayer))) {//se o oponente da peça que mexeu, ficou em checkmate:
			checkMate = true;
			
		} else {
			nextTurn();
		}
		
		return (ChessPiece)capturedPiece;//downcasting to chessPiece (it was piece)
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			
			throw new ChessException("Position not on the board");
		}
		
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {//getColor é de ChessPiece, portanto, downcasting
			
			throw new ChessException("The chosen piece is not yours");
		}
		
		if(!board.piece(position).isThereAnyPossibleMove()) {//if (source) piece's target position is not possible:
			
			throw new ChessException("There is no possible moves for the chosen piece.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		
		if(!board.piece(source).possibleMove(target)){
			
			throw new ChessException("The chosen piece cannot move to target position.");
		}
	}
	
	private void nextTurn() {
		
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE)? Color.BLACK : Color.WHITE;//se o jogador for branco, muda pra preto. caso contrátio, muda pra branco
	}
	
	private Piece makeMove(Position source, Position target) {
		
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();//só pode chamar o increaseMoveCount através de um objeto ChessPiece
		
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		
		board.placePiece(p, source);
		if(capturedPiece != null) {
			
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	
	}
	
	private Color opponent(Color color) {
		
		return (color == Color.WHITE)? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		
		for(Piece p : list) {
			
			if(p instanceof King) {	
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no "+ color+ "king on the board");
	}
	
	private boolean testCheck(Color color) {
		
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for(Piece p : opponentPieces) {
			
			boolean[][]mat = p.possibleMoves();//matriz de movimentos possiveis de uma peça P
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		
		if(!testCheck(color)) {
			return false;
		}
		
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p: list) {
			
			boolean[][]mat = p.possibleMoves();
			for(int i=0; i<board.getRows();i++) {
				for(int j=0; j<board.getRows();j++) {
					
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();//downcasting -> getCHessPositin -> toPosition(só funciona no chessPiece)
						Position target = new Position(i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);//vai testar se o rei da minha cor está em check
						undoMove(source,target,capturedPiece);
						
						if(!testCheck) {
							return false; //se não está em check, retorna falso
						}
					}
				}
			}
		}
		return true;
	}
	
	private void initialSetup() {
		
		/*
		placeNewPiece('h',7, new Rook(board,Color.WHITE));
		placeNewPiece('d',1, new Rook(board,Color.WHITE));
		placeNewPiece('e',1, new King(board,Color.WHITE));
		CHECKMATE TEST
		placeNewPiece('b',8, new Rook(board,Color.BLACK));
		placeNewPiece('a',8, new King(board,Color.BLACK));
		*/
		
		
		//rooks
		placeNewPiece('a',1, new Rook(board,Color.WHITE));
		placeNewPiece('h',1, new Rook(board,Color.WHITE));
		placeNewPiece('a',8, new Rook(board,Color.BLACK));
		placeNewPiece('h',8, new Rook(board,Color.BLACK));
		
		//knights
		placeNewPiece('b',1, new Knight(board,Color.WHITE));
		placeNewPiece('g',1, new Knight(board,Color.WHITE));
		placeNewPiece('b',8, new Knight(board,Color.BLACK));
		placeNewPiece('g',8, new Knight(board,Color.BLACK));
		
		//bishops
		placeNewPiece('c',1, new Bishop(board,Color.WHITE));
		placeNewPiece('f',1, new Bishop(board,Color.WHITE));
		placeNewPiece('c',8, new Bishop(board,Color.BLACK));
		placeNewPiece('f',8, new Bishop(board,Color.BLACK));
		
		//queens
		placeNewPiece('d',1, new Queen(board,Color.WHITE));
		placeNewPiece('d',8, new Queen(board,Color.BLACK));
		
		//kings
		placeNewPiece('e',1, new King(board,Color.WHITE));
		placeNewPiece('e',8, new King(board,Color.BLACK));
		
		//pawns
		for(int i=0; i<board.getColumns();i++) {
			
			board.placePiece(new Pawn(board,Color.WHITE), new Position(6,i));
			board.placePiece(new Pawn(board,Color.BLACK), new Position(1,i));
		}
	}
}
