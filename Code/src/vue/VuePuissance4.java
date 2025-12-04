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



public class VuePuissance4 extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 120; // nombre de pixel par case (dépend du type de jeu)
    
    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)



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
                        g2d.setColor(new Color(211,211,211));
                    } else {
                        g2d.setColor(new Color(0,0,0));
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



    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichagePuissance4();
        if (jeu.estTermine()) {
            afficherFinDePartie();
        }
    }
}
