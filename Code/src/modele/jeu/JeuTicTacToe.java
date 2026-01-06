package modele.jeu;

import modele.jeu.Pieces.PionTicTacToe;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Implémentation du jeu de Morpion (Tic-Tac-Toe).
 * <p>
 * Cette classe gère les règles standards du Tic-Tac-Toe sur un plateau (généralement 3x3) :
 * <ul>
 * <li>Les joueurs posent tour à tour un symbole (Pion) sur une case vide.</li>
 * <li>Le premier joueur à aligner 3 symboles (ligne, colonne ou diagonale) gagne.</li>
 * <li>Si le plateau est rempli sans vainqueur, la partie est nulle.</li>
 * </ul>
 * Elle hérite de la classe abstraite {@link Jeu}.
 * </p>
 */
public class JeuTicTacToe extends Jeu {

    private JeuEventListener listener;

    /**
     * Définit l'écouteur d'événements pour ce jeu.
     * <p>
     * Permet de transmettre les informations (coup valide, victoire, erreur)
     * à l'interface graphique ou à la console.
     * </p>
     *
     * @param listener l'implémentation de {@link JeuEventListener}.
     */
    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    /**
     * Construit une nouvelle instance de jeu Tic-Tac-Toe.
     *
     * @param plateau le plateau de jeu (doit être de dimension 3x3 pour le jeu standard).
     */
    public JeuTicTacToe(Plateau plateau) {
        super(plateau);
    }

    /**
     * Tente de jouer un coup pour le joueur courant.
     * <p>
     * Contrairement aux échecs ou au Puissance 4, le coup n'est valide que si
     * la case ciblée est strictement <b>vide</b>.
     * </p>
     * <p>
     * La méthode effectue les actions suivantes :
     * <ol>
     * <li>Vérifie si la case est libre. Sinon, notifie une erreur.</li>
     * <li>Place un {@link PionTicTacToe} sur la case.</li>
     * <li>Notifie l'écouteur que le coup a été joué.</li>
     * <li>Vérifie si la partie est terminée (Victoire ou Nul).</li>
     * <li>Si la partie continue, passe la main à l'autre joueur.</li>
     * </ol>
     * </p>
     *
     * @param premierCoup l'objet contenant la case de destination.
     * @return {@code true} si le coup est valide et a été joué, {@code false} sinon.
     */
    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseArrivee = premierCoup.getArrivee();

        // Au TicTacToe, on doit placer une pièce sur une case vide
        if (caseArrivee == null || caseArrivee.getPiece() != null) {
            if (listener != null) listener.onCoupInvalide("Cette case est déjà occupée !");
            setChanged();
            notifyObservers();
            return false;
        }

        // Créer une nouvelle pièce et la placer
        new PionTicTacToe(joueurCourant.getCouleur().name(), plateau, caseArrivee);

        if (listener != null) listener.onCoupJoue(joueurCourant, caseArrivee);

        // Vérifier fin de partie
        boolean fin = estTermine();
        if (fin) {
            if (gagnant != null) {
                if (listener != null) listener.onPartieTerminee(gagnant);
            } else {
                if (listener != null) listener.onMatchNul();
            }
        } else {
            // Changer de joueur uniquement si la partie continue
            joueurCourant = (joueurCourant == JOUEUR_BLANC) ? JOUEUR_NOIR : JOUEUR_BLANC;
        }

        setChanged();
        notifyObservers();
        return true;
    }


    /**
     * Vérifie les conditions de victoire ou de match nul.
     * <p>
     * Cette méthode analyse le plateau pour trouver un alignement de 3 pièces identiques :
     * <ul>
     * <li>Sur les lignes horizontales.</li>
     * <li>Sur les colonnes verticales.</li>
     * <li>Sur la diagonale principale et la diagonale secondaire.</li>
     * </ul>
     * Si un alignement est trouvé, les cases concernées sont enregistrées dans {@code winningCells}
     * pour l'affichage. Si le plateau est plein sans alignement, la partie est nulle.
     * </p>
     *
     * @return {@code true} si la partie est finie, {@code false} sinon.
     */
    @Override
    public boolean estTermine() {
        clearWinningCells();
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        // Vérifier les lignes
        for (int x = 0; x < rows; x++) {
            Piece p1 = Plateau.getCase(x, 0).getPiece();
            Piece p2 = Plateau.getCase(x, 1).getPiece();
            Piece p3 = Plateau.getCase(x, 2).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                    p1.getCouleur().equals(p2.getCouleur()) &&
                    p2.getCouleur().equals(p3.getCouleur())) {
                gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                // marquer les cellules gagnantes
                winningCells[x][0] = true;
                winningCells[x][1] = true;
                winningCells[x][2] = true;
                return true;
            }
        }

        // Vérifier les colonnes
        for (int y = 0; y < cols; y++) {
            Piece p1 = Plateau.getCase(0, y).getPiece();
            Piece p2 = Plateau.getCase(1, y).getPiece();
            Piece p3 = Plateau.getCase(2, y).getPiece();

            if (p1 != null && p2 != null && p3 != null &&
                    p1.getCouleur().equals(p2.getCouleur()) &&
                    p2.getCouleur().equals(p3.getCouleur())) {
                gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                // marquer les cellules gagnantes
                winningCells[0][y] = true;
                winningCells[1][y] = true;
                winningCells[2][y] = true;
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
            gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
            winningCells[0][0] = true;
            winningCells[1][1] = true;
            winningCells[2][2] = true;
            return true;
        }

        // Vérifier la diagonale secondaire
        p1 = Plateau.getCase(0, 2).getPiece();
        p2 = Plateau.getCase(1, 1).getPiece();
        p3 = Plateau.getCase(2, 0).getPiece();

        if (p1 != null && p2 != null && p3 != null &&
                p1.getCouleur().equals(p2.getCouleur()) &&
                p2.getCouleur().equals(p3.getCouleur())) {
            gagnant = p1.getCouleur().equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
            winningCells[0][2] = true;
            winningCells[1][1] = true;
            winningCells[2][0] = true;
            return true;
        }

        // Vérifier si le plateau est plein
        boolean plateauPlein = true;
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                if (Plateau.getCase(x, y).getPiece() == null) {
                    plateauPlein = false;
                    break;
                }
            }
            if (!plateauPlein) break;
        }

        if (plateauPlein) {
            gagnant = null; // Match nul
            clearWinningCells();
            return true;
        }

        return false;
    }


    /**
     * Méthode d'affichage (non utilisée directement dans le modèle).
     * <p>
     * L'affichage est délégué aux gestionnaires de vue.
     * Cette méthode est laissée vide intentionnellement.
     * </p>
     */
    @Override
    protected void afficherPlateauEtTrait() {
        // Intentionnellement vide: la vue/handler s'occupe de l'affichage.
    }


    /**
     * Réinitialise la partie pour une nouvelle manche.
     * <p>
     * Vide le plateau, efface les indicateurs de victoire et redonne la main au joueur Blanc.
     * </p>
     */
    @Override
    public void reinitialiserPartie() {
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                Plateau.getCase(x, y).setPiece(null);
            }
        }
        clearWinningCells();
        gagnant = null;
        joueurCourant = JOUEUR_BLANC;
        setChanged();
        notifyObservers();
    }

}