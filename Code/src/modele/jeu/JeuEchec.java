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
import java.util.List;

import static modele.plateau.Plateau.getCase;

/**
 * Implémentation du jeu d’Échecs.
 * <p>
 * Gère les règles spécifiques (déplacements, échec/échec et mat, roques,
 * promotion des pions) et l’orchestration des tours.
 * </p>
 */
/**
 * Implémentation du jeu d’Échecs (modèle pur, sans IHM/son).
 * <p>
 * Cette classe respecte strictement le pattern MVC:
 * - aucune écriture console ni lecture clavier,
 * - aucun accès au son ou aux fenêtres Swing,
 * - publication d’événements via {@link JeuEventListener} pour que la vue/contrôleur réagisse.
 * La méthode jouerPartie traite un seul coup à la fois (pas de boucle bloquante).
 * </p>
 */
public class JeuEchec extends Jeu{

    private JeuEventListener listener;

    /**
     * Définit le listener d'événements pour ce jeu d'échecs.
     * @param listener implémentation de JeuEventListener (vue/contrôleur)
     */
    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    /**
     * Construit un modèle d’échecs sur le plateau fourni et place les pièces.
     * @param plateau plateau 8x8 (ou de taille conforme) sur lequel jouer
     */
    public JeuEchec(Plateau plateau) {
        super(plateau);
        reinitialiserPartie();   // Placer les pièces
    }


/**
     * Traite un seul coup d’échecs (sans boucle). Valide la légalité (y compris échec, roques,
     * prise en passant et promotion automatique en Reine) puis met à jour l’état.
     * Notifie le listener pour les coups joués/invalides et la fin de partie.
     * @param premierCoup coup à jouer (cases départ et arrivée obligatoires)
     * @return true si le coup a été appliqué, false sinon
     */
    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseDepart = premierCoup.getDepart();
        Case caseArrivee = premierCoup.getArrivee();
        if (caseDepart == null || caseArrivee == null) {
            if (listener != null) listener.onCoupInvalide("Coup incomplet (départ/arrivée manquants)");
            setChanged();
            notifyObservers();
            return false;
        }

        Piece piece = caseDepart.getPiece();
        if (piece == null) {
            if (listener != null) listener.onCoupInvalide("Aucune pièce à déplacer");
            setChanged();
            notifyObservers();
            return false;
        }
        if (!piece.getCouleur().equalsIgnoreCase(joueurCourant.getCouleur().name())) {
            if (listener != null) listener.onCoupInvalide("Ce n'est pas votre tour");
            setChanged();
            notifyObservers();
            return false;
        }

        // Vérifier que la destination est accessible selon la pièce
        List<Case> deplacementsPossibles = piece.getdCA().getCA(caseDepart);
        if (!deplacementsPossibles.contains(caseArrivee)) {
            if (listener != null) listener.onCoupInvalide("Coup invalide pour " + piece.getClass().getSimpleName());
            setChanged();
            notifyObservers();
            return false;
        }

        // Sauvegardes pour simulation/retour arrière
        Piece pieceCapturee = caseArrivee.getPiece();
        Case origine = caseDepart;
        Case destination = caseArrivee;
        Piece pieceEnPassantCapturee = null;

        // Gérer cas spéciaux AVANT validation finale (prise en passant, roque)
        boolean roqueDroite = false;
        boolean roqueGauche = false;
        Case tourDepart = null;
        Case tourArrivee = null;

        if (piece instanceof Roi) {
            int dx = destination.getPosX() - origine.getPosX();
            int dy = destination.getPosY() - origine.getPosY();
            if (dx == 0 && dy == 2) { // petit roque
                roqueDroite = true;
                Case c1 = Plateau.getCase(origine.getPosX(), origine.getPosY() + 1);
                Case c2 = Plateau.getCase(origine.getPosX(), origine.getPosY() + 2);
                if (caseMenacee(c1, joueurCourant) || caseMenacee(c2, joueurCourant)) {
                    if (listener != null) listener.onCoupInvalide("Roque interdit: case traversée menacée");
                    setChanged();
                    notifyObservers();
                    return false;
                }
                tourDepart = Plateau.getCase(origine.getPosX(), 7);
                tourArrivee = Plateau.getCase(origine.getPosX(), 5);
            } else if (dx == 0 && dy == -2) { // grand roque
                roqueGauche = true;
                Case c1 = Plateau.getCase(origine.getPosX(), origine.getPosY() - 1);
                Case c2 = Plateau.getCase(origine.getPosX(), origine.getPosY() - 2);
                if (caseMenacee(c1, joueurCourant) || caseMenacee(c2, joueurCourant)) {
                    if (listener != null) listener.onCoupInvalide("Roque interdit: case traversée menacée");
                    setChanged();
                    notifyObservers();
                    return false;
                }
                tourDepart = Plateau.getCase(origine.getPosX(), 0);
                tourArrivee = Plateau.getCase(origine.getPosX(), 3);
            }
        }

        // Gestion de la prise en passant pendant simulation
        Case casePriseEnPassant = null;
        if (piece instanceof Pion) {
            DecPion deplacement = (DecPion) piece.getdCA();
            casePriseEnPassant = deplacement.getCaseAPrendreEnPassant();
        }

        // Simulation du coup pour vérifier l'échec
        origine.setPiece(null);
        if (piece instanceof Pion && casePriseEnPassant != null && destination.getPiece() == null && destination.getPosY() != origine.getPosY()) {
            // Mouvement diagonal sans pièce: c'est une prise en passant
            pieceEnPassantCapturee = casePriseEnPassant.getPiece();
            casePriseEnPassant.setPiece(null);
        }
        destination.setPiece(piece);
        piece.setCase(destination);
        // Déplacer tour pour roque pendant simulation (optionnel mais plus correct)
        Piece tourSauvegardee = null;
        if (roqueDroite || roqueGauche) {
            tourSauvegardee = (tourDepart != null) ? tourDepart.getPiece() : null;
            if (tourDepart != null && tourArrivee != null && tourSauvegardee instanceof Tour) {
                tourDepart.setPiece(null);
                tourArrivee.setPiece(tourSauvegardee);
                tourSauvegardee.setCase(tourArrivee);
            }
        }

        boolean illegal = estEnEchec(joueurCourant);

        // Restaurer l'état après simulation
        destination.setPiece(pieceCapturee);
        origine.setPiece(piece);
        piece.setCase(origine);
        if (pieceEnPassantCapturee != null && casePriseEnPassant != null) {
            casePriseEnPassant.setPiece(pieceEnPassantCapturee);
        }
        if (roqueDroite || roqueGauche) {
            if (tourDepart != null && tourArrivee != null && tourSauvegardee instanceof Tour) {
                tourArrivee.setPiece(null);
                tourDepart.setPiece(tourSauvegardee);
                tourSauvegardee.setCase(tourDepart);
            }
        }

        if (illegal) {
            if (listener != null) listener.onCoupInvalide("Ce coup laisse votre roi en échec");
            setChanged();
            notifyObservers();
            return false;
        }

        // Appliquer le coup réellement
        origine.setPiece(null);
        if (piece instanceof Pion && casePriseEnPassant != null && destination.getPiece() == null && destination.getPosY() != origine.getPosY()) {
            // prise en passant effective
            casePriseEnPassant.setPiece(null);
        }
        destination.setPiece(piece);
        piece.setCase(destination);

        // Roque effectif: déplacer la tour
        if (roqueDroite || roqueGauche) {
            Piece tour = (tourDepart != null) ? tourDepart.getPiece() : null;
            if (tour instanceof Tour && tourArrivee != null) {
                tourDepart.setPiece(null);
                tourArrivee.setPiece(tour);
                tour.setCase(tourArrivee);
            }
        }

        // Promotion automatique en Reine
        if (piece instanceof Pion) {
            if (destination.getPosX() == 0 || destination.getPosX() == 7) {
                // Remplacer le pion par une Reine
                destination.setPiece(null);
                new Reine(piece.getCouleur(), plateau, destination);
            }
        }

        // Marquer les pièces qui ont bougé
        if (piece instanceof Roi roi) {
            roi.setADejaBouge(true);
        }
        if (piece instanceof Tour tour) {
            tour.setADejaBouge(true);
        }

        if (listener != null) listener.onCoupJoue(joueurCourant, destination);

        // Vérifier fin de partie
        boolean fin = estTermine();
        if (fin) {
            if (gagnant != null) {
                if (listener != null) listener.onPartieTerminee(gagnant);
            } else {
                if (listener != null) listener.onMatchNul();
            }
        } else {
            // Alterner le joueur
            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
        }

        setChanged();
        notifyObservers();
        return true;
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
            gagnant = (joueur == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC; // échec et mat
        } else {
            gagnant = null; // pat
        }
        return true;
    }



    @Override
    protected void afficherPlateauEtTrait() {
        // Intentionnellement vide : l'affichage est géré par la vue/handler via JeuEventListener.
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
            // Roi introuvable pour ce joueur (état invalide), considérer comme non en échec.
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
