package modele.jeu;

import modele.plateau.Plateau;
import modele.plateau.Case;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour la logique principale du Tic-Tac-Toe.
 *
 * Scénario: le joueur BLANC aligne trois pions sur la première ligne
 * (0,0), (0,1), (0,2) tandis que NOIR joue ailleurs.
 * On vérifie qu'après le troisième coup BLANC, la partie est terminée
 * et que le gagnant est bien le joueur BLANC.
 */
public class JeuTicTacToeTest {

    @Test
    public void testVictoireLigneHorizontalePourBlanc() {
        // Given: un plateau 3x3 et un jeu TicTacToe
        Plateau plateau = new Plateau(3, 3);
        JeuTicTacToe jeu = new JeuTicTacToe(plateau);

        // Etat initial: la partie ne doit pas être terminée
        assertFalse(jeu.estTermine());

        // When: BLANC joue (0,0)
        assertTrue(jeu.jouerPartie(new Coup(null, Plateau.getCase(0, 0))));
        assertFalse(jeu.estTermine());

        // NOIR joue ailleurs
        assertTrue(jeu.jouerPartie(new Coup(null, Plateau.getCase(1, 0))));
        assertFalse(jeu.estTermine());

        // BLANC joue (0,1)
        assertTrue(jeu.jouerPartie(new Coup(null, Plateau.getCase(0, 1))));
        assertFalse(jeu.estTermine());

        // NOIR joue ailleurs
        assertTrue(jeu.jouerPartie(new Coup(null, Plateau.getCase(1, 1))));
        assertFalse(jeu.estTermine());

        // BLANC joue (0,2) -> devrait gagner
        assertTrue(jeu.jouerPartie(new Coup(null, Plateau.getCase(0, 2))));

        // la partie est terminée et le gagnant est BLANC
        assertTrue("La partie devrait être terminée après 3 X en ligne", jeu.estTermine());
        assertNotNull("Un gagnant devrait être défini", jeu.getGagnant());
        assertSame("Le gagnant attendu est le joueur BLANC", jeu.getJoueurBlanc(), jeu.getGagnant());
    }
}
