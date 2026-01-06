package modele.deplacements;

import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de déplacement en L (type cavalier aux échecs).
 * <p>
 * Génère les cases accessibles à un cavalier depuis une case donnée sans traverser
 * d’autres pièces.
 * </p>
 */
public class DecCava extends DecCasesAccessibles {

    public DecCava(DecCasesAccessibles base) {
        super(base);
    }

    @Override
    public List<Case> getMesCA(Case c) {
        List<Case> casesAccessibles = new ArrayList<>();

        int[][] directions = {
                {-2, -1}, {-1, -2}, //Haut Gauche
                {-2, 1}, {-1, 2}, //Haut Droite
                {2, -1}, {1, -2}, //Bas Gauche
                {2, 1}, {1, 2} //Bas Droite
        };

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            Case nouvelleCase = Plateau.getCasePR(c, new int[]{dx, dy});

            // Si la case est valide (pas hors du plateau), on l'ajoute à la liste
            if (nouvelleCase != null) {
                if (nouvelleCase.getPiece() != null) {
                    if (!c.getPiece().estAlliee(nouvelleCase.getPiece())) {
                        casesAccessibles.add(nouvelleCase);
                    }
                } else {
                    casesAccessibles.add(nouvelleCase);
                }
            }
        }

        return casesAccessibles;
    }

}
