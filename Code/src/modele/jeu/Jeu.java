package modele.jeu;

import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import audio.SoundManager;

/**
 * Modèle abstrait d’un jeu sur plateau.
 * <p>
 * Gère le cycle de jeu (thread, file de coups), l’état des joueurs et expose
 * des opérations communes aux variantes (affichage, réinitialisation, etc.).
 * </p>
 */
public abstract class Jeu extends Observable implements Runnable {
    static Plateau plateau;
    protected Coup buffCoup;
    private static List<Coup> historiqueCoups = new ArrayList<>();
    private static Jeu instance;

    protected final Joueur JOUEUR_BLANC = new Joueur(Joueur.Couleur.BLANC);
    protected final Joueur JOUEUR_NOIR = new Joueur(Joueur.Couleur.NOIR);
    protected Joueur joueurCourant = JOUEUR_BLANC;

    protected Joueur gagnant = null; // null = nul, sinon référence au joueur gagnant

    // Tableau marquant les cellules gagnantes (true = faire apparaître entre parenthèses)
    // Dimensions : [sizeX][sizeY]
    protected boolean[][] winningCells = null;



    public Jeu(Plateau plateau) {
        SoundManager.playSound("Sounds/game-start.wav");
        Jeu.plateau = plateau; // Initialisation du plateau
        new Thread(this).start();
        instance = this;
    }



    public abstract boolean jouerPartie(Coup premierCoup);


    public abstract boolean estTermine();



    public void setCoup(Coup c) {
        synchronized (this) {
            buffCoup = c;
            notify();
        }
    }

    public Coup getCoup() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return buffCoup;
        }
    }

    public Coup getDernierCoup() {
        if (historiqueCoups.isEmpty()) {
            return null;
        }
        return historiqueCoups.get(historiqueCoups.size() - 1);
    }



    // Affichage du plateau et indication du joueur dont c'est le tour
    protected abstract void afficherPlateauEtTrait(); 



    public Plateau getPlateau() {
        return plateau;
    }

    public static Jeu getInstance() {
        return instance;
    }

    public Joueur getGagnant() {
        return gagnant;
    }

    public Joueur getJoueurBlanc() {
        return JOUEUR_BLANC;
    }

    public Joueur getJoueurNoir() {
        return JOUEUR_NOIR;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public boolean[][] getWinningCells() {
        return winningCells;
    }

    public abstract void reinitialiserPartie();


    /**
     * Initialise (ou réinitialise) le tableau winningCells selon la taille du plateau.
     */
    public void clearWinningCells() {
        int sx = plateau.getSizeX();
        int sy = plateau.getSizeY();
        winningCells = new boolean[sx][sy];
    }



    @Override
    public void run() {
        while (true) {
            // Afficher le plateau courant avant d'attendre le prochain coup
            afficherPlateauEtTrait();
            Coup c = getCoup();
            jouerPartie(c);
        }
    }
}