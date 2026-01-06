package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour la logique du Jeu d'Échec.
 * * Scénario testé Fool's Mate
 * * Blancs : Pion f2 -> f3
 * Noirs  : Pion e7 -> e5
 * Blancs : Pion g2 -> g4
 * Noirs  : Reine d8 -> h4 (Echec et Mat)
 */
public class JeuEchecTest {

    @Test
    public void testMatDuLion() {
        // On crée un plateau classique 8x8 et le jeu d'échecs
        Plateau plateau = new Plateau(8, 8);
        JeuEchec jeu = new JeuEchec(plateau);

        // Vérifications initiales
        assertFalse("La partie ne doit pas être terminée au début", jeu.estTermine());
        assertEquals("C'est aux Blancs de commencer", jeu.getJoueurBlanc(), jeu.getJoueurCourant());

        Case f2 = Plateau.getCase(6, 5);
        Case f3 = Plateau.getCase(5, 5);

        boolean coup1Valide = jeu.jouerPartie(new Coup(f2, f3));
        assertTrue("Le coup f2-f3 devrait être valide", coup1Valide);
        assertFalse("La partie continue", jeu.estTermine());
        assertEquals("C'est aux Noirs de jouer", jeu.getJoueurNoir(), jeu.getJoueurCourant());

        Case e7 = Plateau.getCase(1, 4);
        Case e5 = Plateau.getCase(3, 4);

        boolean coup2Valide = jeu.jouerPartie(new Coup(e7, e5));
        assertTrue("Le coup e7-e5 devrait être valide", coup2Valide);
        assertFalse("La partie continue", jeu.estTermine());

        Case g2 = Plateau.getCase(6, 6);
        Case g4 = Plateau.getCase(4, 6);

        boolean coup3Valide = jeu.jouerPartie(new Coup(g2, g4));
        assertTrue("Le coup g2-g4 devrait être valide", coup3Valide);
        assertFalse("La partie continue", jeu.estTermine());

        Case d8 = Plateau.getCase(0, 3); // Reine noire
        Case h4 = Plateau.getCase(4, 7); // Case d'attaque
        boolean coup4Valide = jeu.jouerPartie(new Coup(d8, h4));
        assertTrue("Le coup Reine d8-h4 devrait être valide", coup4Valide);

        assertTrue("La partie devrait être terminée (Echec et Mat)", jeu.estTermine());
        assertNotNull("Il doit y avoir un gagnant", jeu.getGagnant());
        assertEquals("Le gagnant doit être le joueur NOIR", jeu.getJoueurNoir(), jeu.getGagnant());
    }
}