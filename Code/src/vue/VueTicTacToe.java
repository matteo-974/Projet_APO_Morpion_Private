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
 * Interface graphique pour le jeu de Tic-Tac-Toe (Morpion).
 * <p>
 * Cette classe gère l'affichage d'une grille 3x3 et permet aux joueurs de placer
 * leurs symboles (X ou O) en cliquant sur les cases.
 * Elle implémente {@link Observer} pour synchroniser l'affichage avec l'état du modèle {@link Jeu}.
 * </p>
 */
public class VueTicTacToe extends JFrame implements Observer {

    /** Référence au plateau de jeu (modèle) pour accéder aux données. */
    private Plateau plateau;
    /** Référence au contrôleur/modèle du jeu. */
    private Jeu jeu;

    /** Nombre de lignes de la grille (3 pour le Tic-Tac-Toe standard). */
    private final int sizeX;
    /** Nombre de colonnes de la grille (3 pour le Tic-Tac-Toe standard). */
    private final int sizeY;

    /** Taille en pixels d'une case (carrée) pour l'affichage. */
    private final int pxCase = 200;

    /** Tableau de composants graphiques représentant les cases de la grille. */
    private JLabel[][] tabJLabel;

    /**
     * Construit l'interface graphique du Tic-Tac-Toe.
     * <p>
     * Initialise la fenêtre, le plateau graphique et s'abonne aux mises à jour du jeu.
     * </p>
     *
     * @param _jeu L'instance du jeu (modèle) à observer.
     */
    public VueTicTacToe(Jeu _jeu) {
        this.jeu = _jeu;
        this.plateau = jeu.getPlateau(); // Récupérer une instance de Plateau
        this.sizeX = plateau.getSizeX();
        this.sizeY = plateau.getSizeY();

        tabJLabel = new JLabel[sizeX][sizeY];

        placerLesComposantsGraphiques();
        mettreAJourAffichageTicTacToe(); // Mettre à jour l'affichage initialement

        // Configurer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(sizeY * pxCase, sizeY * pxCase);
        this.setLocationRelativeTo(null);
        setVisible(true);

        jeu.addObserver(this);
    }

    /**
     * Initialise et dispose les composants graphiques (cases) dans la fenêtre.
     * <p>
     * Utilise un {@link GridLayout} pour organiser les cases. Chaque case est un {@link JLabel}
     * interactif qui détecte les clics de souris.
     * Un clic sur une case vide déclenche une tentative de coup à ces coordonnées.
     * </p>
     */
    private void placerLesComposantsGraphiques() {
        setLayout(new GridLayout(sizeX, sizeY)); // Utiliser un GridLayout pour une grille
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                final int xx = x;
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

                        // Pour TicTacToe, on clique directement sur une case
                        Case caseClic = Plateau.getCase(xx, yy);
                        if (caseClic.getPiece() == null) {
                            Coup coup = new Coup(caseClic, caseClic);
                            jeu.setCoup(coup);
                            mettreAJourAffichageTicTacToe();
                        } else {
                            System.out.println("Cette case est déjà occupée !");
                            mettreAJourAffichageTicTacToe();
                        }
                    }
                });

                tabJLabel[x][y] = label;
                this.add(label);
            }
        }
    }

    /**
     * Rafraîchit l'affichage du plateau de jeu.
     * <p>
     * Parcourt toutes les cases du modèle. Si une pièce est présente :
     * <ul>
     * <li>Affiche "X" pour le joueur BLANC.</li>
     * <li>Affiche "O" pour le joueur NOIR.</li>
     * </ul>
     * Met également à jour le titre de la fenêtre pour indiquer à qui est le tour.
     * </p>
     */
    private void mettreAJourAffichageTicTacToe() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case caseModele = Plateau.getCase(x, y);
                JLabel caseGraphique = tabJLabel[x][y];

                caseGraphique.setOpaque(true);
                caseGraphique.setBackground(Color.WHITE);
                caseGraphique.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                caseGraphique.setFont(new Font("Arial", Font.BOLD, 50));
                caseGraphique.setHorizontalAlignment(SwingConstants.CENTER);
                caseGraphique.setVerticalAlignment(SwingConstants.CENTER);

                if (caseModele.getPiece() != null) {
                    String couleur = caseModele.getPiece().getCouleur();
                    if ("BLANC".equals(couleur)) {
                        caseGraphique.setText("X");
                        caseGraphique.setForeground(new Color(211,211,211));
                    } else if ("NOIR".equals(couleur)) {
                        caseGraphique.setText("O");
                        caseGraphique.setForeground(new Color(0, 0, 0));
                    }
                    caseGraphique.setIcon(null);
                } else {
                    caseGraphique.setText("");
                    caseGraphique.setIcon(null);
                }
            }
        }

        Joueur joueur = jeu.getJoueurCourant();
        setTitle("TicTacToe - Trait au " + joueur.getCouleur().name().toLowerCase());
    }

    /**
     * Gère la fin de partie.
     * <p>
     * Vérifie le résultat final (Victoire ou Nul), met à jour les scores
     * et affiche une boîte de dialogue proposant de rejouer, revenir au menu ou quitter.
     * </p>
     */
    private void afficherFinDePartie() {
        String message;
        Joueur gagnant = null;

        boolean gagnantTrouve = verifierTicTacToeGagnant();
        gagnant = jeu.getGagnant();
        if (gagnantTrouve && gagnant != null) {
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
     * Vérifie les conditions de victoire pour le Tic-Tac-Toe.
     * <p>
     * Vérifie l'alignement de 3 symboles identiques sur :
     * <ul>
     * <li>Les lignes</li>
     * <li>Les colonnes</li>
     * <li>Les diagonales</li>
     * </ul>
     * </p>
     *
     * @return {@code true} si un gagnant est trouvé, {@code false} sinon.
     */
    private boolean verifierTicTacToeGagnant() {
        // Vérifier les lignes
        for (int x = 0; x < sizeX; x++) {
            Piece p1 = Plateau.getCase(x, 0).getPiece();
            Piece p2 = Plateau.getCase(x, 1).getPiece();
            Piece p3 = Plateau.getCase(x, 2).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                    p1.getCouleur().equals(p2.getCouleur()) &&
                    p2.getCouleur().equals(p3.getCouleur())) {
                return true;
            }
        }

        // Vérifier les colonnes
        for (int y = 0; y < sizeY; y++) {
            Piece p1 = Plateau.getCase(0, y).getPiece();
            Piece p2 = Plateau.getCase(1, y).getPiece();
            Piece p3 = Plateau.getCase(2, y).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                    p1.getCouleur().equals(p2.getCouleur()) &&
                    p2.getCouleur().equals(p3.getCouleur())) {
                return true;
            }
        }

        // Vérifier la diagonale principale
        Piece p1 = Plateau.getCase(0, 0).getPiece();
        Piece p2 = Plateau.getCase(1, 1).getPiece();
        Piece p3 = Plateau.getCase(2, 2).getPiece();

        if (p1 != null && p2 != null && p3 != null &&
                p1.getCouleur().equals(p2.getCouleur()) &&
                p2.getCouleur().equals(p3.getCouleur())) {
            return true;
        }

        // Vérifier la diagonale secondaire
        p1 = Plateau.getCase(0, 2).getPiece();
        p2 = Plateau.getCase(1, 1).getPiece();
        p3 = Plateau.getCase(2, 0).getPiece();

        if (p1 != null && p2 != null && p3 != null &&
                p1.getCouleur().equals(p2.getCouleur()) &&
                p2.getCouleur().equals(p3.getCouleur())) {
            return true;
        }

        return false;
    }

    /**
     * Méthode appelée automatiquement lorsqu'un changement survient dans le modèle (Jeu).
     * <p>
     * Met à jour l'affichage du plateau et vérifie si la partie est terminée.
     * </p>
     *
     * @param o   L'objet observable (le Jeu).
     * @param arg Argument optionnel (non utilisé).
     */
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichageTicTacToe();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}