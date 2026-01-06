package modele.jeu;

import modele.deplacements.DecPion;
import modele.jeu.Pieces.PiecesEchec.Cavalier;
import modele.jeu.Pieces.PiecesEchec.Fou;
import modele.jeu.Pieces.PiecesEchec.Pion;
import modele.jeu.Pieces.PiecesEchec.Reine;
import modele.jeu.Pieces.PiecesEchec.Roi;
import modele.jeu.Pieces.PiecesEchec.Tour;
import modele.plateau.Case;
import modele.plateau.Plateau;
import vue.Fenetres.FenetrePromotion;

import java.util.List;
import audio.SoundManager;

import static modele.plateau.Plateau.getCase;

/**
 * Implémentation du jeu d’Échecs.
 * <p>
 * Gère les règles spécifiques (déplacements, échec/échec et mat, roques,
 * promotion des pions) et l’orchestration des tours.
 * </p>
 */
public class JeuEchec extends Jeu{

    public JeuEchec(Plateau plateau) {
        super(plateau);
        reinitialiserPartie();   // Placer les pièces
    }


    @Override
    public boolean jouerPartie(Coup premierCoup) {
        Coup coup = premierCoup;
        while (!estTermine()) {
            Case caseDepart = coup.getDepart();
            Case caseArrivee = coup.getArrivee();
            Piece piece = caseDepart.getPiece();


            if (piece == null) {
                System.out.println("Erreur : aucune pièce à déplacer.");
                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }

            if (!piece.getCouleur().equalsIgnoreCase(joueurCourant.getCouleur().name())) {
                System.out.println("Ce n'est pas votre tour !");
                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }


            // Récupérer les cases accessibles via le dCA de la pièce
            List<Case> deplacementsPossibles = piece.getdCA().getCA(caseDepart);

            if (!deplacementsPossibles.contains(caseArrivee)) {
                System.out.println("Coup invalide pour " + piece.getClass().getSimpleName());
                SoundManager.playSound("Sounds/illegal.wav");
                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }

            if (piece instanceof Pion) {
                DecPion deplacement = (DecPion) piece.getdCA();
                Case priseEnPassant = deplacement.getCaseAPrendreEnPassant();
                if (priseEnPassant != null) {
                    priseEnPassant.setPiece(null);
                    System.out.println("Prise en passant !");
                    SoundManager.playSound("Sounds/capture.wav");
                }
            }

            if (piece instanceof Roi) {
                int dx = caseArrivee.getPosX() - caseDepart.getPosX();
                int dy = caseArrivee.getPosY() - caseDepart.getPosY();

                // Roque à droite
                if (dx == 0 && dy == 2) {
                    Case caseIntermediaire1 = Plateau.getCase(caseDepart.getPosX(), caseDepart.getPosY() + 1);
                    Case caseIntermediaire2 = Plateau.getCase(caseDepart.getPosX(), caseDepart.getPosY() + 2);

                    boolean case1Menacee = caseMenacee(caseIntermediaire1, joueurCourant);
                    boolean case2Menacee = caseMenacee(caseIntermediaire2, joueurCourant);

                    if (!case1Menacee && !case2Menacee) {
                        Case tourDepart = Plateau.getCase(caseDepart.getPosX(), 7);
                        Case tourArrivee = Plateau.getCase(caseDepart.getPosX(), 5);
                        Piece tour = tourDepart.getPiece();

                        if (tour instanceof Tour) {
                            tourDepart.setPiece(null);
                            tourArrivee.setPiece(tour);
                            tour.setCase(tourArrivee);
                            System.out.println("Petit Roque !");
                            SoundManager.playSound("Sounds/castle.wav");
                        }
                    } else {
                        System.out.println("Roque interdit : une case traversée est menacée !");
                        SoundManager.playSound("Sounds/illegal.wav");
                        setChanged();
                        notifyObservers();
                        coup = getCoup();
                        continue;
                    }
                }


                // Roque à gauche
                if (dx == 0 && dy == -2) {
                    Case caseIntermediaire1 = Plateau.getCase(caseDepart.getPosX(), caseDepart.getPosY() - 1);
                    Case caseIntermediaire2 = Plateau.getCase(caseDepart.getPosX(), caseDepart.getPosY() - 2);

                    boolean case1Menacee = caseMenacee(caseIntermediaire1, joueurCourant);
                    boolean case2Menacee = caseMenacee(caseIntermediaire2, joueurCourant);

                    if (!case1Menacee && !case2Menacee) {
                        Case tourDepart = Plateau.getCase(caseDepart.getPosX(), 0);
                        Case tourArrivee = Plateau.getCase(caseDepart.getPosX(), 3);
                        Piece tour = tourDepart.getPiece();

                        if (tour instanceof Tour) {
                            tourDepart.setPiece(null);
                            tourArrivee.setPiece(tour);
                            tour.setCase(tourArrivee);
                            System.out.println("Grand Roque !");
                            SoundManager.playSound("Sounds/castle.wav");
                        }
                    } else {
                        System.out.println("Roque interdit : une case traversée est menacée !");
                        SoundManager.playSound("Sounds/illegal.wav");
                        setChanged();
                        notifyObservers();
                        coup = getCoup();
                        continue;
                    }
                }
            }


            // --- Simulation du coup ---
            Piece pieceCapturee = caseArrivee.getPiece();   // Peut être null
            Case caseOrigine = caseDepart;
            Case caseDestination = caseArrivee;
            Case ancienneCaseDeLaPiece = piece.getCase();   // Important pour bien la restaurer

            // Déplacement temporaire
            caseOrigine.setPiece(null);
            caseDestination.setPiece(piece);
            piece.setCase(caseDestination);

            // Test échec
            if (estEnEchec(joueurCourant)) {
                // --- Annulation complète ---
                caseOrigine.setPiece(piece);
                piece.setCase(ancienneCaseDeLaPiece);         // On remet la pièce sur sa case d'origine
                caseDestination.setPiece(pieceCapturee);      // On remet la pièce capturée (si elle existait)

                System.out.println("Ce coup met votre roi en échec !");
                SoundManager.playSound("Sounds/illegal.wav");

                setChanged();
                notifyObservers();
                coup = getCoup();
                continue;
            }


            System.out.println("Coup joué: " + piece.getClass().getSimpleName() +
                    " à la position (" + caseArrivee.getPosX() + ", " +
                    caseArrivee.getPosY() + ")");
            SoundManager.playSound("Sounds/move-self.wav");


            if (piece instanceof Pion) {
                if (caseDestination.getPosX() == 0 || caseDestination.getPosX() == 7) {
                    // Appeler la fenêtre de promotion
                    FenetrePromotion fenetre = new FenetrePromotion(piece.getCouleur(), caseDestination);
                    Piece piecePromo = fenetre.getPiecePromo();

                    // Si une pièce a été choisie, on effectue la promotion
                    if (piecePromo != null) {
                        caseDestination.setPiece(piecePromo);
                        piecePromo.setCase(caseDestination);
                        System.out.println("Promotion en " + piecePromo.getClass().getSimpleName() + " !");
                        SoundManager.playSound("Sounds/promote.wav");
                    }
                }
            }


            if (piece instanceof Roi roi) {
                roi.setADejaBouge(true);
            }
            if (piece instanceof Tour tour) {
                tour.setADejaBouge(true);
            }

            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
            setChanged();
            notifyObservers();

            // Afficher le plateau mis à jour et indiquer le trait au joueur suivant
            afficherPlateauEtTrait();

            coup = getCoup(); // enchaîne le prochain coup
        }
        return false;
    }



    @Override
    public boolean estTermine() {
        Joueur joueur = joueurCourant;

        // Vérifie si le roi est en échec
        boolean enEchec = estEnEchec(joueur);

        // Pour chaque pièce du joueur courant
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = Plateau.getCase(x, y).getPiece();
                if (piece != null && piece.getCouleur().equalsIgnoreCase(joueur.getCouleur().name())) {
                    List<Case> deplacements = piece.getdCA().getCA(piece.getCase());

                    for (Case destination : deplacements) {
                        if (coupEstLegal(piece, destination, joueur)) {
                            return false; // Il reste au moins un coup légal
                        }
                    }
                }
            }
        }

        // Aucun coup légal trouvé
        if (enEchec) {
            gagnant = (joueur == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
            // Afficher la position finale avant d'annoncer
            afficherPlateauEtTrait();
            System.out.println("Échec et mat !");
            SoundManager.playSound("Sounds/game-end.wav");
        } else {
            gagnant = null; // Pat = match nul
            // Afficher la position finale avant d'annoncer
            afficherPlateauEtTrait();
            System.out.println("Pat !");
            SoundManager.playSound("Sounds/game-end.wav");
        }

        return true;
    }



    @Override
    protected void afficherPlateauEtTrait() {
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < rows; x++) {
            sb.append("| ");
            for (int y = 0; y < cols; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p == null) {
                    sb.append("O ");
                } else {
                    char letter = p.getClass().getSimpleName().charAt(0);
                    boolean isBlanc = p.getCouleur() != null && p.getCouleur().toUpperCase().startsWith("BL");
                    char colorChar = isBlanc ? 'b' : 'n';
                    sb.append(letter).append(colorChar);
                }
                if (y < cols - 1) sb.append(" ");
            }
            sb.append(" |");
            sb.append(System.lineSeparator());
        }
        System.out.print(sb.toString());
    }



    @Override
    public void reinitialiserPartie() {
    // Réinitialiser le plateau
        joueurCourant = JOUEUR_BLANC;
        gagnant = null;

        // Vider le plateau
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                Plateau.getCase(x, y).setPiece(null);
            }
        }

        initialiserPieces();
    }


    private boolean coupEstLegal(Piece piece, Case destination, Joueur joueur) {
        Case origine = piece.getCase();
        Piece pieceCapturee = destination.getPiece();

        // Simuler le coup
        origine.setPiece(null);
        destination.setPiece(piece);
        piece.setCase(destination);

        boolean roiEnDanger = estEnEchec(joueur);

        // Restaurer l'état du plateau
        destination.setPiece(pieceCapturee);
        origine.setPiece(piece);
        piece.setCase(origine);

        return !roiEnDanger;
    }



    public boolean estEnEchec(Joueur joueur) {
        Piece roi = null;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p instanceof Roi && p.getCouleur().equalsIgnoreCase(joueur.getCouleur().name())) {
                    roi = p;
                    break;
                }
            }
        }

        if (roi == null) {
            System.out.println("Erreur : Roi non trouvé pour " + joueur.getCouleur());
            return false;
        }

        Case caseRoi = roi.getCase();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p != null && !p.getCouleur().equalsIgnoreCase(joueur.getCouleur().name())) {
                    List<Case> acces = p.getdCA().getCA(p.getCase());
                    if (acces.contains(caseRoi)) {
                        return true; // le roi est attaqué
                    }
                }
            }
        }

        return false; // le roi n'est pas menacé
    }


    private boolean caseMenacee(Case c, Joueur joueur) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p != null && !p.getCouleur().equalsIgnoreCase(joueur.getCouleur().name())) {
                    if (p.getdCA().getCA(p.getCase()).contains(c)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void initialiserPieces() {
        // Rois
        new Roi("Blanc", plateau, getCase(7, 4));
        new Roi("Noir", plateau, getCase(0, 4));

        // Reines
        new Reine("Blanc", plateau, getCase(7, 3));
        new Reine("Noir", plateau, getCase(0, 3));

        // Tours
        new Tour("Blanc", plateau, getCase(7, 0));
        new Tour("Blanc", plateau, getCase(7, 7));
        new Tour("Noir", plateau, getCase(0, 0));
        new Tour("Noir", plateau, getCase(0, 7));

        // Fous
        new Fou("Blanc", plateau, getCase(7, 2));
        new Fou("Blanc", plateau, getCase(7, 5));
        new Fou("Noir", plateau, getCase(0, 2));
        new Fou("Noir", plateau, getCase(0, 5));

        // Cavaliers
        new Cavalier("Blanc", plateau, getCase(7, 1));
        new Cavalier("Blanc", plateau, getCase(7, 6));
        new Cavalier("Noir", plateau, getCase(0, 1));
        new Cavalier("Noir", plateau, getCase(0, 6));

        // Pions
        for (int i = 0; i < 8; i++) {
            new Pion("Blanc", plateau, getCase(6, i));
            new Pion("Noir", plateau, getCase(1, i));
        }
    }
    
}
