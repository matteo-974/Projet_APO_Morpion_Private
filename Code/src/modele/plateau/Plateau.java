package modele.plateau;

import java.util.Observable;


public class Plateau extends Observable {

    private static Case[][] cases;
    private static int lignes;
    private static int colonnes;
    private static int sizeX;
    private static int sizeY;

    public Plateau(int lignes, int colonnes) {
        Plateau.lignes = lignes;
        Plateau.colonnes = colonnes;
        Plateau.sizeX = lignes;
        Plateau.sizeY = colonnes;
        Plateau.cases = new Case[lignes][colonnes];

        // Initialisation effective de chaque case
        for (int x = 0; x < lignes; x++) {
            for (int y = 0; y < colonnes; y++) {
                Plateau.cases[x][y] = new Case(x, y);
            }
        }
    }

    public static boolean estDansLimites(int x, int y) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
    }


    public static Case getCase(int ligne, int colonne) {
        if (ligne < 0 || ligne >= lignes || colonne < 0 || colonne >= colonnes) {
            return null;
        }
        return cases[ligne][colonne];
    }


    public static Case getCasePR(Case c, int[] motifDep) {
        int newX = c.getPosX() + motifDep[0];
        int newY = c.getPosY() + motifDep[1];

        if (estDansLimites(newX, newY)) {
            return getCase(newX, newY);
        }
        return null;
    }


    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY(){
        return sizeY;
    }
}
