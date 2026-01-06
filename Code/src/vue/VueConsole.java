package vue;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.plateau.Plateau;
import modele.plateau.Case;

// Imports des jeux et modèles spécifiques
import modele.jeu.JeuTicTacToe;
import modele.jeu.JeuPuissance4;
import modele.jeu.JeuEchec;
import modele.jeu.Joueur;

import java.util.Scanner;

/**
 * Classe principale gérant l'interface utilisateur en mode Console.
 * <p>
 * Cette classe implémente {@link Runnable} pour s'exécuter dans un thread séparé.
 * Elle a deux responsabilités principales :
 * 1. Initialiser les gestionnaires d'affichage (Handlers) selon le type de jeu (MVC).
 * 2. Gérer la boucle de saisie des coups par l'utilisateur via le {@link Scanner}.
 * </p>
 * * @author Votre Nom
 * @version 2.0
 */
public class VueConsole implements Runnable {

    /** Le modèle du jeu en cours. */
    private Jeu jeu;

    /** Le scanner partagé pour lire les entrées clavier. */
    private Scanner scanner;

    /**
     * Constructeur de la Vue Console.
     * <p>
     * Il détecte dynamiquement le type de jeu (TicTacToe, Puissance 4, Échecs) et
     * attache le {@link modele.jeu.JeuEventListener} approprié pour gérer
     * l'affichage et les sons (Pattern Observer/Listener).
     * </p>
     *
     * @param jeu L'instance du jeu (Modèle) en cours.
     * @param sharedScanner Le scanner ouvert dans le Main (pour éviter de fermer System.in).
     */
    public VueConsole(Jeu jeu, Scanner sharedScanner) {
        this.jeu = jeu;
        this.scanner = sharedScanner;

        // --- CABLAGE MVC ---
        // On connecte le bon gestionnaire d'affichage (Handler) selon le type de jeu.
        // C'est ici que la séparation Modèle-Vue est configurée.

        if (jeu instanceof JeuTicTacToe) {
            TicTacToeConsoleHandler handler = new TicTacToeConsoleHandler(jeu);
            ((JeuTicTacToe) jeu).setEventListener(handler);
        } else if (jeu instanceof JeuPuissance4) {
            Puissance4ConsoleHandler handler = new Puissance4ConsoleHandler(jeu);
            ((JeuPuissance4) jeu).setEventListener(handler);
        } else if (jeu instanceof JeuEchec) {
            EchecConsoleHandler handler = new EchecConsoleHandler(jeu);
            ((JeuEchec) jeu).setEventListener(handler);
        }
        // -----------------

        // Lancement du thread de gestion des entrées utilisateur
        new Thread(this).start();

        System.out.println("Vue Console démarrée pour le jeu : " + jeu.getClass().getSimpleName());
    }

    /**
     * Boucle principale du thread de la Vue Console.
     * <p>
     * Tant que la partie n'est pas terminée, cette méthode :
     * 1. Demande un coup à l'utilisateur.
     * 2. Transmet ce coup au modèle via {@link Jeu#setCoup(Coup)}.
     * </p>
     */
    @Override
    public void run() {
        while (true) {
            // Si le jeu est terminé, on affiche le résultat et on arrête la boucle.
            if (jeu.estTermine()) {
                afficherfinPartie();
                break;
            } else {
                // Saisie du coup
                Coup coup = demanderCoup();

                if (coup == null) {
                    // Si l'utilisateur a entré 'q' ou quitte la saisie
                    System.out.println("Fin de l'interaction console.");
                    break;
                }

                // Envoie le coup au contrôleur/modèle qui traitera la logique
                jeu.setCoup(coup);
            }
        }
    }

    /**
     * Affiche le message de fin de partie, le vainqueur et le score final.
     */
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
     * Demande à l'utilisateur d'entrer un coup et le convertit en un objet {@link Coup}.
     * <p>
     * Cette méthode adapte le message d'invite selon le jeu (ex: "1-9" pour le Morpion,
     * "a2a4" pour les Échecs) et gère les erreurs de format (NumberFormat, etc.).
     * </p>
     *
     * @return Un objet {@link Coup} valide (contenant case départ/arrivée), ou {@code null} si l'utilisateur quitte.
     */
    private Coup demanderCoup() {
        Plateau plateau = jeu.getPlateau();
        String instruction = "";

        // Définir l'instruction de saisie adaptée au jeu
        if (jeu instanceof JeuTicTacToe) {
            instruction = "Entrez le numéro de la case où jouer (1-9) ou 'q' pour quitter : ";
        } else if (jeu instanceof JeuPuissance4) {
            instruction = "Entrez le numéro de la colonne où jouer ou 'q' pour quitter : ";
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
                // Délégation du parsing spécifique à chaque jeu
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
                // Capturer les erreurs logiques (case prise, hors limites, format string invalide)
                System.out.println("Coup invalide : " + e.getMessage() + ". Réessayez.");
            } catch (Exception e) {
                System.out.println("Une erreur inattendue s'est produite: " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Interprète une entrée utilisateur pour le Tic-Tac-Toe.
     *
     * @param input La chaîne entrée (ex: "5").
     * @param plateau Le plateau de jeu actuel.
     * @return Un {@link Coup} avec la case d'arrivée correspondante.
     * @throws IllegalArgumentException Si le numéro est hors limites (1-9) ou la case occupée.
     */
    private Coup parseCoupTicTacToe(String input, Plateau plateau) {
        int caseNum = Integer.parseInt(input);
        if (caseNum < 1 || caseNum > 9) {
            throw new IllegalArgumentException("Le numéro de case doit être entre 1 et 9.");
        }

        int indice = caseNum - 1;
        int ligne = indice / plateau.getSizeY();
        int colonne = indice % plateau.getSizeY();

        Case arrivee = Plateau.getCase(ligne, colonne);

        if (arrivee.getPiece() != null) {
            throw new IllegalArgumentException("Cette case est déjà occupée !");
        }

        // TicTacToe : Pas de case de départ, CaseArrivee est la case choisie
        return new Coup(null, arrivee);
    }

    /**
     * Interprète une entrée utilisateur pour le Puissance 4.
     * <p>
     * Calcule automatiquement la case d'arrivée en simulant la gravité :
     * on cherche la case vide la plus basse dans la colonne choisie.
     * </p>
     *
     * @param input Le numéro de la colonne (base 1).
     * @param plateau Le plateau de jeu.
     * @return Un {@link Coup} ciblant la case vide la plus basse de la colonne.
     * @throws IllegalArgumentException Si la colonne est pleine ou invalide.
     */
    private Coup parseCoupPuissance4(String input, Plateau plateau) {
        int colonne = Integer.parseInt(input);

        // Conversion base 1 (utilisateur) -> base 0 (tableau)
        int colIndex = colonne - 1;

        if (colIndex < 0 || colIndex >= plateau.getSizeY()) {
            throw new IllegalArgumentException("Le numéro de colonne est hors limites (1 à " + plateau.getSizeY() + ").");
        }

        // Trouver la ligne de chute la plus basse (Gravité)
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

        return new Coup(null, caseArrivee);
    }

    /**
     * Interprète une entrée utilisateur pour le Jeu d'Échecs (Notation Algébrique).
     * <p>
     * Format attendu : "a2a4" (Case Départ + Case Arrivée).
     * </p>
     *
     * @param input La chaîne de caractères (ex: "e2e4").
     * @param plateau Le plateau de jeu.
     * @return Un {@link Coup} complet avec case de départ et case d'arrivée.
     * @throws IllegalArgumentException Si le format est incorrect ou si la case de départ est vide/invalide.
     */
    private Coup parseCoupEchec(String input, Plateau plateau) {
        // Validation basique de la longueur (ex: a2a4 = 4 caractères)
        if (input.length() != 4) {
            throw new IllegalArgumentException("Format du coup invalide. Utilisez 'd2d4' (départ-arrivée).");
        }

        String notationDepart = input.substring(0, 2); // ex: "a2"
        String notationArrivee = input.substring(2, 4); // ex: "a4"

        try {
            Case caseDepart = coordToCase(notationDepart, plateau);
            Case caseArrivee = coordToCase(notationArrivee, plateau);

            // Validation simple des échecs
            if (caseDepart.getPiece() == null) {
                throw new IllegalArgumentException("La case de départ (" + notationDepart + ") est vide.");
            }

            // On compare la String de la pièce avec le .name() de l'Enum du joueur
            String couleurPiece = caseDepart.getPiece().getCouleur();
            String couleurJoueur = jeu.getJoueurCourant().getCouleur().name(); // Récupère "BLANC" ou "NOIR"

            if (!couleurPiece.equalsIgnoreCase(couleurJoueur)) {
                throw new IllegalArgumentException("Ce n'est pas votre pièce !");
            }
            // ---------------------

            return new Coup(caseDepart, caseArrivee);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Convertit une coordonnée d'échecs (ex: "a1") en objet {@link Case}.
     *
     * @param notation La notation (ex: "a1", "h8").
     * @param plateau Le plateau pour récupérer la case.
     * @return L'objet {@link Case} correspondant.
     * @throws IllegalArgumentException Si la notation est hors du plateau.
     */
    private Case coordToCase(String notation, Plateau plateau) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Notation de case invalide: " + notation);
        }

        // Colonne : 'a' -> 0, 'b' -> 1, ...
        char colChar = notation.toLowerCase().charAt(0);
        int colonne = colChar - 'a';

        // Ligne : '1' -> 7 (bas), '8' -> 0 (haut) pour un plateau 8x8 standard
        // Attention : Integer.parseInt peut lever NumberFormatException, géré par l'appelant
        int ligne = 8 - Integer.parseInt(notation.substring(1));

        if (ligne < 0 || ligne >= plateau.getSizeX() || colonne < 0 || colonne >= plateau.getSizeY()) {
            throw new IllegalArgumentException("Coordonnées de case hors limites: " + notation);
        }

        return Plateau.getCase(ligne, colonne);
    }
}