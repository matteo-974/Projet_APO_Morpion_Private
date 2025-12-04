package modele.deplacements;

import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.List;

public class DecDiag extends DecCasesAccessibles {

    public DecDiag(DecCasesAccessibles base) {
        super(base);
    }

    @Override
    public List<Case> getMesCA(Case c) {
        List<Case> casesAccessibles = new ArrayList<>();

        int[][] directions = {
                {-1, 1},  // Haut Droite
                {-1, -1}, // Haut Gauche
                {1, 1},  // Bas Droite
                {1, -1}  // Bas Gauche
        };

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            for (int i = 1; i < 8; i++) {
                // On applique le déplacement à la case actuelle pour obtenir la nouvelle case
                Case nouvelleCase = Plateau.getCasePR(c, new int[]{dx * i, dy * i});

                // Si la case est valide (pas hors du plateau), on l'ajoute à la liste
                if (nouvelleCase != null) {
                    if (nouvelleCase.getPiece() != null) {
                        if(c.getPiece().estAlliee(nouvelleCase.getPiece())) {
                            break;
                        } else {
                            casesAccessibles.add(nouvelleCase);
                            break;
                        }
                    } else {
                        casesAccessibles.add(nouvelleCase);
                    }
                } else {
                    // Si la case est invalide ou un obstacle, on arrête le déplacement dans cette direction
                    break;
                }
            }
        }

        return casesAccessibles;
    }

}
