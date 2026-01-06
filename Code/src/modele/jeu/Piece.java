package modele.jeu;

import modele.deplacements.DecCasesAccessibles;
import modele.plateau.Case;
import modele.plateau.Plateau;


/**
 * Représente une pièce générique posée sur un plateau.
 * <p>
 * Chaque pièce connaît sa couleur, sa case, le plateau auquel elle appartient
 * et sa stratégie de déplacements ({@link modele.deplacements.DecCasesAccessibles}).
 * </p>
 */
public abstract class Piece {
    protected String couleur;
    protected Case c;
    protected Plateau p;
    protected DecCasesAccessibles dCA;

    /**
     * Construit une pièce avec sa couleur, son plateau, sa case et sa stratégie de déplacement.
     * La pièce est automatiquement posée sur la case fournie.
     * @param couleur couleur logique de la pièce (ex. "Blanc" ou "Noir")
     * @param p plateau de référence
     * @param c case où poser la pièce
     * @param dCA stratégie de calcul des cases accessibles
     */
    public Piece(String couleur, Plateau p, Case c, DecCasesAccessibles dCA) {
        this.couleur = couleur;
        this.p = p;
        this.c = c;
        this.dCA = dCA;
        this.c.setPiece(this);
    }

    /**
     * Retourne la couleur de la pièce.
     * @return la couleur (ex. "Blanc" ou "Noir")
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Retourne la case actuelle occupée par la pièce.
     * @return la case courante
     */
    public Case getCase() {
        return this.c;
    }

    /**
     * Met à jour la case occupée par la pièce (ne modifie pas le contenu de l'ancienne case).
     * @param c nouvelle case de la pièce
     */
    public void setCase(Case c) {
        this.c = c;
    }

    /**
     * Retourne le plateau sur lequel la pièce est posée.
     * @return le plateau associé
     */
    public Plateau getPlateau() {
        return this.p;
    }

    /**
     * Indique si la pièce fournie est alliée (même couleur) à cette pièce.
     * @param piece autre pièce à comparer
     * @return true si les deux pièces ont la même couleur non nulle, false sinon
     */
    public boolean estAlliee(Piece piece) {
        if (piece == null || this.couleur == null || piece.couleur == null) {
            return false;
        }
        return this.couleur.equals(piece.couleur);
    }

    /**
     * Retourne la stratégie de calcul des cases accessibles associée à la pièce.
     * @return le décorateur/stratégie de déplacements
     */
    public DecCasesAccessibles getdCA() {
        return dCA;
    }
}