package modele.plateau;

import modele.jeu.Piece;

/**
 * Représente une case sur le plateau de jeu.
 * <p>
 * Une case possède des coordonnées (x, y) et peut contenir une {@link modele.jeu.Piece}
 * ou être vide (piece == null).
 * </p>
 */
public class Case {
    /** Coordonnée horizontale (colonne). */
    private int posX;
    /** Coordonnée verticale (ligne). */
    private int posY;
    /** La pièce contenue dans cette case (peut être null). */
    private Piece piece; // La pièce contenue dans cette case (peut être null)

    /**
     * Crée une case vide aux coordonnées spécifiées.
     * @param x abscisse (colonne)
     * @param y ordonnée (ligne)
     */
    public Case(int x, int y) {
        this.posX = x;
        this.posY = y;
        this.piece = null; // Aucune pièce par défaut
    }

    /**
     * Retourne l'abscisse (colonne).
     * @return posX
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Retourne l'ordonnée (ligne).
     * @return posY
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Retourne la pièce présente sur la case (ou null si vide).
     * @return la pièce courante ou null
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Place une pièce sur la case (ou null pour la vider).
     * @param piece la pièce à placer, ou null pour vider
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

}