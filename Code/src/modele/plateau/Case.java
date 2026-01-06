package modele.plateau;

import modele.jeu.Piece;

/**
 * Représente une case sur le plateau de jeu.
 * <p>
 * Une case possède des coordonnées (x, y, z) et peut contenir une {@link modele.jeu.Piece}
 * ou être vide (piece == null).
 * </p>
 */
public class Case {
    /** Coordonnée horizontale (colonne). */
    private int posX;
    /** Coordonnée verticale (ligne). */
    private int posY;
    /** Coordonnée en profondeur (couche, pour jeux 3D). */
    private int posZ;
    /** La pièce contenue dans cette case (peut être null). */
    private Piece piece; // La pièce contenue dans cette case (peut être null)

    /**
     * Crée une case vide aux coordonnées spécifiées (2D).
     * @param x abscisse (colonne)
     * @param y ordonnée (ligne)
     */
    public Case(int x, int y) {
        this.posX = x;
        this.posY = y;
        this.posZ = 0; // Par défaut couche 0 pour compatibilité 2D
        this.piece = null; // Aucune pièce par défaut
    }

    /**
     * Crée une case vide aux coordonnées spécifiées (3D).
     * @param x abscisse (colonne)
     * @param y ordonnée (ligne)
     * @param z profondeur (couche)
     */
    public Case(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
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
     * Retourne la profondeur (couche).
     * @return posZ
     */
    public int getPosZ() {
        return posZ;
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