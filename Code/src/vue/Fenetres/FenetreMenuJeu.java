package vue.Fenetres;

import modele.jeu.Jeu;
import modele.jeu.JeuEchec;
import modele.jeu.JeuPuissance4;
import modele.jeu.JeuTicTacToe;
import modele.jeu.JeuTicTacToe3D;
import modele.plateau.Plateau;
import vue.VueEchec;
import vue.VuePuissance4;
import vue.VueTicTacToe;
import vue.VueTicTacToe3D;

import javax.swing.*;

import java.awt.*;

/**
 * Fenêtre de sélection du jeu.
 * <p>
 * Permet de démarrer une partie de TicTacToe, Puissance 4, TicTacToe 3D (placeholder)
 * ou Échecs via des boutons illustrés.
 * </p>
 */
public class FenetreMenuJeu extends JFrame {
    private JButton btnTicTacToe;
    private JButton btnPuissance4;
    private JButton btnTicTacToe3D;
    private JButton btnEchec;
    

    /**
     * Construit la fenêtre listant les jeux disponibles et initialise l'interface.
     */
    public FenetreMenuJeu() {
        // Configuration de la fenêtre
        this.setTitle("Choix du jeu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);

        // Création du panel principal (BorderLayout pour centrer une grille)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Création et configuration des boutons avec les composants graphiques
        btnTicTacToe = new JButton();
        btnTicTacToe.setIcon(new ImageIcon("images/Boutons/tictactoe.png"));
        btnTicTacToe.setBorderPainted(false);
        btnTicTacToe.setContentAreaFilled(false);
        btnTicTacToe.setFocusPainted(false);

        btnPuissance4 = new JButton();
        btnPuissance4.setIcon(new ImageIcon("images/Boutons/puissance4.png"));
        btnPuissance4.setBorderPainted(false);
        btnPuissance4.setContentAreaFilled(false);
        btnPuissance4.setFocusPainted(false);

        btnTicTacToe3D = new JButton();
        btnTicTacToe3D.setIcon(new ImageIcon("images/Boutons/tictactoe3D.png"));
        btnTicTacToe3D.setBorderPainted(false);
        btnTicTacToe3D.setContentAreaFilled(false);
        btnTicTacToe3D.setFocusPainted(false);

        btnEchec = new JButton();
        btnEchec.setIcon(new ImageIcon("images/Boutons/jeuechec.png"));
        btnEchec.setBorderPainted(false);
        btnEchec.setContentAreaFilled(false);
        btnEchec.setFocusPainted(false);


        // Configuration de la taille des boutons
        Dimension buttonSize = new Dimension(256, 128);
        btnTicTacToe.setPreferredSize(buttonSize);
        btnPuissance4.setPreferredSize(buttonSize);
        btnTicTacToe3D.setPreferredSize(buttonSize);
        btnEchec.setPreferredSize(buttonSize);

        // Ajout des actions aux boutons
        btnTicTacToe.addActionListener(e -> demarrerTicTacToe());
        btnPuissance4.addActionListener(e -> demarrerPuissance4());
        btnTicTacToe3D.addActionListener(e -> demarrerTicTacToe3D());
        btnEchec.addActionListener(e -> demarrerEchec());

        // Positionnement des boutons en 2x2 centrée
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        gridPanel.setOpaque(false);
        gridPanel.add(btnTicTacToe);
        gridPanel.add(btnPuissance4);
        gridPanel.add(btnTicTacToe3D);
        gridPanel.add(btnEchec);

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        mainPanel.setBackground(new Color(200, 220, 250));

        this.add(mainPanel);
    }


    /**
     * Lance une partie de TicTacToe en fermant le menu et en créant la vue.
     */
    private void demarrerTicTacToe() {
        this.dispose(); // Ferme le menu
        // Démarrage du jeu
        Plateau plateau = new Plateau(3, 3);
        Jeu jeu = new JeuTicTacToe(plateau);
        new VueTicTacToe(jeu);
    }

    /**
     * Lance une partie de Puissance 4 en fermant le menu et en créant la vue.
     */
    private void demarrerPuissance4() {
        this.dispose(); // Ferme le menu
        Plateau plateau = new Plateau(6, 7);
        Jeu jeu = new JeuPuissance4(plateau);
        new VuePuissance4(jeu);
    }

    /**
     * Lance une partie de TicTacToe 3D en fermant le menu et en créant la vue.
     */
    private void demarrerTicTacToe3D() {
        this.dispose(); // Ferme le menu
        Plateau plateau = new Plateau(3, 3, 3);
        JeuTicTacToe3D jeu = new JeuTicTacToe3D(plateau);
        new VueTicTacToe3D(jeu);
    }

    private void demarrerEchec() {
        this.dispose(); // Ferme le menu
        // Démarrage du jeu
        Plateau plateau = new Plateau(8, 8);
        Jeu jeu = new JeuEchec(plateau);
        new VueEchec(jeu);
    }
}
