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

public class Controleur {

    private FenetreMenuPrincipal menuPrincipal;
    private FenetreMenuJeu menuJeu;
    private Jeu jeuActuel;
    private Plateau plateau;

    public Controleur() {
        afficherMenuPrincipal();
    }

    // --- MENUS ---

    public void afficherMenuPrincipal() {
        menuPrincipal = new FenetreMenuPrincipal();
        menuPrincipal.setVisible(true);
    }

    public void afficherMenuJeu() {
        if (menuPrincipal != null) menuPrincipal.dispose();
        menuJeu = new FenetreMenuJeu();
        menuJeu.setVisible(true);
    }

    public void quitterProgramme() {
        System.exit(0);
    }

    // --- LANCEMENT DES JEUX ---

    public void lancerTicTacToe() {
        jeuActuel = new JeuTicTacToe(plateau);
        demarrerJeu();
    }

    public void lancerPuissance4() {
        jeuActuel = new JeuPuissance4(plateau);
        demarrerJeu();
    }

    public void lancerEchec() {
        jeuActuel = new JeuEchec(plateau);
        demarrerJeu();
    }

    private void demarrerJeu() {
        // Fermeture du menu
        if (menuJeu != null) menuJeu.dispose();
    }

    // --- RECEPTION D'ACTIONS DE LA VUE GRAPHIQUE ---

    public void caseCliquee(int x, int y) {
        if (jeuActuel == null) return;

        Case c = Plateau.getCase(x, y);
        Coup coup = new Coup(c, c);
        jeuActuel.setCoup(coup);
    }

    // --- FIN DE PARTIE ---

    public void finDePartie() {
        JOptionPane.showMessageDialog(null, "Partie terminée !");
        afficherMenuPrincipal();
    }
}

