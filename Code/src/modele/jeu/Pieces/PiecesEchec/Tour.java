package modele.jeu.Pieces.PiecesEchec;

import modele.deplacements.DecLigne;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Tour extends Piece {
    private boolean aDejaBouge = false;

    public boolean getADejaBouge() {
        return aDejaBouge;
    }

    public void setADejaBouge(boolean aDejaBouge) {
        this.aDejaBouge = aDejaBouge;
    }

    public Tour(String couleur, Plateau plateau, Case c) {
        super(couleur, plateau, c, new DecLigne(null));
    }
}

