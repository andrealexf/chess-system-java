package boardgame;

public class Piece {

	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		
		this.board = board;
		position = null;
	}

	protected Board getBoard() {//o tabuleiro não deve ser acessado por fora do pacote "boardgame". pode ser acessada pelas peças, por exemplo
		return board;
	}
		
	
}
