package modele.deplacements;

import java.util.List;

import modele.plateau.Case;

public class DecPuissance4 extends DecCasesAccessibles {
    public DecPuissance4() { 
        super(null);
    }


    @Override
    public List<Case> getMesCA(Case c) {
        return new java.util.ArrayList<>();
    }
}