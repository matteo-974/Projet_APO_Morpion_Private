package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecDiag;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Fou extends Piece {

    public Fou(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecDiag(null));
    }
}