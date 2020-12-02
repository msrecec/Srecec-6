package main.java.hr.java.covidportal.model;

/**
 * Služi za definiranje Entiteta koji imaju naziv
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public abstract class ImenovaniEntitet {
    private Long id;
    private String naziv;

    /**
     * Instancira objekt klase <code>class ImenovaniEntitet</code>
     *
     * @param naziv naziv imena elementa
     */

    public ImenovaniEntitet(Long id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    /**
     * Vraća id imenovanog entiteta
     *
     * @return id
     */

    public Long getId() {
        return id;
    }

    /**
     * Postavlja id imenovanog entiteta
     *
     * @param id nova vrijednost varijable <code>Long id</code>
     */

    public void setId(Long id) {
        this.id = id;
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

