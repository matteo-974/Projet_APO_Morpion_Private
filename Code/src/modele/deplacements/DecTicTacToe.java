package modele.deplacements;

import java.util.List;

import modele.plateau.Case;

public class DecTicTacToe extends DecCasesAccessibles {
    
    public DecTicTacToe() {
        super(null);
    }

    @Override
    public List<Case> getMesCA(Case c) {
        return new java.util.ArrayList<>();
    }
}