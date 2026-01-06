package vue;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import modele.jeu.*;
import modele.jeu.Pieces.PiecesEchec.Cavalier;
import modele.jeu.Pieces.PiecesEchec.Fou;
import modele.jeu.Pieces.PiecesEchec.Pion;
import modele.jeu.Pieces.PiecesEchec.Reine;
import modele.jeu.Pieces.PiecesEchec.Roi;
import modele.jeu.Pieces.PiecesEchec.Tour;
import modele.plateau.Case;
import modele.plateau.Plateau;
import vue.Fenetres.FenetreMenuPrincipal;

/**
 * Interface graphique pour le jeu d'Échecs.
 * <p>
 * Cette classe hérite de {@link JFrame} pour créer la fenêtre de jeu et implémente {@link Observer}
 * pour se mettre à jour automatiquement lorsque l'état du modèle {@link Jeu} change.
 * </p>
 * <p>
 * Ses principales responsabilités sont :
 * <ul>
 * <li>Afficher le plateau de jeu et les pièces d'échecs sous forme d'icônes.</li>
 * <li>Gérer les interactions utilisateur (clics souris) pour sélectionner et déplacer les pièces.</li>
 * <li>Afficher les déplacements possibles (indicateurs visuels).</li>
 * <li>Indiquer visuellement l'état d'échec (roi en rouge).</li>
 * <li>Gérer la fin de partie (affichage du vainqueur, score, menu de redémarrage).</li>
 * </ul>
 * </p>
 */
public class VueEchec extends JFrame implements Observer {

    /** Référence au plateau de jeu (modèle) pour accéder aux données des cases. */
    private Plateau plateau;
    /** Référence générique au contrôleur/modèle du jeu. */
    private Jeu jeu;
    /** Référence spécifique au modèle du jeu d'échecs. */
    private JeuEchec jeuEchec;

    /** Nombre de lignes de la grille affichée. */
    private final int sizeX;
    /** Nombre de colonnes de la grille affichée. */
    private final int sizeY;

    /** Taille en pixels du côté d'une case carrée. */
    private static final int pxCase = 100;

    // Icônes des pièces (Blanc / Noir)
    private ImageIcon icoRoiB, icoRoiN, icoReineB, icoReineN, icoTourB, icoTourN, icoFouB, icoFouN, icoCavalierB, icoCavalierN, icoPionB, icoPionN;

    /** Mémorisation de la première case cliquée (départ du coup). */
    private Case caseClic1;
    /** Mémorisation de la deuxième case cliquée (arrivée du coup). */
    private Case caseClic2;

    /** Tableau de composants graphiques représentant les cases de la grille. */
    private JLabel[][] tabJLabel;

    /** Liste des cases accessibles pour la pièce sélectionnée (pour l'affichage des aides). */
    private java.util.List<Case> casesAccessibles = new java.util.ArrayList<>();

    /**
     * Construit l'interface graphique du jeu d'échecs.
     * <p>
     * Initialise le plateau, charge les ressources graphiques (icônes), met en place les composants
     * et s'abonne aux notifications du modèle.
     * </p>
     *
     * @param _jeu L'instance du jeu (modèle) à observer et contrôler.
     */
    public VueEchec(Jeu _jeu) {
        this.jeu = _jeu;
        this.jeuEchec = (JeuEchec) _jeu;
        this.plateau = jeu.getPlateau();
        this.sizeX = plateau.getSizeX();
        this.sizeY = plateau.getSizeY();

        tabJLabel = new JLabel[sizeX][sizeY];

        chargerLesIcones();
        placerLesComposantsGraphiques();
        mettreAJourAffichageEchecs(); // Mettre à jour l'affichage initialement

        // Configurer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(sizeY * pxCase, sizeY * pxCase);
        this.setLocationRelativeTo(null);
        setVisible(true);

        jeu.addObserver(this);
    }

    /**
     * Charge les images des pièces d'échecs depuis les fichiers ressources.
     * <p>
     * Utilise {@link #chargerIcone(String)} pour chaque type de pièce.
     * </p>
     */
    private void chargerLesIcones() {
        icoRoiB = chargerIcone("Images/wK.png");
        icoRoiN = chargerIcone("Images/bK.png");
        icoReineB = chargerIcone("Images/wQ.png");
        icoReineN = chargerIcone("Images/bQ.png");
        icoTourB = chargerIcone("Images/wR.png");
        icoTourN = chargerIcone("Images/bR.png");
        icoFouB = chargerIcone("Images/wB.png");
        icoFouN = chargerIcone("Images/bB.png");
        icoCavalierB = chargerIcone("Images/wN.png");
        icoCavalierN = chargerIcone("Images/bN.png");
        icoPionB = chargerIcone("Images/wP.png");
        icoPionN = chargerIcone("Images/bP.png");
    }

    /**
     * Charge une image depuis un chemin donné et la redimensionne à la taille des cases.
     *
     * @param urlIcone Le chemin relatif vers le fichier image.
     * @return L'objet {@link ImageIcon} redimensionné.
     */
    private ImageIcon chargerIcone(String urlIcone) {
        ImageIcon icon = new ImageIcon(urlIcone);

        // Redimensionner l'icône
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        return resizedIcon;
    }

    /**
     * Initialise et place les composants graphiques (JLabels) représentant les cases du plateau.
     * <p>
     * Configure le layout en grille, initialise chaque case avec sa couleur de fond (damier),
     * et ajoute les écouteurs de souris pour gérer la sélection et le déplacement des pièces.
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

                // Pour Échecs, alterner les couleurs
                label.setBackground(Color.LIGHT_GRAY);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le contenu
                add(label); // Ajouter au JPanel principal
                tabJLabel[x][y] = label; // Ajouter au tableau des composants graphiques

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jeu.estTermine()) return;

                        Case caseClic = Plateau.getCase(xx, yy);
                        if (caseClic1 == null) {
                            if (caseClic.getPiece() != null) {
                                caseClic1 = caseClic;
                                Piece piece = caseClic1.getPiece();
                                casesAccessibles = piece.getdCA().getCA(caseClic1);
                                mettreAJourAffichageEchecs();
                            } else {
                                System.out.println("Veuillez sélectionner une case contenant une pièce.");
                                mettreAJourAffichageEchecs();
                            }
                        } else {
                            caseClic2 = caseClic;
                            Coup coup = new Coup(caseClic1, caseClic2);
                            jeu.setCoup(coup);
                            caseClic1 = null;
                            caseClic2 = null;
                            casesAccessibles.clear();
                            mettreAJourAffichageEchecs(); // remet toutes les couleurs correctement
                        }
                    }
                });

                tabJLabel[x][y] = label;
                this.add(label);
            }
        }
    }

    /**
     * Crée une icône représentant un indicateur de déplacement possible (rond gris).
     *
     * @return Une ImageIcon contenant un rond gris translucide.
     */
    private ImageIcon dessinerRondGris() {
        BufferedImage image = new BufferedImage(pxCase, pxCase, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        int diametre = pxCase / 4;
        int x = (pxCase - diametre) / 2;
        int y = (pxCase - diametre) / 2;
        g2d.fillOval(x, y, diametre, diametre);
        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Crée une icône composite pour indiquer une prise possible (rond rouge sur une pièce).
     *
     * @param iconePiece L'icône de la pièce à capturer.
     * @return Une nouvelle ImageIcon combinant la pièce et un indicateur rouge semi-transparent.
     */
    private ImageIcon dessinerRondRouge(ImageIcon iconePiece) {
        BufferedImage image = new BufferedImage(pxCase, pxCase, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Dessiner l'icône de la pièce
        g2d.drawImage(iconePiece.getImage(), 0, 0, null);

        // Dessiner le rond rouge par-dessus
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 0, 0, 180));
        int diametre = pxCase / 4;
        int x = (pxCase - diametre) / 2;
        int y = (pxCase - diametre) / 2;
        g2d.fillOval(x, y, diametre, diametre);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Rafraîchit l'ensemble de l'affichage du plateau.
     * <p>
     * Cette méthode :
     * <ul>
     * <li>Réinitialise les couleurs de fond (damier).</li>
     * <li>Met en évidence la case sélectionnée.</li>
     * <li>Place les icônes des pièces aux bonnes positions selon le modèle.</li>
     * <li>Affiche les indicateurs de coups possibles (ronds gris/rouges).</li>
     * <li>Met en évidence le Roi en rouge s'il est en échec.</li>
     * <li>Met à jour le titre de la fenêtre pour indiquer le trait.</li>
     * </ul>
     * </p>
     */
    private void mettreAJourAffichageEchecs() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case caseModele = Plateau.getCase(x, y);
                JLabel caseGraphique = tabJLabel[x][y];

                caseGraphique.setOpaque(true);
                caseGraphique.setBackground((x + y) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);

                if (caseModele.equals(caseClic1)) {
                    caseGraphique.setBackground(new Color(180, 180, 180)); // gris clair
                }


                if (caseModele.getPiece() != null) {
                    // Sélection de l'icône appropriée selon le type de pièce et sa couleur
                    if (caseModele.getPiece() instanceof Roi) {
                        Roi roi = (Roi) caseModele.getPiece();
                        if ("Blanc".equals(roi.getCouleur())) {
                            caseGraphique.setIcon(icoRoiB);
                        } else if ("Noir".equals(roi.getCouleur())) {
                            caseGraphique.setIcon(icoRoiN);
                        }
                    }

                    if (caseModele.getPiece() instanceof Reine) {
                        Reine reine = (Reine) caseModele.getPiece();
                        if ("Blanc".equals(reine.getCouleur())) {
                            caseGraphique.setIcon(icoReineB);
                        } else if ("Noir".equals(reine.getCouleur())) {
                            caseGraphique.setIcon(icoReineN);
                        }
                    }

                    if (caseModele.getPiece() instanceof Tour) {
                        Tour tour = (Tour) caseModele.getPiece();
                        if ("Blanc".equals(tour.getCouleur())) {
                            caseGraphique.setIcon(icoTourB);
                        } else if ("Noir".equals(tour.getCouleur())) {
                            caseGraphique.setIcon(icoTourN);
                        }
                    }

                    if (caseModele.getPiece() instanceof Fou) {
                        Fou fou = (Fou) caseModele.getPiece();
                        if ("Blanc".equals(fou.getCouleur())) {
                            caseGraphique.setIcon(icoFouB);
                        } else if ("Noir".equals(fou.getCouleur())) {
                            caseGraphique.setIcon(icoFouN);
                        }
                    }

                    if (caseModele.getPiece() instanceof Cavalier) {
                        Cavalier cavalier = (Cavalier) caseModele.getPiece();
                        if ("Blanc".equals(cavalier.getCouleur())) {
                            caseGraphique.setIcon(icoCavalierB);
                        } else if ("Noir".equals(cavalier.getCouleur())) {
                            caseGraphique.setIcon(icoCavalierN);
                        }
                    }

                    if (caseModele.getPiece() instanceof Pion) {
                        Pion pion = (Pion) caseModele.getPiece();
                        if ("Blanc".equals(pion.getCouleur())) {
                            caseGraphique.setIcon(icoPionB);
                        } else if ("Noir".equals(pion.getCouleur())) {
                            caseGraphique.setIcon(icoPionN);
                        }
                    }

                } else {
                    caseGraphique.setIcon(null); // Pas de pièce = icône vide
                }

                if (casesAccessibles.contains(caseModele)) {
                    if (caseModele.getPiece() == null) {
                        caseGraphique.setIcon(dessinerRondGris());
                    } else if (!caseModele.getPiece().getCouleur().equals(caseClic1.getPiece().getCouleur())) {
                        // Superposer rond rouge sur la pièce ennemie
                        ImageIcon iconeActuelle = (ImageIcon) caseGraphique.getIcon();
                        caseGraphique.setIcon(dessinerRondRouge(iconeActuelle));
                    }
                }
            }
        }


        Joueur joueur = jeu.getJoueurCourant();
        if (jeuEchec.estEnEchec(joueur)) {
            // Trouver le roi et le marquer en rouge
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    Case c = Plateau.getCase(x, y);
                    Piece p = c.getPiece();
                    if (p instanceof Roi && p.getCouleur().equalsIgnoreCase(joueur.getCouleur().name())) {
                        tabJLabel[x][y].setBackground(new Color(180, 0, 0));
                    }
                }
            }
        }

        setTitle("Trait au " + joueur.getCouleur().name().toLowerCase());
    }

    /**
     * Affiche une boîte de dialogue indiquant la fin de la partie.
     * <p>
     * Affiche le gagnant, le type de victoire (Mat ou Pat), et les scores.
     * Propose à l'utilisateur de rejouer, de retourner au menu principal ou de quitter.
     * </p>
     */
    private void afficherFinDePartie() {
        String message;
        Joueur gagnant = null;

        gagnant = jeu.getGagnant();
        if (gagnant != null) {
            message = "Échec et mat ! Le joueur " + gagnant.getCouleur() + " a gagné.";
            gagnant.ajouterPoint();
            System.out.println("Score: " + jeu.getJoueurBlanc().getPoints() + " - " + jeu.getJoueurNoir().getPoints());
        } else {
            message = "Pat ! Match nul.";
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
            mettreAJourAffichageEchecs();
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
     * Méthode appelée automatiquement lorsqu'un changement survient dans le modèle observé (Jeu).
     * <p>
     * Déclenche le rafraîchissement de l'affichage et vérifie si la partie est terminée.
     * </p>
     *
     * @param o   L'objet observable qui a notifié le changement (ici, le Jeu).
     * @param arg Un argument optionnel passé par notifyObservers (non utilisé ici).
     */
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichageEchecs();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}