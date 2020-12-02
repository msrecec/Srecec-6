package main.java.hr.java.covidportal.model;

import main.java.hr.java.covidportal.enumeracija.VrijednostSimptoma;

import java.util.Objects;

/**
 * Služi za definiranje instanci klase Simptom
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class Simptom extends ImenovaniEntitet {
    private VrijednostSimptoma vrijednost;

    /**
     * Služi za instanciranje objekta klase <code>class Simptom</code>
     *
     * @param naziv
     * @param vrijednost
     */

    public Simptom(String naziv, VrijednostSimptoma vrijednost) {
        super(naziv);
        this.vrijednost = vrijednost;
    }

    /**
     * Služi za duboku usporedbu po vrijednosti instanci objekata klase <code>class Simptom</code>
     * Vraća <code>true</code> ako su dva objekta ista po vrijednostima
     *
     * @param o
     * @return istinitost usporedbe
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simptom)) return false;
        Simptom simptom = (Simptom) o;
        return getNaziv().equals(simptom.getNaziv()) &&
                getVrijednost().equals(simptom.getVrijednost());
    }

    /**
     * Hashcode
     *
     * @return
     */

    @Override
    public int hashCode() {
        return Objects.hash(getNaziv(), getVrijednost());
    }

    /**
     * Vraća vrijednost simptoma
     *
     * @return vrijednost
     */

    public VrijednostSimptoma getVrijednost() {
        return vrijednost;
    }

    /**
     * Postavlja vrijednost simptoma
     *
     * @param vrijednost
     */

    public void setVrijednost(VrijednostSimptoma vrijednost) {
        this.vrijednost = vrijednost;
    }
}
