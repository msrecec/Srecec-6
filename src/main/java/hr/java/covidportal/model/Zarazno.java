package main.java.hr.java.covidportal.model;

/**
 * Služi za definiranje sučelja Zarazno
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public interface Zarazno {

    /**
     * Služi za definiranje metode koja će implementirati funkcionalnost prelaska Zaraze na Osobu
     *
     * @param osoba
     */

    public void prelazakZarazeNaOsobu(Osoba osoba);
}
