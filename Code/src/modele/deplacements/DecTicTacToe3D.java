package modele.deplacements;

import java.util.List;

import modele.plateau.Case;

/**
 * Stratégie de déplacements pour Tic-Tac-Toe 3D.
 * <p>
 * Aucun déplacement n'est calculé car les coups sont la pose de pions
 * gérée par la vue/contrôleur.
 * </p>
 */
public class DecTicTacToe3D extends DecCasesAccessibles {
    
    public DecTicTacToe3D() {
        super(null);
    }

    @Override
    public List<Case> getMesCA(Case c) {
        return new java.util.ArrayList<>();
    }
}
