package modele.jeu.Pieces;

import modele.deplacements.DecPuissance4;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Représente un pion du jeu Puissance 4
 */
public class PionPuissance4 extends Piece {
    public PionPuissance4(String couleur, Plateau p, Case c) {
        super(couleur, p, c, new DecPuissance4());
    }
}
