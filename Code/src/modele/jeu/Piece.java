package modele.jeu;

import modele.deplacements.DecCasesAccessibles;
import modele.plateau.Case;
import modele.plateau.Plateau;


public abstract class Piece {
    protected String couleur;
    protected Case c;
    protected Plateau p;
    protected DecCasesAccessibles dCA;

    public Piece(String couleur, Plateau p, Case c, DecCasesAccessibles dCA) {
        this.couleur = couleur;
        this.p = p;
        this.c = c;
        this.dCA = dCA;
        this.c.setPiece(this);
    }

    public String getCouleur() {
        return couleur;
    }

    public Case getCase() {
        return this.c;
    }

    public void setCase(Case c) {
        this.c = c;
    }

    public Plateau getPlateau() {
        return this.p;
    }

    public boolean estAlliee(Piece piece) {
        if (piece == null || this.couleur == null || piece.couleur == null) {
            return false;
        }
        return this.couleur.equals(piece.couleur);
    }

    public DecCasesAccessibles getdCA() {
        return dCA;
    }
}