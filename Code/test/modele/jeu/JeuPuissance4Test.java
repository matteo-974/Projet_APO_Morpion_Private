package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour le Puissance 4.
 * Vérifie la mécanique de gravité (les pions tombent en bas)
 * et la détection de victoire (alignement de 4).
 */
public class JeuPuissance4Test {

    /**
     * Teste que les pions tombent bien tout en bas de la colonne
     * et s'empilent correctement.
     */
    @Test
    public void testGraviteEtEmpilement() {

        Plateau plateau = new Plateau(6, 7);
        JeuPuissance4 jeu = new JeuPuissance4(plateau);

        // On cible la colonne 0.
        // Peu importe la ligne indiquée dans le Coup (ici 0),
        // le jeu doit faire tomber le pion à la ligne 5 (le fond).
        Case colonne0 = Plateau.getCase(0, 0);

        // When: BLANC joue dans la colonne 0
        jeu.jouerPartie(new Coup(null, colonne0));

        // Then: Le pion doit être à la ligne 5 (tout en bas)
        Case caseFond = Plateau.getCase(5, 0);
        assertNotNull("La case du fond (5,0) doit être occupée", caseFond.getPiece());
        assertEquals("C'est un pion BLANC", "BLANC", caseFond.getPiece().getCouleur());

        // La case juste au-dessus (4,0) doit être encore vide
        assertNull("La case (4,0) doit être vide", Plateau.getCase(4, 0).getPiece());

        // When: NOIR joue aussi dans la colonne 0 (Empilement)
        jeu.jouerPartie(new Coup(null, colonne0));

        // Then: Le pion doit être à la ligne 4 (juste au-dessus du précédent)
        Case caseEmpilee = Plateau.getCase(4, 0);
        assertNotNull("La case (4,0) doit être occupée maintenant", caseEmpilee.getPiece());
        assertEquals("C'est un pion NOIR", "NOIR", caseEmpilee.getPiece().getCouleur());
    }

    /**
     * Teste une victoire verticale simple.
     * BLANC aligne 4 pions dans la colonne 0.
     */
    @Test
    public void testVictoireVerticale() {
        Plateau plateau = new Plateau(6, 7);
        JeuPuissance4 jeu = new JeuPuissance4(plateau);

        Case col0 = Plateau.getCase(0, 0); // Pour BLANC
        Case col1 = Plateau.getCase(0, 1); // Pour NOIR (il joue à côté pour ne pas gêner)

        // On simule les tours :
        // Tour 1
        jeu.jouerPartie(new Coup(null, col0)); // Blanc (5,0)
        jeu.jouerPartie(new Coup(null, col1)); // Noir (5,1)

        // Tour 2
        jeu.jouerPartie(new Coup(null, col0)); // Blanc (4,0)
        jeu.jouerPartie(new Coup(null, col1)); // Noir (4,1)

        // Tour 3
        jeu.jouerPartie(new Coup(null, col0)); // Blanc (3,0)
        jeu.jouerPartie(new Coup(null, col1)); // Noir (3,1)

        // Vérification avant le coup gagnant
        assertFalse("La partie ne devrait pas être finie", jeu.estTermine());

        // Tour 4 (Gagnant)
        jeu.jouerPartie(new Coup(null, col0)); // Blanc (2,0) -> ALIGNEMENT DE 4 !

        // Vérifications finales
        assertTrue("La partie doit être terminée", jeu.estTermine());
        assertNotNull("Il doit y avoir un gagnant", jeu.getGagnant());
        assertEquals("Le gagnant est BLANC", jeu.getJoueurBlanc(), jeu.getGagnant());
    }
}