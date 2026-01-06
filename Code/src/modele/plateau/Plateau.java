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

    private static Case[][][] cases3D; // Pour jeux 3D
    private static Case[][] cases;     // Pour jeux 2D (compatibilité)
    private static int lignes;
    private static int colonnes;
    private static int couches; // Nombre de couches (dimension Z)
    private static int sizeX;
    private static int sizeY;
    private static int sizeZ;
    private static boolean is3D = false;

    /**
     * Construit un plateau rectangulaire 2D et initialise toutes les cases.
     * @param lignes nombre de lignes (axe X)
     * @param colonnes nombre de colonnes (axe Y)
     */
    public Plateau(int lignes, int colonnes) {
        Plateau.lignes = lignes;
        Plateau.colonnes = colonnes;
        Plateau.couches = 1;
        Plateau.sizeX = lignes;
        Plateau.sizeY = colonnes;
        Plateau.sizeZ = 1;
        Plateau.is3D = false;
        Plateau.cases = new Case[lignes][colonnes];

        // Initialisation effective de chaque case
        for (int x = 0; x < lignes; x++) {
            for (int y = 0; y < colonnes; y++) {
                Plateau.cases[x][y] = new Case(x, y);
            }
        }
    }

    /**
     * Construit un plateau 3D et initialise toutes les cases.
     * @param lignes nombre de lignes (axe X)
     * @param colonnes nombre de colonnes (axe Y)
     * @param couches nombre de couches (axe Z)
     */
    public Plateau(int lignes, int colonnes, int couches) {
        Plateau.lignes = lignes;
        Plateau.colonnes = colonnes;
        Plateau.couches = couches;
        Plateau.sizeX = lignes;
        Plateau.sizeY = colonnes;
        Plateau.sizeZ = couches;
        Plateau.is3D = true;
        Plateau.cases3D = new Case[lignes][colonnes][couches];

        // Initialisation effective de chaque case 3D
        for (int x = 0; x < lignes; x++) {
            for (int y = 0; y < colonnes; y++) {
                for (int z = 0; z < couches; z++) {
                    Plateau.cases3D[x][y][z] = new Case(x, y, z);
                }
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
     * Retourne la case aux coordonnées demandées ou null si hors limites (2D).
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
     * Retourne la case aux coordonnées demandées ou null si hors limites (3D).
     * @param ligne index de ligne (x)
     * @param colonne index de colonne (y)
     * @param couche index de couche (z)
     * @return la case existante ou null si invalide
     */
    public static Case getCase(int ligne, int colonne, int couche) {
        if (!is3D) {
            return null; // Pas de plateau 3D initialisé
        }
        if (ligne < 0 || ligne >= lignes || colonne < 0 || colonne >= colonnes || 
            couche < 0 || couche >= couches) {
            return null;
        }
        return cases3D[ligne][colonne][couche];
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

    /**
     * Nombre de couches du plateau (dimension Z).
     * @return taille Z
     */
    public int getSizeZ(){
        return sizeZ;
    }

    /**
     * Indique si le plateau est en 3D.
     * @return true si 3D, false si 2D
     */
    public boolean is3D() {
        return is3D;
    }
}
