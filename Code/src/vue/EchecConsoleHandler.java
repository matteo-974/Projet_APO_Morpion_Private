package vue;

import audio.SoundManager;
import modele.jeu.Jeu;
import modele.jeu.JeuEventListener;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Gestionnaire d'affichage console pour le Jeu d'Échecs.
 * <p>
 * Version Utilise des lettres (R, D, T, F, C, P) au lieu des symboles Unicode.
 * Cela garantit un alignement parfait du plateau sur toutes les consoles (Windows/Mac/Linux/IDE).
 * </p>
 */
public class EchecConsoleHandler implements JeuEventListener {

    private final Jeu jeu;

    public EchecConsoleHandler(Jeu jeu) {
        this.jeu = jeu;
        afficherPlateau(); // Afficher le plateau dès le début
    }

    @Override
    public void onCoupJoue(Joueur joueur, Case destination) {
        System.out.println("Coup joué par " + joueur.getCouleur().name());
        SoundManager.playSound("Sounds/move-self.wav");
        afficherPlateau();
    }

    @Override
    public void onCoupInvalide(String raison) {
        System.out.println(">> COUP ILLÉGAL : " + raison);
        SoundManager.playSound("Sounds/illegal.wav");
    }

    @Override
    public void onPartieTerminee(Joueur gagnant) {
        afficherPlateau();
        System.out.println("=== FIN DE PARTIE ===");
        System.out.println("ÉCHEC ET MAT ! Vainqueur : " + gagnant.getCouleur().name());
        SoundManager.playSound("Sounds/game-end.wav");
    }

    @Override
    public void onMatchNul() {
        afficherPlateau();
        System.out.println("=== FIN DE PARTIE ===");
        System.out.println("PAT (Match Nul) !");
        SoundManager.playSound("Sounds/game-end.wav");
    }

    /**
     * Affiche le plateau avec des lettres (Notation Algébrique Française).
     * Blancs en Majuscules, Noirs en Minuscules.
     * Utilise 'D' pour Dame (Reine) afin de distinguer du 'R' de Roi.
     */
    private void afficherPlateau() {
        Plateau p = jeu.getPlateau();
        System.out.println();
        System.out.println("   a b c d e f g h");
        System.out.println("  -----------------");

        for (int x = 0; x < 8; x++) {
            System.out.print((8 - x) + " |"); // Numéro de ligne à gauche
            for (int y = 0; y < 8; y++) {
                Piece piece = Plateau.getCase(x, y).getPiece();
                if (piece == null) {
                    System.out.print(" .");
                } else {
                    String nomClasse = piece.getClass().getSimpleName();
                    char lettre = '?';

                    // Mapping des lettres (Français)
                    switch (nomClasse) {
                        case "Roi":      lettre = 'R'; break;
                        case "Reine":    lettre = 'D'; break; // D pour Dame ! (Crucial)
                        case "Tour":     lettre = 'T'; break;
                        case "Fou":      lettre = 'F'; break;
                        case "Cavalier": lettre = 'C'; break;
                        case "Pion":     lettre = 'P'; break;
                        default:
                            if (nomClasse.length() > 0) lettre = nomClasse.charAt(0);
                    }

                    // Gestion de la couleur (Majuscule = Blanc, Minuscule = Noir)
                    if (piece.getCouleur() != null && piece.getCouleur().equalsIgnoreCase("Blanc")) {
                        lettre = Character.toUpperCase(lettre);
                    } else {
                        lettre = Character.toLowerCase(lettre);
                    }

                    System.out.print(" " + lettre);
                }
            }
            System.out.println(" | " + (8 - x)); // Numéro de ligne à droite
        }
        System.out.println("  -----------------");
        System.out.println("   a b c d e f g h");
        System.out.println();
    }
}