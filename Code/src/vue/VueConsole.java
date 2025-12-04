package vue;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.plateau.Plateau;
import java.util.Scanner;

// Importez les classes spécifiques pour vérifier le type
import modele.jeu.JeuTicTacToe;
import modele.jeu.Joueur;
import modele.jeu.JeuPuissance4;
import modele.jeu.JeuEchec; 
import modele.plateau.Case;

public class VueConsole implements Runnable {
    private Jeu jeu;
    private Scanner scanner;
    
    public VueConsole(Jeu jeu, Scanner sharedScanner) {
        this.jeu = jeu;
        this.scanner = sharedScanner; // Utilise le Scanner déjà ouvert par Main
        new Thread(this).start();
        
        System.out.println("Vue Console démarrée pour le jeu : " + jeu.getClass().getSimpleName());
    }

    /**
     * Boucle principale de la Vue Console pour gérer la saisie utilisateur.
     */
    @Override
    public void run() {
        while (true) {
            // Si le jeu est terminé, on arrête la boucle de saisie.
            if (jeu.estTermine()) {
                afficherfinPartie();
                break;
            } else{

                Coup coup = demanderCoup();
                
                if (coup == null) {
                    // Si l'utilisateur a entré 'q' ou quitte la saisie
                    System.out.println("Fin de l'interaction console.");
                    break; 
                }
                
                // Envoie le coup au modèle Jeu (appelle notify() sur le modèle).
                jeu.setCoup(coup);
            }
        }
    }


    private void afficherfinPartie() {
        System.out.println("La partie est terminée !");
        if (jeu.getGagnant() != null) {
            Joueur gagnant = jeu.getGagnant();
            gagnant.ajouterPoint();
            System.out.println("Le gagnant est : " + jeu.getGagnant().getCouleur());
        } else {
            System.out.println("La partie s'est terminée par un match nul.");
        }
        System.out.println("Score: " + jeu.getJoueurBlanc().getPoints() + " - " + jeu.getJoueurNoir().getPoints());
    }

    /**
     * Demande à l'utilisateur d'entrer un coup et le convertit en un objet Coup.
     * Le format d'entrée dépend du type de jeu.
     * @return Le Coup valide créé (avec CaseDepart et CaseArrivee) ou null si erreur/quitter.
     */
    private Coup demanderCoup() {
        Plateau plateau = jeu.getPlateau();
        
        String instruction = "";

        // Définir l'instruction de saisie
        if (jeu instanceof JeuTicTacToe) {
            instruction = "Entrez le numéro de la case où jouer (1-9) ou 'q' pour quitter : ";
        } else if (jeu instanceof JeuPuissance4) {
            instruction = "Entrez le numéro de la colonne où jouer ou 'q' pour quitter :";
        } else if (jeu instanceof JeuEchec) {
            instruction = "Entrez votre coup (ex: a2a4) ou 'q' pour quitter : ";
        } else {
             instruction = "Entrez votre coup ou 'q' pour quitter : ";
        }

        while (true) {
            System.out.print(instruction);
            
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quitter")) {
                return null; 
            }

            try {
                // Traiter l'entrée selon le type de jeu
                if (jeu instanceof JeuTicTacToe) {
                    return parseCoupTicTacToe(input, plateau);
                    
                } else if (jeu instanceof JeuPuissance4) {
                    return parseCoupPuissance4(input, plateau);
                    
                } else if (jeu instanceof JeuEchec) {
                    return parseCoupEchec(input, plateau);
                    
                } else {
                    System.out.println("Type de jeu inconnu. Coup invalide.");
                    return null;
                }
                
            } catch (NumberFormatException e) {
                 System.out.println("Erreur: Veuillez entrer un NUMÉRO valide.");
            } catch (IllegalArgumentException e) {
                // Capturer les erreurs des méthodes de parsing (ex: case déjà prise, format erroné)
                System.out.println("Coup invalide : " + e.getMessage() + ". Réessayez.");
            } catch (Exception e) {
                System.out.println("Une erreur inattendue s'est produite: " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Parse un coup pour le TicTacToe (numéro 1 à 9) et crée Coup(null, caseArrivee).
     */
    private Coup parseCoupTicTacToe(String input, Plateau plateau) {
        int caseNum = Integer.parseInt(input);
        if (caseNum < 1 || caseNum > 9) {
            throw new IllegalArgumentException("Le numéro de case doit être entre 1 et 9.");
        }
        
        int indice = caseNum - 1;
        int ligne = indice / plateau.getSizeY(); // Correction : on divise par le nombre de colonnes (Y)
        int colonne = indice % plateau.getSizeY(); 
        
        Case arrivee = Plateau.getCase(ligne, colonne);

        if (arrivee.getPiece() != null) {
            throw new IllegalArgumentException("Cette case est déjà occupée !");
        }
        
        // TicTacToe : Pas de case de départ, CaseArrivee est la case choisie
        return new Coup(null, arrivee); 
    }

    /**
     * Parse un coup pour Puissance 4 (numéro de colonne) et crée Coup(null, caseArrivee).
     * La CaseArrivee est la Case vide la plus basse de la colonne.
     */
    private Coup parseCoupPuissance4(String input, Plateau plateau) {
        int colonne = Integer.parseInt(input);
        
        // On suppose que l'utilisateur entre la colonne en base 1 (ex: 1 pour la première colonne)
        // Si l'entrée doit être 0-indexée, retirez le '- 1'. Je prends 1-indexée pour la console utilisateur.
        int colIndex = colonne - 1; 

        if (colIndex < 0 || colIndex >= plateau.getSizeY()) {
            throw new IllegalArgumentException("Le numéro de colonne est hors limites (1 à " + plateau.getSizeY() + ").");
        }
        
        // Trouver la ligne de chute la plus basse (Puissance 4)
        Case caseArrivee = null;
        // Parcourir de bas en haut (ligne max - 1)
        for (int ligne = plateau.getSizeX() - 1; ligne >= 0; ligne--) {
            Case c = Plateau.getCase(ligne, colIndex);
            if (c.getPiece() == null) {
                caseArrivee = c;
                break;
            }
        }
        
        if (caseArrivee == null) {
            throw new IllegalArgumentException("Cette colonne est pleine !");
        }
        
        // Puissance 4 : Pas de case de départ, CaseArrivee est la case vide trouvée
        return new Coup(null, caseArrivee);
    }
    
    /**
     * Parse un coup pour les Échecs (notation "a2a4") et crée Coup(caseDepart, caseArrivee).
     */
    private Coup parseCoupEchec(String input, Plateau plateau) {
        // Validation basique de la notation (ex: a2a4 doit avoir 4 caractères)
        if (input.length() != 4) {
            throw new IllegalArgumentException("Format du coup invalide. Utilisez 'd2d4' (départ-arrivée).");
        }
        
        String notationDepart = input.substring(0, 2); // ex: "a2"
        String notationArrivee = input.substring(2, 4); // ex: "a4"

        // Conversion des notations d'échecs (a1, h8, etc.) en coordonnées [ligne][colonne]
        
        // La méthode de conversion du modèle JeuEchec devrait être capable de faire cela,
        // mais comme la VueConsole ne peut pas dépendre du code interne de JeuEchec, 
        // nous allons implémenter ici une conversion standard (en supposant a1 = [7][0] et h8 = [0][7]).
        
        try {
            Case caseDepart = coordToCase(notationDepart, plateau);
            Case caseArrivee = coordToCase(notationArrivee, plateau);
            
            // Validation simple des échecs (le modèle gérera les règles complexes)
            if (caseDepart.getPiece() == null) {
                 throw new IllegalArgumentException("La case de départ (" + notationDepart + ") est vide.");
            }
            // Option la plus sûre pour les objets, y compris les énumérations
            if (!caseDepart.getPiece().getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                throw new IllegalArgumentException("Ce n'est pas votre pièce !");
            }
            
            // Le modèle JeuEchec est responsable de valider si le coup est légal.
            return new Coup(caseDepart, caseArrivee);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    /**
     * Convertit la notation d'échecs (ex: "a1") en un objet Case.
     * @param notation La notation d'échecs (ex: "a1").
     * @param plateau Le plateau de jeu.
     * @return La Case correspondante.
     * @throws IllegalArgumentException si la notation est hors limites.
     */
    private Case coordToCase(String notation, Plateau plateau) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Notation de case invalide: " + notation);
        }
        
        // Colonne : 'a' -> 0, 'b' -> 1, ...
        char colChar = notation.toLowerCase().charAt(0);
        int colonne = colChar - 'a'; 
        
        // Ligne : '1' -> 7 (bas), '8' -> 0 (haut)
        int ligne = 8 - Integer.parseInt(notation.substring(1));

        if (ligne < 0 || ligne >= plateau.getSizeX() || colonne < 0 || colonne >= plateau.getSizeY()) {
            throw new IllegalArgumentException("Coordonnées de case hors limites: " + notation);
        }
        
        return Plateau.getCase(ligne, colonne);
    }
}