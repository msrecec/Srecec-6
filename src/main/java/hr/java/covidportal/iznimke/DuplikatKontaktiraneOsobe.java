package main.java.hr.java.covidportal.iznimke;

/**
 * Služi za definiranje iznimke koja se baca u slučaju duplikata kontaktiranih osoba
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class DuplikatKontaktiraneOsobe extends Exception {

    /**
     * Instancira objekt klase <code>DuplikatKontaktiraneOsobe</code> i šalje vrijednost stringa konstruktoru nadklase
     * <code>super("Unesena osoba je duplikat!");</code>
     */

    public DuplikatKontaktiraneOsobe() {
        super("Unesena osoba je duplikat!");
    }

    /**
     * Instancira objekt klase <code>DuplikatKontaktiraneOsobe</code> i šalje vrijednost stringa <code>String message</code> konstruktoru nadklase
     * <code>super(message);</code>
     *
     * @param message
     */

    public DuplikatKontaktiraneOsobe(String message) {
        super(message);
    }

    /**
     * Instancira objekt klase <code>DuplikatKontaktiraneOsobe</code> i šalje vrijednost stringa <code>String message</code> konstruktoru nadklase
     * <code>super(message);</code> i ujedno vrijednost drugog uzroka greške <code>Throwable cause</code>
     *
     * @param message
     * @param cause
     */

    public DuplikatKontaktiraneOsobe(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instancira objekt klase <code>DuplikatKontaktiraneOsobe</code> i šalje drugi uzrok greške <code>Throwable cause</code> konstruktoru nadklase
     * <code>super(cause);</code>
     *
     * @param cause
     */

    public DuplikatKontaktiraneOsobe(Throwable cause) {
        super(cause);
    }

}
