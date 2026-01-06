package vue;

import modele.jeu.Coup;
import modele.jeu.JeuTicTacToe3D;
import modele.plateau.Case;
import modele.plateau.Plateau;
import java.util.Scanner;

/**
 * Vue Console pour le TicTacToe 3D.
 * Gère l'affichage des 3 grilles et la saisie des coups au format a1-c9.
 */
public class VueTicTacToe3DConsole implements Runnable {
    private JeuTicTacToe3D jeu;
    private Scanner scanner;
    private TicTacToe3DConsoleHandler handler;
    
    public VueTicTacToe3DConsole(JeuTicTacToe3D jeu, Scanner sharedScanner) {
        this.jeu = jeu;
        this.scanner = sharedScanner;
        
        // Attacher le gestionnaire d'affichage
        this.handler = new TicTacToe3DConsoleHandler(jeu);
        jeu.setEventListener(handler);
        
        new Thread(this).start();
        
        System.out.println("Vue Console TicTacToe 3D démarrée !");
    }

    @Override
    public void run() {
        while (true) {
            if (jeu.estTermine()) {
                afficherFinPartie();
                break;
            }

            Coup coup = demanderCoup();
            
            if (coup == null) {
                System.out.println("Fin de l'interaction console.");
                break; 
            }
            
            jeu.setCoup(coup);
        }
    }

    private void afficherFinPartie() {
        System.out.println("\n========== PARTIE TERMINÉE ==========");
        if (jeu.getGagnant() != null) {
            jeu.getGagnant().ajouterPoint();
            System.out.println("Le gagnant est : " + jeu.getGagnant().getCouleur());
        } else {
            System.out.println("Match nul !");
        }
        System.out.println("Score: " + jeu.getJoueurBlanc().getPoints() + " - " + 
                          jeu.getJoueurNoir().getPoints());
        System.out.println("=====================================\n");
    }

    /**
     * Demande un coup à l'utilisateur au format a1-c9
     */
    private Coup demanderCoup() {
        Plateau plateau = jeu.getPlateau();
        
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quitter")) {
                return null; 
            }

            try {
                return parseCoupTicTacToe3D(input, plateau);
            } catch (IllegalArgumentException e) {
                System.out.println("Coup invalide : " + e.getMessage());
                System.out.print("Réessayez (ex: a1, b5, c9): ");
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
                System.out.print("Réessayez (ex: a1, b5, c9): ");
            }
        }
    }

    /**
     * Parse un coup au format a1-c9 (lettre pour la couche, chiffre pour la position)
     * a1-a9 : couche 0 (z=0)
     * b1-b9 : couche 1 (z=1)
     * c1-c9 : couche 2 (z=2)
     */
    private Coup parseCoupTicTacToe3D(String input, Plateau plateau) {
        // Valider le format
        if (input.length() < 2 || input.length() > 2) {
            throw new IllegalArgumentException("Format invalide. Utilisez: a1-a9, b1-b9, ou c1-c9");
        }
        
        char layer = input.charAt(0);
        char numChar = input.charAt(1);
        
        // Valider la couche (a, b, ou c)
        if (layer < 'a' || layer > 'c') {
            throw new IllegalArgumentException("La couche doit être 'a', 'b', ou 'c'");
        }
        
        // Valider le numéro (1-9)
        if (numChar < '1' || numChar > '9') {
            throw new IllegalArgumentException("Le numéro doit être entre 1 et 9");
        }
        
        int z = layer - 'a'; // Convertir a->0, b->1, c->2
        int num = numChar - '0'; // Convertir caractère en chiffre
        
        // Convertir le numéro (1-9) en coordonnées x,y dans une grille 3x3
        int indice = num - 1; // Convertir 1-9 en 0-8
        int x = indice / 3; // Ligne (0-2)
        int y = indice % 3; // Colonne (0-2)
        
        Case arrivee = Plateau.getCase(x, y, z);
        
        if (arrivee.getPiece() != null) {
            throw new IllegalArgumentException("Cette case est déjà occupée !");
        }
        
        // TicTacToe 3D : Pas de case de départ, juste la case d'arrivée
        return new Coup(arrivee, arrivee);
    }
}
