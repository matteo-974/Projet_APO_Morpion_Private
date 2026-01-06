package vue;

import audio.SoundManager;
import modele.jeu.Jeu;
import modele.jeu.JeuEventListener;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Gestionnaire console pour TicTacToe.
 * Implémente l'interface d'événements du jeu et se charge
 * d'afficher les informations et jouer les sons.
 */
public class TicTacToeConsoleHandler implements JeuEventListener {

    private final Jeu jeu; // pour accéder au plateau et aux cellules gagnantes

    public TicTacToeConsoleHandler(Jeu jeu) {
        this.jeu = jeu;
        // AJOUT : On affiche le plateau vide dès le début pour que le joueur voie la grille 1-9
        afficherPlateauAvecTrait();
    }

    @Override
    public void onCoupJoue(Joueur joueur, Case destination) {
        System.out.println("Coup joué: " + joueur.getCouleur().name() +
                " à la position (" + destination.getPosX() + ", " + destination.getPosY() + ")");
        SoundManager.playSound("Sounds/move-self.wav");
        afficherPlateauAvecTrait();
    }

    @Override
    public void onCoupInvalide(String raison) {
        System.out.println(raison);
        SoundManager.playSound("Sounds/illegal.wav");
    }

    @Override
    public void onPartieTerminee(Joueur gagnant) {
        afficherPlateauAvecTrait();
        System.out.println("Le joueur " + (gagnant != null ? gagnant.getCouleur().name() : "?") + " a gagné !");
        SoundManager.playSound("Sounds/game-end.wav");
    }

    @Override
    public void onMatchNul() {
        afficherPlateauAvecTrait();
        System.out.println("Pat ! Match nul.");
        SoundManager.playSound("Sounds/game-end.wav");
    }

    private void afficherPlateauAvecTrait() {
        Plateau p = jeu.getPlateau();
        int rows = p.getSizeX();
        int cols = p.getSizeY();
        boolean[][] winning = jeu.getWinningCells();
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (int x = 0; x < rows; x++) {
            sb.append("| ");
            for (int y = 0; y < cols; y++) {
                Piece piece = Plateau.getCase(x, y).getPiece();
                if (piece == null) {
                    sb.append(n);
                } else {
                    boolean isBlanc = piece.getCouleur() != null && piece.getCouleur().toUpperCase().startsWith("BL");
                    String symbol = isBlanc ? "X" : "O";
                    if (winning != null && winning.length == rows && winning[x][y]) {
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
}
