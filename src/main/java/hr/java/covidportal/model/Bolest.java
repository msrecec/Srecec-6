package main.java.hr.java.covidportal.model;

import java.util.Objects;
import java.util.Set;

/**
 * Služi za definiranje instanci klase Bolest
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class Bolest extends ImenovaniEntitet {
    private Set<Simptom> simptomi;

    /**
     * Služi za instanciranje objekta klase <code>class Bolest</code>
     *
     * @param naziv
     * @param simptomi
     */

    public Bolest(String naziv, Set<Simptom> simptomi) {
        super(naziv);
        this.simptomi = simptomi;
    }

    /**
     * Uspoređuje elemente
     *
     * @param o objekt usporedbe
     * @return true ako su elementi jednaki
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bolest)) return false;
        Bolest bolest = (Bolest) o;
        return Objects.equals(getSimptomi(), bolest.getSimptomi());
    }

    /**
     * Radi hashcode
     *
     * @return cijeli broj hashcode
     */

    @Override
    public int hashCode() {
        return Objects.hash(getSimptomi());
    }

    /**
     * Vraća simptome klase <code>class Bolest</code>
     *
     * @return simptomi
     */

    public Set<Simptom> getSimptomi() {
        return simptomi;
    }

    /**
     * Postavlja simptome klase <code>class Bolest</code>
     *
     * @param simptomi
     */

    public void setSimptomi(Set<Simptom> simptomi) {
        this.simptomi = simptomi;
    }
}
