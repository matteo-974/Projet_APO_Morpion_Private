package vue;

import modele.jeu.Coup;
import modele.jeu.JeuTicTacToe3D;
import modele.jeu.Piece;
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
            
            // Afficher l'aperçu du coup et demander confirmation
            if (!confirmerCoup(coup)) {
                // L'utilisateur a annulé, on continue la boucle sans jouer le coup
                System.out.println("Coup annulé. À vous de rejouer.\n");
                continue;
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
        return new Coup(null, arrivee);
    }

    /**
     * Affiche un aperçu du coup avec le symbole entre chevrons (>X<) et demande confirmation.
     * 
     * @param coup Le coup à confirmer.
     * @return true si l'utilisateur confirme (y), false sinon (n).
     */
    private boolean confirmerCoup(Coup coup) {
        // Afficher l'aperçu du coup
        System.out.println("\n=== APERÇU DE VOTRE COUP ===");
        afficherApercuTicTacToe3D(coup);
        
        // Demander confirmation
        while (true) {
            System.out.print("Voulez-vous jouer ce coup ? (y/n) : ");
            String reponse = scanner.nextLine().trim().toLowerCase();
            
            if (reponse.equals("y") || reponse.equals("yes") || reponse.equals("oui") || reponse.equals("o")) {
                return true;
            } else if (reponse.equals("n") || reponse.equals("no") || reponse.equals("non")) {
                return false;
            } else {
                System.out.println("Réponse invalide. Veuillez entrer 'y' pour oui ou 'n' pour non.");
            }
        }
    }

    /**
     * Affiche un aperçu pour le TicTacToe3D avec le symbole entre chevrons.
     */
    private void afficherApercuTicTacToe3D(Coup coup) {
        boolean[][][] winning = jeu.getWinningCells3D();
        
        // Déterminer le symbole du joueur courant
        boolean isBlanc = jeu.getJoueurCourant().getCouleur().name().equalsIgnoreCase("BLANC");
        String symbol = isBlanc ? "X" : "O";
        
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
                    
                    // Si c'est la case du coup à jouer, afficher avec chevrons
                    if (coup.getArrivee().equals(currentCase)) {
                        sb.append(">").append(symbol).append("<");
                    } else if (piece == null) {
                        // Afficher la notation (a1-a9, b1-b9, c1-c9)
                        String notation = getCaseNotation3D(currentCase);
                        sb.append(notation);
                        // Padding pour alignement
                        if (notation.length() == 2) sb.append(" ");
                    } else {
                        boolean isPieceBlanc = piece.getCouleur() != null && 
                                        piece.getCouleur().toUpperCase().startsWith("BL");
                        String pieceSymbol = isPieceBlanc ? "X" : "O";
                        
                        // Vérifier si cette case est gagnante
                        boolean isWinning = winning != null && winning[x][y][z];
                        if (isWinning) {
                            sb.append("(").append(pieceSymbol).append(")");
                        } else {
                            sb.append(" ").append(pieceSymbol).append(" ");
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
        
        System.out.print(sb.toString());
    }

    /**
     * Convertit une case en notation 3D (a1-a9, b1-b9, c1-c9).
     */
    private String getCaseNotation3D(Case c) {
        int z = c.getPosZ();
        int x = c.getPosX();
        int y = c.getPosY();
        char layer = (char) ('a' + z);
        int number = x * 3 + y + 1;
        return "" + layer + number;
    }
}
