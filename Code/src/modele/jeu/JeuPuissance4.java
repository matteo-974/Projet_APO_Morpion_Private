package modele.jeu;

import audio.SoundManager;
import modele.jeu.Pieces.PionPuissance4;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class JeuPuissance4 extends Jeu {

    public JeuPuissance4(Plateau plateau) {
        super(plateau);
    }



    @Override
    public boolean jouerPartie(Coup premierCoup) {
        Coup coup = premierCoup;
        while (!estTermine()) {
            Case caseArrivee = coup.getArrivee();
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
                System.out.println("Colonne pleine !");
                SoundManager.playSound("Sounds/illegal.wav");
                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }
            // Placer le pion
            new PionPuissance4(joueurCourant.getCouleur().name(), plateau, caseLibre);
            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
            setChanged();
            notifyObservers();

            // Afficher le plateau mis à jour et indiquer le trait au joueur suivant
            afficherPlateauEtTrait();

            coup = getCoup();
        }
        return false;
    }

    @Override
    public boolean estTermine() {
        // Vérifier alignement de 4
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
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
                        // marquer les 4 cellules gagnantes
                        clearWinningCells();
                        winningCells[x][y] = true;
                        winningCells[x][y + 1] = true;
                        winningCells[x][y + 2] = true;
                        winningCells[x][y + 3] = true;

                        // Afficher la position finale puis annoncer le gagnant
                        afficherPlateauEtTrait();
                        System.out.println("Le joueur " + couleur + " a gagné !");
                        SoundManager.playSound("Sounds/game-end.wav");
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
                        clearWinningCells();
                        winningCells[x][y] = true;
                        winningCells[x + 1][y] = true;
                        winningCells[x + 2][y] = true;
                        winningCells[x + 3][y] = true;
                        afficherPlateauEtTrait();
                        System.out.println("Le joueur " + couleur + " a gagné !");
                        SoundManager.playSound("Sounds/game-end.wav");
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
                        clearWinningCells();
                        winningCells[x][y] = true;
                        winningCells[x + 1][y + 1] = true;
                        winningCells[x + 2][y + 2] = true;
                        winningCells[x + 3][y + 3] = true;
                        afficherPlateauEtTrait();
                        System.out.println("Le joueur " + couleur + " a gagné !");
                        SoundManager.playSound("Sounds/game-end.wav");
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
                        clearWinningCells();
                        winningCells[x][y] = true;
                        winningCells[x - 1][y + 1] = true;
                        winningCells[x - 2][y + 2] = true;
                        winningCells[x - 3][y + 3] = true;
                        afficherPlateauEtTrait();
                        System.out.println("Le joueur " + couleur + " a gagné !");
                        SoundManager.playSound("Sounds/game-end.wav");
                        return true;
                    }
                }
            }
        }
        // Vérifier si le plateau est plein
        boolean plein = true;
        for (int y = 0; y < cols; y++) {
            if (Plateau.getCase(0, y).getPiece() == null) {
                plein = false;
                break;
            }
        }
        if (plein) {
            gagnant = null; // Match nul
            // Afficher la position finale et annoncer le match nul
            clearWinningCells();
            afficherPlateauEtTrait();
            System.out.println("Pat ! Match nul.");
            SoundManager.playSound("Sounds/game-end.wav");
        }
        return plein;
    }




    @Override
    protected void afficherPlateauEtTrait() {
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        StringBuilder sb = new StringBuilder();

        // En-tête : numéros de colonnes
        sb.append("| ");
        for (int y = 0; y < cols; y++) {
            sb.append(y + 1);
            if (y < cols - 1) sb.append("  "); // deux espaces entre les numéros
        }
        sb.append(" |").append(System.lineSeparator());

        // Lignes du plateau (vides ou avec X/O). On affiche chaque cellule large pour donner une grille aérée.
        for (int x = 0; x < rows; x++) {
            sb.append("| ");
            for (int y = 0; y < cols; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                String cell;
                if (p == null) {
                    cell = " ";
                } else {
                    boolean isBlanc = p.getCouleur() != null && p.getCouleur().toUpperCase().startsWith("BL");
                    String symbol = isBlanc ? "X" : "O";
                    if (winningCells != null && winningCells.length == rows && winningCells[x][y]) {
                        cell = "(" + symbol + ")";
                    } else {
                        cell = symbol;
                    }
                }
                // espace entre colonnes pour aérer la grille
                sb.append(cell);
                if (y < cols - 1) sb.append("  ");
            }
            sb.append(" |").append(System.lineSeparator());
        }

        System.out.print(sb.toString());
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
