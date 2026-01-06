package vue;

import audio.SoundManager;
import modele.jeu.Jeu;
import modele.jeu.JeuEventListener;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Gestionnaire console pour Puissance 4.
 * Implémente l'interface d'événements du jeu, affiche la grille avec
 * les numéros de colonnes et joue les sons appropriés.
 */
public class Puissance4ConsoleHandler implements JeuEventListener {

    private final Jeu jeu;

    public Puissance4ConsoleHandler(Jeu jeu) {
        this.jeu = jeu;
        // Afficher le plateau vide dès l'attachement pour que l'utilisateur voie les colonnes
        afficherPlateauAvecTrait();
    }

    @Override
    public void onCoupJoue(Joueur joueur, Case destination) {
        System.out.println("Coup joué: " + joueur.getCouleur().name() +
                " dans la colonne " + (destination != null ? (destination.getPosY() + 1) : "?") +
                ", position (" + (destination != null ? destination.getPosX() : -1) + ", " +
                (destination != null ? destination.getPosY() : -1) + ")");
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

        // En-tête : numéros de colonnes 1..cols
        sb.append("| ");
        for (int y = 0; y < cols; y++) {
            sb.append(y + 1);
            if (y < cols - 1) sb.append("  ");
        }
        sb.append(" |").append(System.lineSeparator());

        // Corps de la grille
        for (int x = 0; x < rows; x++) {
            sb.append("| ");
            for (int y = 0; y < cols; y++) {
                Piece piece = Plateau.getCase(x, y).getPiece();
                String cell;
                if (piece == null) {
                    cell = " ";
                } else {
                    boolean isBlanc = piece.getCouleur() != null && piece.getCouleur().toUpperCase().startsWith("BL");
                    String symbol = isBlanc ? "X" : "O";
                    if (winning != null && winning.length == rows && winning[x][y]) {
                        cell = "(" + symbol + ")";
                    } else {
                        cell = symbol;
                    }
                }
                if (y < cols - 1) {
                    sb.append(cell).append("  ");
                } else {
                    sb.append(cell);
                }
            }
            sb.append(" |").append(System.lineSeparator());
        }

        System.out.print(sb.toString());
    }
}
