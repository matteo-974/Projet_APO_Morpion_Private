package modele.jeu;

import modele.jeu.Pieces.PionTicTacToe;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Jeu de TicTacToe 3D : 3 grilles 3x3 représentant un cube.
 * - Grille 1 (couche 0, z=0): a1-a9
 * - Grille 2 (couche 1, z=1): b1-b9
 * - Grille 3 (couche 2, z=2): c1-c9
 */
public class JeuTicTacToe3D extends Jeu {

    private JeuEventListener listener;

    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    public JeuTicTacToe3D(Plateau plateau) {
        super(plateau);
    }

    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseArrivee = premierCoup.getArrivee();

        // Vérifier que la case existe et est vide
        if (caseArrivee == null || caseArrivee.getPiece() != null) {
            if (listener != null) listener.onCoupInvalide("Cette case est déjà occupée !");
            setChanged();
            notifyObservers();
            return false;
        }

        // Placer le pion
        new PionTicTacToe(joueurCourant.getCouleur().name(), plateau, caseArrivee);

        if (listener != null) listener.onCoupJoue(joueurCourant, caseArrivee);

        // Vérifier fin de partie
        boolean fin = estTermine();
        if (fin) {
            if (gagnant != null) {
                if (listener != null) listener.onPartieTerminee(gagnant);
            } else {
                if (listener != null) listener.onMatchNul();
            }
        } else {
            // Changer de joueur
            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
        }

        setChanged();
        notifyObservers();
        return true;
    }

    @Override
    public boolean estTermine() {
        clearWinningCells();
        
        // Vérifier toutes les conditions de victoire
        
        // 1. Victoires dans chaque grille (lignes, colonnes, diagonales)
        for (int z = 0; z < 3; z++) {
            if (verifierGrille(z)) return true;
        }
        
        // 2. Victoires verticales (même position x,y sur différentes couches z)
        if (verifierColonnesVerticales()) return true;
        
        // 3. Diagonales inter-grilles sur les faces du cube
        if (verifierDiagonalesInterGrillesFaces()) return true;
        
        // 4. Diagonales principales du cube (4 diagonales traversant tout le cube)
        if (verifierDiagonalesPrincipalesCube()) return true;

        // Vérifier si le plateau est plein (match nul)
        boolean plateauPlein = true;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (Plateau.getCase(x, y, z).getPiece() == null) {
                        plateauPlein = false;
                        break;
                    }
                }
                if (!plateauPlein) break;
            }
            if (!plateauPlein) break;
        }

        if (plateauPlein) {
            gagnant = null;
            clearWinningCells();
            return true;
        }

        return false;
    }

    /**
     * Vérifie les victoires dans une grille donnée (lignes, colonnes, diagonales)
     */
    private boolean verifierGrille(int z) {
        // Lignes
        for (int x = 0; x < 3; x++) {
            if (verifierAlignement(
                Plateau.getCase(x, 0, z),
                Plateau.getCase(x, 1, z),
                Plateau.getCase(x, 2, z)
            )) return true;
        }

        // Colonnes
        for (int y = 0; y < 3; y++) {
            if (verifierAlignement(
                Plateau.getCase(0, y, z),
                Plateau.getCase(1, y, z),
                Plateau.getCase(2, y, z)
            )) return true;
        }

        // Diagonale principale
        if (verifierAlignement(
            Plateau.getCase(0, 0, z),
            Plateau.getCase(1, 1, z),
            Plateau.getCase(2, 2, z)
        )) return true;

        // Diagonale secondaire
        if (verifierAlignement(
            Plateau.getCase(0, 2, z),
            Plateau.getCase(1, 1, z),
            Plateau.getCase(2, 0, z)
        )) return true;

        return false;
    }

    /**
     * Vérifie les colonnes verticales (même x,y, différents z)
     */
    private boolean verifierColonnesVerticales() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (verifierAlignement(
                    Plateau.getCase(x, y, 0),
                    Plateau.getCase(x, y, 1),
                    Plateau.getCase(x, y, 2)
                )) return true;
            }
        }
        return false;
    }

    /**
     * Vérifie les diagonales inter-grilles sur les 4 faces du cube
     */
    private boolean verifierDiagonalesInterGrillesFaces() {
        // Face XZ (y constant) - 6 diagonales (3 valeurs de y × 2 diagonales)
        for (int y = 0; y < 3; y++) {
            // Diagonale descendante
            if (verifierAlignement(
                Plateau.getCase(0, y, 0),
                Plateau.getCase(1, y, 1),
                Plateau.getCase(2, y, 2)
            )) return true;
            
            // Diagonale montante
            if (verifierAlignement(
                Plateau.getCase(0, y, 2),
                Plateau.getCase(1, y, 1),
                Plateau.getCase(2, y, 0)
            )) return true;
        }

        // Face YZ (x constant) - 6 diagonales (3 valeurs de x × 2 diagonales)
        for (int x = 0; x < 3; x++) {
            // Diagonale descendante
            if (verifierAlignement(
                Plateau.getCase(x, 0, 0),
                Plateau.getCase(x, 1, 1),
                Plateau.getCase(x, 2, 2)
            )) return true;
            
            // Diagonale montante
            if (verifierAlignement(
                Plateau.getCase(x, 0, 2),
                Plateau.getCase(x, 1, 1),
                Plateau.getCase(x, 2, 0)
            )) return true;
        }

        return false;
    }

    /**
     * Vérifie les 4 diagonales principales du cube (d'un coin à l'opposé)
     */
    private boolean verifierDiagonalesPrincipalesCube() {
        // Diagonale 1 : (0,0,0) -> (1,1,1) -> (2,2,2)
        if (verifierAlignement(
            Plateau.getCase(0, 0, 0),
            Plateau.getCase(1, 1, 1),
            Plateau.getCase(2, 2, 2)
        )) return true;

        // Diagonale 2 : (0,0,2) -> (1,1,1) -> (2,2,0)
        if (verifierAlignement(
            Plateau.getCase(0, 0, 2),
            Plateau.getCase(1, 1, 1),
            Plateau.getCase(2, 2, 0)
        )) return true;

        // Diagonale 3 : (0,2,0) -> (1,1,1) -> (2,0,2)
        if (verifierAlignement(
            Plateau.getCase(0, 2, 0),
            Plateau.getCase(1, 1, 1),
            Plateau.getCase(2, 0, 2)
        )) return true;

        // Diagonale 4 : (0,2,2) -> (1,1,1) -> (2,0,0)
        if (verifierAlignement(
            Plateau.getCase(0, 2, 2),
            Plateau.getCase(1, 1, 1),
            Plateau.getCase(2, 0, 0)
        )) return true;

        return false;
    }

    /**
     * Vérifie si 3 cases contiennent des pièces de même couleur
     */
    private boolean verifierAlignement(Case c1, Case c2, Case c3) {
        Piece p1 = c1.getPiece();
        Piece p2 = c2.getPiece();
        Piece p3 = c3.getPiece();

        if (p1 != null && p2 != null && p3 != null &&
            p1.getCouleur().equals(p2.getCouleur()) && 
            p2.getCouleur().equals(p3.getCouleur())) {
            gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
            // Marquer les cases gagnantes
            if (winningCells3D != null) {
                winningCells3D[c1.getPosX()][c1.getPosY()][c1.getPosZ()] = true;
                winningCells3D[c2.getPosX()][c2.getPosY()][c2.getPosZ()] = true;
                winningCells3D[c3.getPosX()][c3.getPosY()][c3.getPosZ()] = true;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void afficherPlateauEtTrait() {
        // Géré par la vue console
    }

    @Override
    public void reinitialiserPartie() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    Plateau.getCase(x, y, z).setPiece(null);
                }
            }
        }
        clearWinningCells();
        gagnant = null;
        joueurCourant = JOUEUR_BLANC;
        setChanged();
        notifyObservers();
    }
}
