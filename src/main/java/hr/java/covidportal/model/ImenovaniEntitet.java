package main.java.hr.java.covidportal.model;

/**
 * Služi za definiranje Entiteta koji imaju naziv
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public abstract class ImenovaniEntitet {
    private String naziv;

    /**
     * Instancira objekt klase <code>class ImenovaniEntitet</code>
     *
     * @param naziv naziv imena elementa
     */

    public ImenovaniEntitet(String naziv) {
        this.naziv = naziv;
    }

    /**
     * Vraća naziv imenovanog entiteta
     *
     * @return naziv
     */

    public String getNaziv() {
        return naziv;
    }

    /**
     * Postavlja naziv imenovanog entiteta
     *
     * @param naziv nova vrijednost varijable <code>String naziv</code>
     */

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
}
