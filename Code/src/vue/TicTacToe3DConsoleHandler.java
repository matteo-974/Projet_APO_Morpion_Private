package vue;

import audio.SoundManager;
import modele.jeu.Jeu;
import modele.jeu.JeuEventListener;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Gestionnaire console pour TicTacToe 3D.
 * Affiche 3 grilles 3x3 représentant les 3 couches du cube.
 */
public class TicTacToe3DConsoleHandler implements JeuEventListener {

    private final Jeu jeu;

    public TicTacToe3DConsoleHandler(Jeu jeu) {
        this.jeu = jeu;
        afficherPlateauAvecTrait();
    }

    @Override
    public void onCoupJoue(Joueur joueur, Case destination) {
        System.out.println("Coup joué: " + joueur.getCouleur().name() +
                " à la position " + getCaseNotation(destination));
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

    /**
     * Convertit une case en notation (a1-a9, b1-b9, c1-c9)
     */
    private String getCaseNotation(Case c) {
        int z = c.getPosZ();
        int x = c.getPosX();
        int y = c.getPosY();
        char layer = (char) ('a' + z);
        int number = x * 3 + y + 1;
        return "" + layer + number;
    }

    /**
     * Affiche les 3 grilles du cube 3D
     */
    private void afficherPlateauAvecTrait() {
        boolean[][][] winning = jeu.getWinningCells3D();
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n========== TIC-TAC-TOE 3D ==========\n");
        
        // Afficher les 3 grilles (couches)
        char[] layers = {'a', 'b', 'c'};
        
        for (int z = 0; z < 3; z++) {
            sb.append("\nGrille ").append(Character.toUpperCase(layers[z])).append(":\n");
            sb.append("-------------\n");
            
            for (int x = 0; x < 3; x++) {
                sb.append("| ");
                for (int y = 0; y < 3; y++) {
                    Case currentCase = Plateau.getCase(x, y, z);
                    Piece piece = currentCase.getPiece();
                    
                    if (piece == null) {
                        // Afficher la notation (a1-a9, b1-b9, c1-c9)
                        String notation = getCaseNotation(currentCase);
                        sb.append(notation);
                        // Padding pour alignement
                        if (notation.length() == 2) sb.append(" ");
                    } else {
                        boolean isBlanc = piece.getCouleur() != null && 
                                        piece.getCouleur().toUpperCase().startsWith("BL");
                        String symbol = isBlanc ? "X" : "O";
                        
                        // Vérifier si cette case est gagnante
                        boolean isWinning = winning != null && winning[x][y][z];
                        if (isWinning) {
                            sb.append("(").append(symbol).append(")");
                        } else {
                            sb.append(" ").append(symbol).append(" ");
                        }
                    }
                    
                    if (y < 2) sb.append("| ");
                }
                sb.append("|\n");
                
                if (x < 2) {
                    sb.append("|----|----|----|").append("\n");
                } else {
                    sb.append("----------------\n");
                }
            }
        }
        
        sb.append("\n====================================\n");
        sb.append("Légende: X = Blanc, O = Noir\n");
        sb.append("Entrez votre coup (ex: a1, b5, c9): ");
        
        System.out.print(sb.toString());
    }
}
