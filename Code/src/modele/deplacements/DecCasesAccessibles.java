package modele.deplacements;

import modele.plateau.Case;
import java.util.List;

public abstract class DecCasesAccessibles {
    private DecCasesAccessibles base;

    public DecCasesAccessibles(DecCasesAccessibles base) {
        this.base = base;
    }

    // Méthode abstraite avec la case de départ en paramètre
    public abstract List<Case> getMesCA(Case c);

    public List<Case> getCA(Case c){
        List<Case> cA = getMesCA(c);
        if(base != null) {
            List<Case> cB = base.getMesCA(c); // cas de la base
            if (cB != null) {
                cA.addAll(cB);
            }
        }
        return cA;
    }
}
