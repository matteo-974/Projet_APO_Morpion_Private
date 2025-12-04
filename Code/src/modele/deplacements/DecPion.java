package modele.deplacements;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.jeu.Piece;
import modele.jeu.Pieces.PiecesEchec.Pion;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.List;

public class DecPion extends DecCasesAccessibles {

    public DecPion(DecCasesAccessibles base) {
        super(base);
    }

    private Case caseAPrendreEnPassant = null;

    public Case getCaseAPrendreEnPassant() {
        return caseAPrendreEnPassant;
    }


    @Override
    public List<Case> getMesCA(Case c) {
        List<Case> casesAccessibles = new ArrayList<>();
        Piece pion = c.getPiece();
        String couleur = pion.getCouleur();
        int dir;

        caseAPrendreEnPassant = null;

        Coup dernierCoup = Jeu.getInstance().getDernierCoup();

        if (couleur.equals("Blanc")) {
            dir = -1;
        } else{
            dir = 1;
        }
        Case nouvelleCase = Plateau.getCasePR(c, new int[]{dir, 0});
        Case nouvelleCaseBis = Plateau.getCasePR(c, new int[]{dir*2, 0});

        //Mouvement d'une case
        if (nouvelleCase != null) {
            if (nouvelleCase.getPiece() == null) {
                casesAccessibles.add(nouvelleCase);

                //Mouvement de 2 cases au premier tour
                int ligneDepart = couleur.equals("Blanc") ? 6 : 1;
                if (c.getPosX() == ligneDepart) {
                    if(nouvelleCaseBis.getPiece() == null) {
                        casesAccessibles.add(nouvelleCaseBis);
                    }
                }
            }
        }

        //Prise en diagonale
        for (int i = -1; i < 2; i+=2){
            nouvelleCase = Plateau.getCasePR(c, new int[]{dir, i});
            if (nouvelleCase != null) {
                if (nouvelleCase.getPiece() != null) {
                    if (!c.getPiece().estAlliee(nouvelleCase.getPiece())) {
                        casesAccessibles.add(nouvelleCase);
                    }
                }
            }
        }

        //Prise en passant
        if (dernierCoup != null) {
            Case dep = dernierCoup.getDepart();
            Case arr = dernierCoup.getArrivee();
            Piece piece = arr.getPiece();

            if (piece instanceof Pion &&
                    Math.abs(arr.getPosX() - dep.getPosX()) == 2 &&
                    arr.getPosX() == c.getPosX() &&
                    Math.abs(arr.getPosY() - c.getPosY()) == 1) {
                Case caseEnPassant = Plateau.getCasePR(arr, new int[]{dir, 0});
                if (caseEnPassant != null && caseEnPassant.getPiece() == null) {
                    caseAPrendreEnPassant = arr;
                    casesAccessibles.add(caseEnPassant);
                }
            }
        }

        return casesAccessibles;
    }

}
