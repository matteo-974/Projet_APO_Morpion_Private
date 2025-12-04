package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecCava;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Cavalier extends Piece {

    public Cavalier(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecCava(null));
    }
}

