package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecDiag;
import modele.deplacements.DecLigne;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Reine extends Piece {

    public Reine(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecDiag(new DecLigne(null)));
    }
}

