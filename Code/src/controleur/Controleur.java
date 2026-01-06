package controleur;

import javax.swing.JOptionPane;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.jeu.JeuEchec;
import modele.jeu.JeuTicTacToe;
import modele.jeu.JeuPuissance4;
import modele.plateau.Case;
import modele.plateau.Plateau;
import vue.Fenetres.FenetreMenuJeu;
import vue.Fenetres.FenetreMenuPrincipal;

/**
 * Contrôleur principal de l'application.
 * <p>
 * Gère la navigation entre les menus, l'initialisation des jeux disponibles
 * (TicTacToe, Puissance 4, Échecs) ainsi que la transmission des actions
 * provenant de la vue (clics sur le plateau) vers la logique de jeu.
 * </p>
 */
public class Controleur {

    /** Fenêtre du menu principal. */
    private FenetreMenuPrincipal menuPrincipal;
    /** Fenêtre de sélection/lancement de jeu. */
    private FenetreMenuJeu menuJeu;
    /** Jeu actuellement actif. */
    private Jeu jeuActuel;
    /** Plateau courant associé au jeu (peut être injecté/initialisé ailleurs). */
    private Plateau plateau;

    /**
     * Crée le contrôleur et affiche le menu principal.
     */
    public Controleur() {
        afficherMenuPrincipal();
    }

    // --- MENUS ---

    /**
     * Affiche la fenêtre du menu principal.
     */
    public void afficherMenuPrincipal() {
        menuPrincipal = new FenetreMenuPrincipal();
        menuPrincipal.setVisible(true);
    }

    /**
     * Affiche le menu de sélection de jeu et ferme le menu principal si nécessaire.
     */
    public void afficherMenuJeu() {
        if (menuPrincipal != null) menuPrincipal.dispose();
        menuJeu = new FenetreMenuJeu();
        menuJeu.setVisible(true);
    }

    /**
     * Quitte proprement l'application.
     */
    public void quitterProgramme() {
        System.exit(0);
    }

    // --- LANCEMENT DES JEUX ---

    /** Lance une partie de Tic-Tac-Toe. */
    public void lancerTicTacToe() {
        jeuActuel = new JeuTicTacToe(plateau);
        demarrerJeu();
    }

    /** Lance une partie de Puissance 4. */
    public void lancerPuissance4() {
        jeuActuel = new JeuPuissance4(plateau);
        demarrerJeu();
    }

    /** Lance une partie d'Échecs. */
    public void lancerEchec() {
        jeuActuel = new JeuEchec(plateau);
        demarrerJeu();
    }

    /**
     * Réalise les opérations communes au démarrage d'un jeu (fermeture des menus, etc.).
     */
    private void demarrerJeu() {
        // Fermeture du menu
        if (menuJeu != null) menuJeu.dispose();
    }

    // --- RECEPTION D'ACTIONS DE LA VUE GRAPHIQUE ---

    /**
     * Gère le clic sur une case du plateau en le traduisant en {@link Coup} pour le jeu actif.
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public void caseCliquee(int x, int y) {
        if (jeuActuel == null) return;

        Case c = Plateau.getCase(x, y);
        Coup coup = new Coup(c, c);
        jeuActuel.setCoup(coup);
    }

    // --- FIN DE PARTIE ---

    /**
     * Termine la partie courante, affiche un message et revient au menu principal.
     */
    public void finDePartie() {
        JOptionPane.showMessageDialog(null, "Partie terminée !");
        afficherMenuPrincipal();
    }
}

