package vue;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import modele.jeu.*;
import modele.plateau.Case;
import modele.plateau.Plateau;
import vue.Fenetres.FenetreMenuPrincipal;

/**
 * Interface graphique pour le jeu de Puissance 4.
 * <p>
 * Cette classe hérite de {@link JFrame} et permet l'affichage d'une grille de Puissance 4
 * ainsi que la gestion des interactions utilisateur (clic sur une colonne pour jouer).
 * Elle implémente {@link Observer} pour mettre à jour l'affichage en temps réel selon l'état du {@link Jeu}.
 * </p>
 */
public class VuePuissance4 extends JFrame implements Observer {

    /** Référence au plateau de jeu (modèle) pour accéder aux données des cases. */
    private Plateau plateau;
    /** Référence au contrôleur/modèle du jeu. */
    private Jeu jeu;

    /** Nombre de lignes de la grille affichée. */
    private final int sizeX;
    /** Nombre de colonnes de la grille affichée. */
    private final int sizeY;

    /** Taille en pixels d'une case (carrée) sur l'écran. */
    private static final int pxCase = 120;

    /** Tableau de composants graphiques représentant les cases de la grille. */
    private JLabel[][] tabJLabel;

    /**
     * Construit l'interface graphique du Puissance 4.
     * <p>
     * Initialise la fenêtre, la grille graphique et les écouteurs d'événements.
     * </p>
     *
     * @param _jeu L'instance du jeu (modèle) à observer.
     */
    public VuePuissance4(Jeu _jeu) {
        this.jeu = _jeu;
        this.plateau = jeu.getPlateau(); // Récupérer une instance de Plateau
        this.sizeX = plateau.getSizeX();
        this.sizeY = plateau.getSizeY();

        tabJLabel = new JLabel[sizeX][sizeY];

        placerLesComposantsGraphiques();
        mettreAJourAffichagePuissance4(); // Mettre à jour l'affichage initialement

        // Configurer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(sizeY * pxCase, sizeY * pxCase);
        this.setLocationRelativeTo(null);
        setVisible(true);

        jeu.addObserver(this);
    }

    /**
     * Initialise et place les composants graphiques (cases) dans la fenêtre.
     * <p>
     * Utilise un {@link GridLayout} pour disposer les {@link JLabel} représentant les cases.
     * Ajoute un écouteur de souris sur chaque case pour détecter les clics.
     * Au Puissance 4, un clic sur n'importe quelle case d'une colonne déclenche un coup dans cette colonne.
     * </p>
     */
    private void placerLesComposantsGraphiques() {
        setLayout(new GridLayout(sizeX, sizeY)); // Utiliser un GridLayout pour une grille
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                final int yy = y;

                JLabel label = new JLabel();
                label.setPreferredSize(new Dimension(pxCase, pxCase));
                label.setOpaque(true);

                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                label.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le contenu
                add(label); // Ajouter au JPanel principal
                tabJLabel[x][y] = label; // Ajouter au tableau des composants graphiques

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jeu.estTermine()) return;

                        // Pour Puissance4, on clique sur une colonne
                        int colonne = yy;
                        // On envoie le coup sur la colonne (la logique du placement est dans Jeu)
                        Case caseClic = Plateau.getCase(0, colonne); // n'importe quelle case de la colonne
                        Coup coup = new Coup(caseClic, caseClic);
                        jeu.setCoup(coup);
                        mettreAJourAffichagePuissance4();
                    }
                });

                tabJLabel[x][y] = label;
                this.add(label);
            }
        }
    }

    /**
     * Rafraîchit l'affichage de la grille entière.
     * <p>
     * Parcourt toutes les cases du modèle {@link Plateau}. Si une case contient une pièce,
     * un pion de la couleur correspondante (rond plein) est dessiné dans la case graphique.
     * Met également à jour le titre de la fenêtre pour indiquer à qui est le tour.
     * </p>
     */
    private void mettreAJourAffichagePuissance4() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case caseModele = Plateau.getCase(x, y);
                JLabel caseGraphique = tabJLabel[x][y];

                caseGraphique.setOpaque(true);
                caseGraphique.setBackground(Color.WHITE);
                caseGraphique.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                caseGraphique.setText("");
                caseGraphique.setIcon(null);

                if (caseModele.getPiece() != null) {
                    String couleur = caseModele.getPiece().getCouleur();
                    BufferedImage image = new BufferedImage(pxCase, pxCase, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if ("BLANC".equals(couleur)) {
                        g2d.setColor(new Color(211,211,211)); // Gris clair pour Blanc (plus visible sur fond blanc)
                    } else {
                        g2d.setColor(new Color(0,0,0)); // Noir
                    }
                    int diametre = pxCase - 20;
                    int xOval = (pxCase - diametre) / 2;
                    int yOval = (pxCase - diametre) / 2;
                    g2d.fillOval(xOval, yOval, diametre, diametre);
                    g2d.dispose();
                    caseGraphique.setIcon(new ImageIcon(image));
                }
            }
        }
        Joueur joueur = jeu.getJoueurCourant();
        setTitle("Puissance4 - Trait au " + joueur.getCouleur().name().toLowerCase());
    }

    /**
     * Gère la fin de partie.
     * <p>
     * Affiche une boîte de dialogue avec le résultat (Vainqueur ou Nul) et les scores.
     * Propose de rejouer, de retourner au menu ou de quitter.
     * </p>
     */
    private void afficherFinDePartie() {
        String message;
        Joueur gagnant = null;

        boolean gagnantTrouve = verifierPuissance4Gagnant();
        gagnant = jeu.getGagnant();
        if(gagnantTrouve && gagnant != null) {
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
            mettreAJourAffichagePuissance4();
        } else if(choix == 1) {
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
     * Vérifie manuellement s'il y a un gagnant (alignement de 4 pions).
     * <p>
     * Note : Cette logique est redondante avec {@link JeuPuissance4#estTermine()} mais est utilisée ici
     * pour confirmer l'état visuel avant l'affichage du message de fin.
     * Elle vérifie les lignes, colonnes et diagonales.
     * </p>
     *
     * @return {@code true} si un alignement gagnant est trouvé, {@code false} sinon.
     */
    private boolean verifierPuissance4Gagnant() {
        // Vérifier les lignes (horizontales)
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY - 3; y++) {
                Piece p1 = Plateau.getCase(x, y).getPiece();
                Piece p2 = Plateau.getCase(x, y + 1).getPiece();
                Piece p3 = Plateau.getCase(x, y + 2).getPiece();
                Piece p4 = Plateau.getCase(x, y + 3).getPiece();

                if (p1 != null && p2 != null && p3 != null && p4 != null &&
                        p1.getCouleur().equals(p2.getCouleur()) &&
                        p2.getCouleur().equals(p3.getCouleur()) &&
                        p3.getCouleur().equals(p4.getCouleur())) {
                    return true;
                }
            }
        }

        // Vérifier les colonnes (verticales)
        for (int x = 0; x < sizeX - 3; x++) {
            for (int y = 0; y < sizeY; y++) {
                Piece p1 = Plateau.getCase(x, y).getPiece();
                Piece p2 = Plateau.getCase(x + 1, y).getPiece();
                Piece p3 = Plateau.getCase(x + 2, y).getPiece();
                Piece p4 = Plateau.getCase(x + 3, y).getPiece();

                if (p1 != null && p2 != null && p3 != null && p4 != null &&
                        p1.getCouleur().equals(p2.getCouleur()) &&
                        p2.getCouleur().equals(p3.getCouleur()) &&
                        p3.getCouleur().equals(p4.getCouleur())) {
                    return true;
                }
            }
        }

        // Vérifier les diagonales (montante)
        for (int x = 0; x < sizeX - 3; x++) {
            for (int y = 0; y < sizeY - 3; y++) {
                Piece p1 = Plateau.getCase(x, y).getPiece();
                Piece p2 = Plateau.getCase(x + 1, y + 1).getPiece();
                Piece p3 = Plateau.getCase(x + 2, y + 2).getPiece();
                Piece p4 = Plateau.getCase(x + 3, y + 3).getPiece();

                if (p1 != null && p2 != null && p3 != null && p4 != null &&
                        p1.getCouleur().equals(p2.getCouleur()) &&
                        p2.getCouleur().equals(p3.getCouleur()) &&
                        p3.getCouleur().equals(p4.getCouleur())) {
                    return true;
                }
            }
        }

        // Vérifier les diagonales (descendante)
        for (int x = 0; x < sizeX - 3; x++) {
            for (int y = 3; y < sizeY; y++) {
                Piece p1 = Plateau.getCase(x, y).getPiece();
                Piece p2 = Plateau.getCase(x + 1, y - 1).getPiece();
                Piece p3 = Plateau.getCase(x + 2, y - 2).getPiece();
                Piece p4 = Plateau.getCase(x + 3, y - 3).getPiece();

                if (p1 != null && p2 != null && p3 != null && p4 != null &&
                        p1.getCouleur().equals(p2.getCouleur()) &&
                        p2.getCouleur().equals(p3.getCouleur()) &&
                        p3.getCouleur().equals(p4.getCouleur())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Méthode appelée automatiquement lorsque le modèle notifie un changement.
     * <p>
     * Met à jour l'affichage et vérifie si la partie est terminée.
     * </p>
     *
     * @param o   L'objet observable (le Jeu).
     * @param arg Argument optionnel (non utilisé).
     */
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichagePuissance4();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}