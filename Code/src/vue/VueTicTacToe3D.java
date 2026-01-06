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
 * Vue graphique pour le jeu TicTacToe 3D.
 * <p>
 * Cette classe affiche le plateau de jeu en 3 dimensions sous la forme de trois grilles 2D (3x3)
 * disposées côte à côte. Chaque grille représente une "couche" (ou un étage) du cube 3x3x3 :
 * <ul>
 * <li>Grille A : Couche du bas (z=0)</li>
 * <li>Grille B : Couche du milieu (z=1)</li>
 * <li>Grille C : Couche du haut (z=2)</li>
 * </ul>
 * Elle permet aux joueurs de visualiser l'état du cube et de cliquer sur n'importe quelle case
 * pour y placer leur symbole.
 * </p>
 */
public class VueTicTacToe3D extends JFrame implements Observer {

    /** Référence vers le modèle spécifique du TicTacToe 3D. */
    private JeuTicTacToe3D jeu;

    /** Taille en pixels d'une case de la grille. */
    private final int pxCase = 120;

    /** * Tableau tridimensionnel stockant les références vers les composants graphiques (JLabel).
     * Dimensions : [z][x][y] où z est la couche, x la ligne et y la colonne.
     */
    private JLabel[][][] tabJLabel;

    /**
     * Construit l'interface graphique du Tic-Tac-Toe 3D.
     * <p>
     * Initialise la fenêtre principale, crée les trois grilles de jeu,
     * et s'abonne aux notifications du modèle pour les mises à jour.
     * </p>
     *
     * @param jeu L'instance du jeu TicTacToe3D (modèle) à contrôler et afficher.
     */
    public VueTicTacToe3D(JeuTicTacToe3D jeu) {
        this.jeu = jeu;
        tabJLabel = new JLabel[3][3][3]; // 3 grilles de 3x3

        placerLesComposantsGraphiques();
        mettreAJourAffichage();

        // Configuration de la fenêtre
        setTitle("TicTacToe 3D - Trait au BLANC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Taille ajustée pour contenir les 3 grilles côte à côte
        setSize(3 * 3 * pxCase + 100, 3 * pxCase + 150);
        setLocationRelativeTo(null);
        setVisible(true);

        jeu.addObserver(this);
    }

    /**
     * Initialise et dispose tous les éléments visuels de la fenêtre.
     * <p>
     * Crée un panneau principal contenant :
     * <ul>
     * <li>Le titre du jeu en haut.</li>
     * <li>Un panneau central divisé en 3 colonnes pour les 3 grilles (A, B, C).</li>
     * <li>Une légende informative en bas.</li>
     * </ul>
     * Utilise la méthode {@link #creerGrille(int, String, Color)} pour générer chaque couche.
     * </p>
     */
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

        // Créer les 3 grilles (A, B, C) avec des couleurs de fond distinctes pour mieux les différencier
        String[] nomsCouches = {"Grille A", "Grille B", "Grille C"};
        Color[] couleursGrilles = {
                new Color(200, 220, 255), // Bleu clair
                new Color(220, 255, 220), // Vert clair
                new Color(255, 220, 220)  // Rouge clair
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

    /**
     * Crée un panneau représentant une seule couche (grille 3x3) du cube.
     *
     * @param z           L'indice de la couche (0, 1 ou 2).
     * @param nomGrille   Le nom affiché au-dessus de la grille (ex: "Grille A").
     * @param couleurFond La couleur de fond des cases pour cette couche.
     * @return Un JPanel contenant le titre et la grille de JLabels interactifs.
     */
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

                // Ajout de l'écouteur de clic pour jouer un coup
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jeu.estTermine()) return;

                        // Récupération de la case du modèle correspondant aux coordonnées (x, y, z)
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

    /**
     * Rafraîchit l'affichage des trois grilles.
     * <p>
     * Parcourt toutes les cases du modèle (x, y, z) et met à jour les JLabels correspondants :
     * <ul>
     * <li>Si la case contient un pion BLANC : affiche "X".</li>
     * <li>Si la case contient un pion NOIR : affiche "O".</li>
     * <li>Si la case est vide : efface le texte.</li>
     * </ul>
     * Met également à jour le titre de la fenêtre pour indiquer le joueur courant.
     * </p>
     */
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

    /**
     * Affiche une boîte de dialogue de fin de partie.
     * <p>
     * Informe les joueurs du résultat (Vainqueur ou Match nul) et des scores actuels.
     * Propose trois options :
     * <ol>
     * <li>Rejouer : Relance une partie immédiatement.</li>
     * <li>Menu : Retourne au menu principal de l'application.</li>
     * <li>Quitter : Ferme l'application.</li>
     * </ol>
     * </p>
     */
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

    /**
     * Méthode de l'interface {@link Observer}.
     * <p>
     * Appelée automatiquement quand le modèle change.
     * Provoque le rafraîchissement de l'affichage et vérifie la fin de partie.
     * </p>
     *
     * @param o   L'objet observable (JeuTicTacToe3D).
     * @param arg Argument optionnel.
     */
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}