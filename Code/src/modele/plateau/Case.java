package modele.plateau;

import modele.jeu.Piece;

public class Case {
    private int posX;
    private int posY;
    private Piece piece; // La pièce contenue dans cette case (peut être null)

    public Case(int x, int y) {
        this.posX = x;
        this.posY = y;
        this.piece = null; // Aucune pièce par défaut
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Piece getPiece() {
        return piece;
    }


    public void setPiece(Piece piece) {
        this.piece = piece;
    }

}