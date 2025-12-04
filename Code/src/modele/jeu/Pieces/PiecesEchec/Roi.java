package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecRoi;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Roi extends Piece {
    private boolean aDejaBouge = false;

    public boolean getADejaBouge() {
        return aDejaBouge;
    }

    public void setADejaBouge(boolean aDejaBouge) {
        this.aDejaBouge = aDejaBouge;
    }

    public Roi(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecRoi(null));
    }
}

