package modele.jeu;

import modele.jeu.Pieces.PionTicTacToe;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class JeuTicTacToe extends Jeu {

    private JeuEventListener listener;

    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    public JeuTicTacToe(Plateau plateau) {
        super(plateau);
    }


    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseArrivee = premierCoup.getArrivee();

        // Au TicTacToe, on doit placer une pièce sur une case vide
        if (caseArrivee == null || caseArrivee.getPiece() != null) {
            if (listener != null) listener.onCoupInvalide("Cette case est déjà occupée !");
            setChanged();
            notifyObservers();
            return false;
        }

        // Créer une nouvelle pièce et la placer
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
            // Changer de joueur uniquement si la partie continue
            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
        }

        setChanged();
        notifyObservers();
        return true;
    }



    @Override
    public boolean estTermine() {
        clearWinningCells();
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        // Vérifier les lignes
        for (int x = 0; x < rows; x++) {
            Piece p1 = Plateau.getCase(x, 0).getPiece();
            Piece p2 = Plateau.getCase(x, 1).getPiece();
            Piece p3 = Plateau.getCase(x, 2).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                p1.getCouleur().equals(p2.getCouleur()) && 
                p2.getCouleur().equals(p3.getCouleur())) {
                gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                // marquer les cellules gagnantes
                winningCells[x][0] = true;
                winningCells[x][1] = true;
                winningCells[x][2] = true;
                return true;
            }
        }

        // Vérifier les colonnes
        for (int y = 0; y < cols; y++) {
            Piece p1 = Plateau.getCase(0, y).getPiece();
            Piece p2 = Plateau.getCase(1, y).getPiece();
            Piece p3 = Plateau.getCase(2, y).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                p1.getCouleur().equals(p2.getCouleur()) && 
                p2.getCouleur().equals(p3.getCouleur())) {
                gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                // marquer les cellules gagnantes
                winningCells[0][y] = true;
                winningCells[1][y] = true;
                winningCells[2][y] = true;
                return true;
            }
        }

        // Vérifier la diagonale principale
        Piece p1 = Plateau.getCase(0, 0).getPiece();
        Piece p2 = Plateau.getCase(1, 1).getPiece();
        Piece p3 = Plateau.getCase(2, 2).getPiece();

        if (p1 != null && p2 != null && p3 != null &&
            p1.getCouleur().equals(p2.getCouleur()) && 
            p2.getCouleur().equals(p3.getCouleur())) {
            gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
            winningCells[0][0] = true;
            winningCells[1][1] = true;
            winningCells[2][2] = true;
            return true;
        }

        // Vérifier la diagonale secondaire
        p1 = Plateau.getCase(0, 2).getPiece();
        p2 = Plateau.getCase(1, 1).getPiece();
        p3 = Plateau.getCase(2, 0).getPiece();

        if (p1 != null && p2 != null && p3 != null &&
            p1.getCouleur().equals(p2.getCouleur()) && 
            p2.getCouleur().equals(p3.getCouleur())) {
            gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
            winningCells[0][2] = true;
            winningCells[1][1] = true;
            winningCells[2][0] = true;
            return true;
        }

        // Vérifier si le plateau est plein
        boolean plateauPlein = true;
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                if (Plateau.getCase(x, y).getPiece() == null) {
                    plateauPlein = false;
                    break;
                }
            }
            if (!plateauPlein) break;
        }

        if (plateauPlein) {
            gagnant = null; // Match nul
            clearWinningCells();
            return true;
        }

        return false;
    }





    @Override
    protected void afficherPlateauEtTrait() {
        // Intentionnellement vide: la vue/handler s'occupe de l'affichage.
    }




    @Override
    public void reinitialiserPartie() {
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                Plateau.getCase(x, y).setPiece(null);
            }
        }
        clearWinningCells();
        gagnant = null;
        joueurCourant = JOUEUR_BLANC;
        setChanged();
        notifyObservers();
    }
    
}
