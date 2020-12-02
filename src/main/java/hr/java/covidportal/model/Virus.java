package main.java.hr.java.covidportal.model;

import java.util.Objects;
import java.util.Set;

/**
 * Služi za definiranje instanci klase Virus
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class Virus extends Bolest implements Zarazno {

    /**
     * Služi za instanciranje objekta klase <code>class Virus</code>
     *
     * @param naziv
     * @param simptomi
     */

    public Virus(String naziv, Set<Simptom> simptomi) {
        super(naziv, simptomi);
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
        if (!(o instanceof Virus)) return false;
        Virus virus = (Virus) o;
        return Objects.equals(getSimptomi(), virus.getSimptomi());
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
     * Služi za duboko kopiranje vrijednosti virusa na osobu instanciranjem novog virusa istih vrijednosti
     * "Zaraza osobe"
     *
     * @param osoba
     */

    // "Deep Copy" implementacija prelaska zaraze na drugu osobu
    @Override
    public void prelazakZarazeNaOsobu(Osoba osoba) {
        osoba.setZarazenBolescu(new Virus(this.getNaziv(), this.getSimptomi()));
    }

    @Override
    public String toString() {
        return "Virus: " + super.getNaziv();
    }
}
