package modele.jeu;

import audio.SoundManager;
import modele.jeu.Pieces.PionTicTacToe;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class JeuTicTacToe extends Jeu {

    public JeuTicTacToe(Plateau plateau) {
        super(plateau);
    }


    @Override
    public boolean jouerPartie(Coup premierCoup) {
        Coup coup = premierCoup;
        while (!estTermine()) {
            Case caseArrivee = coup.getArrivee();

            // Au TicTacToe, on doit placer une pièce sur une case vide
            if (caseArrivee.getPiece() != null) {
                System.out.println("Cette case est déjà occupée !");
                SoundManager.playSound("Sounds/illegal.wav");
                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }

            // Créer une nouvelle pièce et la placer
            new PionTicTacToe(joueurCourant.getCouleur().name(), plateau, caseArrivee);

            System.out.println("Coup joué: " + joueurCourant.getCouleur().name() + 
                    " à la position (" + caseArrivee.getPosX() + ", " +
                    caseArrivee.getPosY() + ")");
            SoundManager.playSound("Sounds/move-self.wav");

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
                // Afficher la position finale
                afficherPlateauEtTrait();
                System.out.println("Le joueur " + p1.getCouleur() + " a gagné !");
                SoundManager.playSound("Sounds/game-end.wav");
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
                // Afficher la position finale
                afficherPlateauEtTrait();
                System.out.println("Le joueur " + p1.getCouleur() + " a gagné !");
                SoundManager.playSound("Sounds/game-end.wav");
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
            // Afficher la position finale
            afficherPlateauEtTrait();
            System.out.println("Le joueur " + p1.getCouleur() + " a gagné !");
            SoundManager.playSound("Sounds/game-end.wav");
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
            // Afficher la position finale
            afficherPlateauEtTrait();
            System.out.println("Le joueur " + p1.getCouleur() + " a gagné !");
            SoundManager.playSound("Sounds/game-end.wav");
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
            // Afficher la position finale
            afficherPlateauEtTrait();
            System.out.println("Pat ! Match nul.");
            SoundManager.playSound("Sounds/game-end.wav");
            return true;
        }

        return false;
    }





    @Override
    protected void afficherPlateauEtTrait() {
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (int x = 0; x < rows; x++) {
            sb.append("| ");
            for (int y = 0; y < cols; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p == null) {
                    sb.append(n);
                } else {
                    boolean isBlanc = p.getCouleur() != null && p.getCouleur().toUpperCase().startsWith("BL");
                    String symbol = isBlanc ? "X" : "O";
                    if (winningCells != null && winningCells.length == rows && winningCells[x][y]) {
                        sb.append("(").append(symbol).append(")");
                    } else {
                        sb.append(symbol);
                    }
                }
                if (y < cols - 1) sb.append(" ");
                n++;
            }
            sb.append(" |");
            sb.append(System.lineSeparator());
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
