package lanceur;

import java.util.Scanner;

import javax.swing.SwingUtilities;

import modele.jeu.Jeu;
import modele.jeu.JeuEchec;
import modele.jeu.JeuPuissance4;
import modele.jeu.JeuTicTacToe;
import modele.jeu.JeuTicTacToe3D;
import modele.plateau.Plateau;
import vue.VueConsole;
import vue.VueTicTacToe3DConsole;
import vue.Fenetres.FenetreMenuPrincipal;

/**
 * Point d'entrée de l’application.
 * <p>
 * Propose le choix du mode d’exécution (console ou graphique) puis délègue
 * l’initialisation aux composants concernés.
 * </p>
 */
public class Main {


    /**
     * Gère le menu de sélection du jeu en mode Console.
     * @param scanner Le Scanner pour lire l'entrée utilisateur.
     * @return Une instance concrète de la classe Jeu sélectionnée.
     */
    private static Jeu choisirJeuConsole(Scanner scanner) {
        
        System.out.println("\n------------------------------------------");
        System.out.println("Choisir le jeu à lancer :");
        System.out.println("  1. TicTacToe");
        System.out.println("  2. Puissance4");
        System.out.println("  3. TicTacToe 3D");
        System.out.println("  4. Échecs");
        System.out.println("------------------------------------------");
        System.out.print("Choix : ");
        
        String choixJeu = scanner.nextLine().trim();

        switch (choixJeu) {
            case "1":
                Plateau plateauTicTacToe = new Plateau(3, 3);
                return new JeuTicTacToe(plateauTicTacToe);
            case "2":
                Plateau plateauPuissance4 = new Plateau(6, 7);
                return new JeuPuissance4(plateauPuissance4);
            case "3":
                // TicTacToe 3D avec 3 grilles 3x3 (3 couches)
                Plateau plateauTicTacToe3D = new Plateau(3, 3, 3);
                return new JeuTicTacToe3D(plateauTicTacToe3D);
            case "4":
                Plateau plateauEchec = new Plateau(8, 8);
                return new JeuEchec(plateauEchec);
            default:
                System.err.println("Choix de jeu invalide.");
                return null; // Retourne null si le choix est mauvais
        }
    }


    public static void main(String[] args) {
        
        // Un Scanner temporaire pour le choix initial du mode.
        // On suppose ici que la classe 'Input' de votre projet n'est pas encore initialisée
        // ou qu'elle est spécifique au mode Console.
        Scanner scanner = new Scanner(System.in);

        try{
            
            System.out.println("==========================================");
            System.out.println("Choisir le mode de fonctionnement:");
            System.out.println("  1. Mode console");
            System.out.println("  2. Mode graphique");
            System.out.println("  3. Quitter");
            System.out.println("==========================================");
            System.out.print("Choix : ");
            
            String choix = scanner.nextLine().trim();
            
            switch (choix) {
                case "1":
                    // Délègue entièrement le contrôle au gestionnaire du mode Console
                    Jeu jeuConsole = choisirJeuConsole(scanner); 
                    
                    if (jeuConsole != null) {
                        System.out.println("Lancement de la vue Console...");
                        
                        // Gestion spéciale pour TicTacToe3D
                        if (jeuConsole instanceof JeuTicTacToe3D) {
                            new VueTicTacToe3DConsole((JeuTicTacToe3D) jeuConsole, scanner);
                        } else {
                            new VueConsole(jeuConsole, scanner);
                        }
                    } else {
                        System.out.println("Le programme s'arrête en raison d'un choix de jeu invalide.");
                    }
                    break;

                case "2":
                    // Délègue entièrement le contrôle au gestionnaire du mode Graphique
                    System.out.println("Mode Graphique lancé.");
                    SwingUtilities.invokeLater(() -> {
                        // Création d'une instance de la fenêtre principale
                        FenetreMenuPrincipal menuPrincipal = new FenetreMenuPrincipal();
                        menuPrincipal.setVisible(true);
                    });
                    break;

                case "3":
                    System.out.println("Fermeture du programme.");
                    break;

                default:
                    System.out.println("Choix invalide. Le programme va s'arrêter.");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }
}