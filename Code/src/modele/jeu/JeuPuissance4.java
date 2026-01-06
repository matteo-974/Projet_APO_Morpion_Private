package modele.jeu;

import modele.jeu.Pieces.PionPuissance4;
import modele.plateau.Case;
import modele.plateau.Plateau;

/**
 * Implémentation du jeu Puissance 4.
 * <p>
 * Cette classe gère la logique métier spécifique au Puissance 4 :
 * <ul>
 * <li>La mécanique de <b>gravité</b> : les pions sont insérés dans une colonne et tombent à la position la plus basse disponible.</li>
 * <li>Les conditions de <b>victoire</b> : alignement de 4 pions de même couleur (horizontal, vertical ou diagonal).</li>
 * </ul>
 * Elle étend la classe abstraite {@link Jeu} et utilise un {@link JeuEventListener} pour communiquer avec l'interface.
 * </p>
 */
public class JeuPuissance4 extends Jeu {

    private JeuEventListener listener;

    /**
     * Définit l'écouteur d'événements pour ce jeu.
     * <p>
     * Cet écouteur permet de notifier la vue ou le contrôleur lors d'actions clés
     * (coup joué, coup invalide, fin de partie).
     * </p>
     *
     * @param listener l'implémentation de l'interface {@link JeuEventListener}.
     */
    public void setEventListener(JeuEventListener listener) {
        this.listener = listener;
    }

    /**
     * Construit une nouvelle instance de jeu Puissance 4.
     *
     * @param plateau le plateau de jeu sur lequel la partie se déroule (standard 6 lignes x 7 colonnes).
     */
    public JeuPuissance4(Plateau plateau) {
        super(plateau);
    }

    /**
     * Tente de jouer un coup pour le joueur courant en respectant la gravité.
     * <p>
     * Cette méthode :
     * <ol>
     * <li>Identifie la colonne ciblée par le {@code premierCoup}.</li>
     * <li>Cherche la première case vide dans cette colonne en partant du bas (gravité).</li>
     * <li>Si la colonne est pleine, notifie un coup invalide et retourne false.</li>
     * <li>Sinon, place un {@link PionPuissance4}, vérifie la fin de partie et passe le tour.</li>
     * </ol>
     * </p>
     *
     * @param premierCoup l'objet Coup contenant les informations sur la case ciblée (principalement la colonne).
     * @return {@code true} si le coup a été joué avec succès, {@code false} sinon (coup null, colonne pleine).
     */
    @Override
    public boolean jouerPartie(Coup premierCoup) {
        if (premierCoup == null) return false;
        Case caseArrivee = premierCoup.getArrivee();
        if (caseArrivee == null) return false;
        int col = caseArrivee.getPosY();

        // Chercher la première case vide en partant du bas
        Case caseLibre = null;
        for (int x = plateau.getSizeX() - 1; x >= 0; x--) {
            Case c = Plateau.getCase(x, col);
            if (c.getPiece() == null) {
                caseLibre = c;
                break;
            }
        }
        if (caseLibre == null) {
            if (listener != null) listener.onCoupInvalide("Colonne pleine !");
            setChanged();
            notifyObservers();
            return false;
        }

        // Placer le pion
        new PionPuissance4(joueurCourant.getCouleur().name(), plateau, caseLibre);
        if (listener != null) listener.onCoupJoue(joueurCourant, caseLibre);

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
     * Vérifie si la partie est terminée (victoire ou match nul).
     * <p>
     * La méthode parcourt le plateau pour détecter un alignement de 4 pions consécutifs de la même couleur :
     * <ul>
     * <li>Horizontalement</li>
     * <li>Verticalement</li>
     * <li>Diagonalement (descendante et montante)</li>
     * </ul>
     * Si une victoire est détectée, le champ {@code gagnant} est mis à jour et les cases gagnantes
     * sont marquées dans le tableau {@code winningCells}.
     * Si le plateau est plein sans vainqueur, la partie est déclarée nulle.
     * </p>
     *
     * @return {@code true} si la partie est terminée, {@code false} sinon.
     */
    @Override
    public boolean estTermine() {
        // Vérifier alignement de 4
        int rows = plateau.getSizeX();
        int cols = plateau.getSizeY();
        // S'assurer que le tableau des cellules gagnantes existe
        clearWinningCells();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Piece p = Plateau.getCase(x, y).getPiece();
                if (p == null) continue;
                String couleur = p.getCouleur();
                // Horizontal
                if (y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x][y + 1] = true;
                        winningCells[x][y + 2] = true;
                        winningCells[x][y + 3] = true;
                        return true;
                    }
                }
                // Vertical
                if (x <= rows - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x + k, y).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x + 1][y] = true;
                        winningCells[x + 2][y] = true;
                        winningCells[x + 3][y] = true;
                        return true;
                    }
                }
                // Diagonale descendante
                if (x <= rows - 4 && y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x + k, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x + 1][y + 1] = true;
                        winningCells[x + 2][y + 2] = true;
                        winningCells[x + 3][y + 3] = true;
                        return true;
                    }
                }
                // Diagonale montante
                if (x >= 3 && y <= cols - 4) {
                    boolean win = true;
                    for (int k = 1; k < 4; k++) {
                        Piece p2 = Plateau.getCase(x - k, y + k).getPiece();
                        if (p2 == null || !p2.getCouleur().equals(couleur)) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        gagnant = couleur.equalsIgnoreCase("BLANC") ? JOUEUR_BLANC : JOUEUR_NOIR;
                        winningCells[x][y] = true;
                        winningCells[x - 1][y + 1] = true;
                        winningCells[x - 2][y + 2] = true;
                        winningCells[x - 3][y + 3] = true;
                        return true;
                    }
                }
            }
        }
        // Vérifier si le plateau est plein pour match nul
        boolean plein = true;
        for (int y = 0; y < cols; y++) {
            if (Plateau.getCase(0, y).getPiece() == null) {
                plein = false;
                break;
            }
        }
        if (plein) {
            gagnant = null; // Match nul
            // aucune cellule gagnante
            clearWinningCells();
            return true;
        }
        return false;
    }

    /**
     * Méthode d'affichage (non utilisée directement dans le modèle).
     * <p>
     * L'affichage est délégué aux gestionnaires de vue via le pattern Observer/Listener.
     * Cette méthode est laissée vide intentionnellement.
     * </p>
     */
    @Override
    protected void afficherPlateauEtTrait() {
        // Intentionnellement vide: l'affichage est géré par la vue/handler.
    }

    /**
     * Réinitialise la partie pour commencer une nouvelle manche.
     * <p>
     * Cette méthode :
     * <ul>
     * <li>Vide toutes les cases du plateau.</li>
     * <li>Efface le tableau des cellules gagnantes.</li>
     * <li>Réinitialise le gagnant à null.</li>
     * <li>Redonne la main au joueur BLANC.</li>
     * <li>Notifie les observateurs du changement d'état.</li>
     * </ul>
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