package vue;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import modele.jeu.*;
import modele.plateau.Case;
import modele.plateau.Plateau;
import vue.Fenetres.FenetreMenuPrincipal;

/**
 * Vue graphique pour le TicTacToe 3D.
 * Affiche 3 grilles 3x3 côte à côte représentant les 3 couches du cube.
 */
public class VueTicTacToe3D extends JFrame implements Observer {
    private JeuTicTacToe3D jeu;
    private final int pxCase = 120; // Taille de chaque case en pixels
    private JLabel[][][] tabJLabel; // [grille][x][y]

    public VueTicTacToe3D(JeuTicTacToe3D jeu) {
        this.jeu = jeu;
        tabJLabel = new JLabel[3][3][3]; // 3 grilles de 3x3

        placerLesComposantsGraphiques();
        mettreAJourAffichage();

        // Configuration de la fenêtre
        setTitle("TicTacToe 3D - Trait au BLANC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(3 * 3 * pxCase + 100, 3 * pxCase + 150);
        setLocationRelativeTo(null);
        setVisible(true);

        jeu.addObserver(this);
    }

    private void placerLesComposantsGraphiques() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 250));

        // Titre
        JLabel titre = new JLabel("TIC-TAC-TOE 3D", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(50, 50, 150));
        mainPanel.add(titre, BorderLayout.NORTH);

        // Panel pour les 3 grilles
        JPanel grillesPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        grillesPanel.setOpaque(false);

        // Créer les 3 grilles (A, B, C)
        String[] nomsCouches = {"Grille A", "Grille B", "Grille C"};
        Color[] couleursGrilles = {
            new Color(200, 220, 255),
            new Color(220, 255, 220),
            new Color(255, 220, 220)
        };

        for (int z = 0; z < 3; z++) {
            JPanel grillePanel = creerGrille(z, nomsCouches[z], couleursGrilles[z]);
            grillesPanel.add(grillePanel);
        }

        mainPanel.add(grillesPanel, BorderLayout.CENTER);

        // Info en bas
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel("Cliquez sur une case pour jouer - X = Blanc, O = Noir");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel creerGrille(int z, String nomGrille, Color couleurFond) {
        JPanel grilleContainer = new JPanel(new BorderLayout(5, 5));
        grilleContainer.setOpaque(false);

        // Titre de la grille
        JLabel labelGrille = new JLabel(nomGrille, SwingConstants.CENTER);
        labelGrille.setFont(new Font("Arial", Font.BOLD, 16));
        labelGrille.setForeground(new Color(50, 50, 100));
        grilleContainer.add(labelGrille, BorderLayout.NORTH);

        // Grille 3x3
        JPanel grille = new JPanel(new GridLayout(3, 3, 2, 2));
        grille.setBackground(Color.DARK_GRAY);
        grille.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                final int xx = x;
                final int yy = y;
                final int zz = z;

                JLabel label = new JLabel();
                label.setPreferredSize(new Dimension(pxCase, pxCase));
                label.setOpaque(true);
                label.setBackground(couleurFond);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 48));
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jeu.estTermine()) return;

                        Case caseClic = Plateau.getCase(xx, yy, zz);
                        if (caseClic.getPiece() == null) {
                            Coup coup = new Coup(caseClic, caseClic);
                            jeu.setCoup(coup);
                            mettreAJourAffichage();
                        } else {
                            System.out.println("Cette case est déjà occupée !");
                        }
                    }
                });

                tabJLabel[z][x][y] = label;
                grille.add(label);
            }
        }

        grilleContainer.add(grille, BorderLayout.CENTER);
        return grilleContainer;
    }

    private void mettreAJourAffichage() {
        Color[] couleursGrilles = {
            new Color(200, 220, 255),
            new Color(220, 255, 220),
            new Color(255, 220, 220)
        };

        for (int z = 0; z < 3; z++) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Case caseModele = Plateau.getCase(x, y, z);
                    JLabel caseGraphique = tabJLabel[z][x][y];

                    caseGraphique.setOpaque(true);
                    caseGraphique.setBackground(couleursGrilles[z]);
                    caseGraphique.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    caseGraphique.setFont(new Font("Arial", Font.BOLD, 48));
                    caseGraphique.setHorizontalAlignment(SwingConstants.CENTER);
                    caseGraphique.setVerticalAlignment(SwingConstants.CENTER);

                    if (caseModele.getPiece() != null) {
                        String couleur = caseModele.getPiece().getCouleur();
                        if ("BLANC".equals(couleur)) {
                            caseGraphique.setText("X");
                            caseGraphique.setForeground(new Color(211, 211, 211));
                        } else if ("NOIR".equals(couleur)) {
                            caseGraphique.setText("O");
                            caseGraphique.setForeground(new Color(0, 0, 0));
                        }
                    } else {
                        caseGraphique.setText("");
                    }
                }
            }
        }

        Joueur joueur = jeu.getJoueurCourant();
        setTitle("TicTacToe 3D - Trait au " + joueur.getCouleur().name());
    }

    private void afficherFinDePartie() {
        String message;
        Joueur gagnant = jeu.getGagnant();

        if (gagnant != null) {
            message = "Le joueur " + gagnant.getCouleur() + " a gagné !";
            gagnant.ajouterPoint();
            System.out.println("Score: " + jeu.getJoueurBlanc().getPoints() + " - " + jeu.getJoueurNoir().getPoints());
        } else {
            message = "Match nul !";
            System.out.println("Score: " + jeu.getJoueurBlanc().getPoints() + " - " + jeu.getJoueurNoir().getPoints());
        }

        // Affichage du score
        String scoreMessage = message + "\n\n--- SCORE ---\n" +
                "Blanc: " + jeu.getJoueurBlanc().getPoints() + " points\n" +
                "Noir: " + jeu.getJoueurNoir().getPoints() + " points";

        // Boîte de dialogue avec options
        String[] options = {"Rejouer", "Menu", "Quitter"};
        int choix = JOptionPane.showOptionDialog(
                this,
                scoreMessage,
                "Fin de partie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choix == 0) {
            // Rejouer
            jeu.reinitialiserPartie();
        } else if (choix == 1) {
            // Retour au menu
            this.dispose();
            FenetreMenuPrincipal menu = new FenetreMenuPrincipal();
            menu.setVisible(true);
        } else {
            // Quitter
            System.exit(0);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}
