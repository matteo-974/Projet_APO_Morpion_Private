package modele.jeu;

import modele.jeu.Pieces.PionPuissance4;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class JeuPuissance4 extends Jeu {

    private JeuEventListener listener;

    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    public JeuPuissance4(Plateau plateau) {
        super(plateau);
    }

    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseArrivee = premierCoup.getArrivee();
        if (caseArrivee == null) return false;
        int col = caseArrivee.getPosY();

        // Chercher la première case vide en partant du bas
        Case caseLibre = null;
        for (int x = plateau.getSizeX() - 1; x >= 0; x--) {
            Case c = Plateau.getCase(x, col);
            if (c.getPiece() == null) {
                caseLibre = c;
                break;
            }
        }
        if (caseLibre == null) {
            if (listener != null) listener.onCoupInvalide("Colonne pleine !");
            setChanged();
            notifyObservers();
            return false;
        }

        // Placer le pion
        new PionPuissance4(joueurCourant.getCouleur().name(), plateau, caseLibre);
        if (listener != null) listener.onCoupJoue(joueurCourant, caseLibre);

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
        // Vérifier alignement de 4
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        // S'assurer que le tableau des cellules gagnantes existe
        clearWinningCells();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p == null) continue;
                String couleur = p.getCouleur();
                // Horizontal
                if (y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x][y + 1] = true;
                        winningCells[x][y + 2] = true;
                        winningCells[x][y + 3] = true;
                        return true;
                    }
                }
                // Vertical
                if (x <= rows - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x + k, y).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x + 1][y] = true;
                        winningCells[x + 2][y] = true;
                        winningCells[x + 3][y] = true;
                        return true;
                    }
                }
                // Diagonale descendante
                if (x <= rows - 4 && y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x + k, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x + 1][y + 1] = true;
                        winningCells[x + 2][y + 2] = true;
                        winningCells[x + 3][y + 3] = true;
                        return true;
                    }
                }
                // Diagonale montante
                if (x >= 3 && y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x - k, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x - 1][y + 1] = true;
                        winningCells[x - 2][y + 2] = true;
                        winningCells[x - 3][y + 3] = true;
                        return true;
                    }
                }
            }
        }
        // Vérifier si le plateau est plein pour match nul
        boolean plein = true;
        for (int y = 0; y < cols; y++) {
            if (Plateau.getCase(0, y).getPiece() == null) {
                plein = false;
                break;
            }
        }
        if (plein) {
            gagnant = null; // Match nul
            // aucune cellule gagnante
            clearWinningCells();
            return true;
        }
        return false;
    }

    @Override
    protected void afficherPlateauEtTrait() {
        // Intentionnellement vide: l'affichage est géré par la vue/handler.
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
