package modele.deplacements;

import modele.plateau.Case;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DecCasesAccessiblesTest {

    private static class StubDec extends DecCasesAccessibles {
        private final List<Case> toReturn;
        public StubDec(DecCasesAccessibles base, List<Case> toReturn) {
            super(base);
            this.toReturn = toReturn;
        }
        @Override
        public List<Case> getMesCA(Case c) {
            // return a copy to avoid external mutation issues
            return new ArrayList<>(toReturn);
        }
    }

    @Test
    public void testGetCAWithoutBase() {
        Case c = new Case(0, 0);
        List<Case> own = Arrays.asList(new Case(1, 1), new Case(2, 2));
        DecCasesAccessibles dec = new StubDec(null, own);

        List<Case> result = dec.getCA(c);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getPosX());
        assertEquals(1, result.get(0).getPosY());
        assertEquals(2, result.get(1).getPosX());
        assertEquals(2, result.get(1).getPosY());
    }

    @Test
    public void testGetCAWithBaseMergesLists() {
        Case c = new Case(0, 0);
        List<Case> baseList = Arrays.asList(new Case(3, 3));
        List<Case> own = Arrays.asList(new Case(1, 1), new Case(2, 2));
        DecCasesAccessibles base = new StubDec(null, baseList);
        DecCasesAccessibles dec = new StubDec(base, own);

        List<Case> result = dec.getCA(c);
        assertEquals(3, result.size());
        // First the own list, then base items appended
        assertEquals(1, result.get(0).getPosX());
        assertEquals(2, result.get(1).getPosX());
        assertEquals(3, result.get(2).getPosX());
    }

    @Test
    public void testGetCAWithBaseNullReturn() {
        Case c = new Case(0, 0);
        // Base that returns null to simulate edge-case; override Stub to do that
        DecCasesAccessibles base = new DecCasesAccessibles(null) {
            @Override
            public List<Case> getMesCA(Case c) {
                return null;
            }
        };
        List<Case> own = Arrays.asList(new Case(1, 0));
        DecCasesAccessibles dec = new StubDec(base, own);

        List<Case> result = dec.getCA(c);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPosX());
        assertEquals(0, result.get(0).getPosY());
    }
}
