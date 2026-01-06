package modele.deplacements;

import modele.jeu.Pieces.PiecesEchec.Roi;
import modele.jeu.Pieces.PiecesEchec.Tour;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de déplacement du roi (échecs).
 * <p>
 * Le roi se déplace d’une case dans toutes les directions et gère ici les
 * mouvements spéciaux de roque (petit et grand) sous conditions.
 * </p>
 */
public class DecRoi extends DecCasesAccessibles {

    public DecRoi(DecCasesAccessibles base) {
        super(base);
    }

    @Override
    public List<Case> getMesCA(Case c) {
        List<Case> casesAccessibles = new ArrayList<>();

        int[][] directions = {
                {0, 1},  // Droite
                {0, -1}, // Gauche
                {1, 0},  // Bas
                {-1, 0},  // Haut
                {-1, 1},  // Haut Droite
                {-1, -1}, // Haut Gauche
                {1, 1},  // Bas Droite
                {1, -1}  // Bas Gauche
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

        // Roque du Roi
        // Petit Roque
        if (c.getPosY() == 4 && c.getPiece() instanceof Roi roi && !roi.getADejaBouge()) {
            Case tourDroite = Plateau.getCasePR(c, new int[]{0, 3});  // La case de la tour à droite (case 7,1)
            if (tourDroite != null && tourDroite.getPiece() instanceof Tour) {
                Tour tour = (Tour) tourDroite.getPiece();
                if(!tour.getADejaBouge()){
                    // Vérifie qu'il n'y a pas de pièces entre le roi et la tour à droite
                    Case case1 = Plateau.getCasePR(c, new int[]{0, 1});
                    Case case2 = Plateau.getCasePR(c, new int[]{0, 2});

                    if (case1 != null && case2 != null && case1.getPiece() == null && case2.getPiece() == null) {
                        casesAccessibles.add(case2);
                    }
                }
            }
        }

        // Grand Roque
        if (c.getPosY() == 4 && c.getPiece() instanceof Roi roi && !roi.getADejaBouge()) {
            Case tourGauche = Plateau.getCasePR(c, new int[]{0, -4});
            if (tourGauche != null && tourGauche.getPiece() instanceof Tour) {
                Tour tour = (Tour) tourGauche.getPiece();
                if(!tour.getADejaBouge()) {
                    // Vérifie qu'il n'y a pas de pièces entre le roi et la tour à gauche
                    Case case1 = Plateau.getCasePR(c, new int[]{0, -1});
                    Case case2 = Plateau.getCasePR(c, new int[]{0, -2});
                    Case case3 = Plateau.getCasePR(c, new int[]{0, -3});

                    if (case1 != null && case2 != null && case3 != null &&
                            case1.getPiece() == null && case2.getPiece() == null && case3.getPiece() == null) {
                        casesAccessibles.add(case2);
                        }
                }
            }
        }

        return casesAccessibles;
    }

}
