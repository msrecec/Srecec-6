package main.java.hr.java.covidportal.genericsi;

import main.java.hr.java.covidportal.model.Osoba;
import main.java.hr.java.covidportal.model.Virus;

import java.io.Serializable;
import java.util.List;

/**
 * Služi za definiranje instanci klase KlinikaZaInfektivneBolesti
 *
 * @author Mislav Srečec
 * @version 1.0
 *
 * @param <T> Virus ili klasa koja nasljeđuje klasu virus
 * @param <S> Osoba ili klasa koja nasljeđuje klasu osoba
 */

public class KlinikaZaInfektivneBolesti <T extends Virus, S extends Osoba> implements Serializable {
    private List<T> uneseniVirusi;
    private List<S> osobeZarazeneUnesenimVirusima;

    /**
     * Služi za instanciranje objekta klase <code>class KlinikaZaInfektivneBolesti</code>
     *
     * @param uneseniVirusi
     * @param osobeZarazeneUnesenimVirusima
     */

    public KlinikaZaInfektivneBolesti(List<T> uneseniVirusi, List<S> osobeZarazeneUnesenimVirusima) {
        this.uneseniVirusi = uneseniVirusi;
        this.osobeZarazeneUnesenimVirusima = osobeZarazeneUnesenimVirusima;
    }

    /**
     * Vraća instancu liste koja sadrzi klase <code>class uneseniVirusi</code>
     *
     * @return
     */

    public List<T> getUneseniVirusi() {
        return uneseniVirusi;
    }

    /**
     * Postavlja instancu liste koja sadrzi klase <code>class uneseniVirusi</code>
     *
     * @param uneseniVirusi
     */

    public void setUneseniVirusi(List<T> uneseniVirusi) {
        this.uneseniVirusi = uneseniVirusi;
    }

    /**
     * Vraća instancu liste koja sadrzi klase <code>class getOsobeZarazeneUnesenimVirusima</code>
     *
     * @return
     */

    public List<S> getOsobeZarazeneUnesenimVirusima() {
        return osobeZarazeneUnesenimVirusima;
    }

    /**
     * Postavlja instancu liste koja sadrzi klase <code>class setOsobeZarazeneUnesenimVirusima</code>
     *
     * @param osobeZarazeneUnesenimVirusima
     */

    public void setOsobeZarazeneUnesenimVirusima(List<S> osobeZarazeneUnesenimVirusima) {
        this.osobeZarazeneUnesenimVirusima = osobeZarazeneUnesenimVirusima;
    }

    /**
     * Dodaje novi virus u listu  <code>List<T> uneseniVirusi</code>
     *
     * @param novVirus
     */

    public void addVirusToList(T novVirus) {
        this.uneseniVirusi.add(novVirus);
    }

    /**
     * Dodaje novu osobu u listu  <code>List<S> osobeZarazeneUnesenimVirusima</code>
     *
     * @param novaOsoba
     */

    public void addOsobaToList(S novaOsoba) {
        this.osobeZarazeneUnesenimVirusima.add(novaOsoba);
    }
}
