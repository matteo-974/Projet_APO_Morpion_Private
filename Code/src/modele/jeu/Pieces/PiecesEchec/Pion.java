package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecPion;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Pion extends Piece {

    public Pion(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecPion(null));
    }
}

