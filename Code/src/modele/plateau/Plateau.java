package modele.plateau;

import java.util.Observable;


/**
 * Représente le plateau de jeu et fournit des utilitaires d’accès aux cases.
 * <p>
 * Le plateau est stocké sous forme de matrice statique de {@link Case}. Cette classe
 * expose des méthodes pour vérifier les limites et récupérer des cases relatives.
 * </p>
 */
public class Plateau extends Observable {

    private static Case[][] cases;
    private static int lignes;
    private static int colonnes;
    private static int sizeX;
    private static int sizeY;

    /**
     * Construit un plateau rectangulaire et initialise toutes les cases.
     * @param lignes nombre de lignes (axe X)
     * @param colonnes nombre de colonnes (axe Y)
     */
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

    /**
     * Indique si les coordonnées fournies se trouvent à l'intérieur du plateau.
     * @param x ligne visée
     * @param y colonne visée
     * @return true si (x,y) est une case valide, false sinon
     */
    public static boolean estDansLimites(int x, int y) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
    }


    /**
     * Retourne la case aux coordonnées demandées ou null si hors limites.
     * @param ligne index de ligne (x)
     * @param colonne index de colonne (y)
     * @return la case existante ou null si invalide
     */
    public static Case getCase(int ligne, int colonne) {
        if (ligne < 0 || ligne >= lignes || colonne < 0 || colonne >= colonnes) {
            return null;
        }
        return cases[ligne][colonne];
    }


    /**
     * Récupère une case par rapport à une case de référence et un motif de déplacement.
     * @param c case de référence
     * @param motifDep déplacement relatif {dx, dy}
     * @return la case atteinte si valide, sinon null
     */
    public static Case getCasePR(Case c, int[] motifDep) {
        int newX = c.getPosX() + motifDep[0];
        int newY = c.getPosY() + motifDep[1];

        if (estDansLimites(newX, newY)) {
            return getCase(newX, newY);
        }
        return null;
    }


    /**
     * Nombre de lignes du plateau (dimension X).
     * @return taille X
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Nombre de colonnes du plateau (dimension Y).
     * @return taille Y
     */
    public int getSizeY(){
        return sizeY;
    }
}
