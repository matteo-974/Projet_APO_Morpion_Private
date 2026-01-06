package modele.deplacements;

import java.util.List;

import modele.plateau.Case;

/**
 * Stratégie de déplacements pour le jeu Puissance 4.
 * <p>
 * Classe actuellement factice qui ne renvoie aucune case accessible (le pion tombe
 * par gravité dans la colonne choisie via la vue/contrôleur).
 * </p>
 */
public class DecPuissance4 extends DecCasesAccessibles {
    public DecPuissance4() { 
        super(null);
    }


    @Override
    public List<Case> getMesCA(Case c) {
        return new java.util.ArrayList<>();
    }
}