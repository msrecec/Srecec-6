package main.java.hr.java.covidportal.iznimke;

/**
 * Služi za definiranje iznimke koja se baca u slučaju bolesti istih simptoma (duplikata)
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class BolestIstihSimptoma extends Exception {

    /**
     * Instancira objekt klase <code>BolestIstihSimptoma</code> i šalje vrijednost stringa konstruktoru nadklase
     * <code>super("Unesena bolest ima iste simptome kao prethodno unesena bolest!");</code>
     */

    public BolestIstihSimptoma() {
        super("Unesena bolest ima iste simptome kao prethodno unesena bolest!");
    }

    /**
     * Instancira objekt klase <code>BolestIstihSimptoma</code> i šalje vrijednost stringa<code>String message</code> konstruktoru nadklase
     * <code>super(message);</code>
     */

    public BolestIstihSimptoma(String message) {
        super(message);
    }

    /**
     * Instancira objekt klase <code>BolestIstihSimptoma</code> i šalje vrijednost stringa <code>String message</code> konstruktoru nadklase
     * <code>super(message);</code> i ujedno vrijednost drugog uzroka greške <code>Throwable cause</code>
     *
     * @param message
     * @param cause
     */

    public BolestIstihSimptoma(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instancira objekt klase <code>DuplikatKontaktiraneOsobe</code> i šalje drugi uzrok greške <code>Throwable cause</code> konstruktoru nadklase
     * <code>super(cause);</code>
     *
     * @param cause
     */

    public BolestIstihSimptoma(Throwable cause) {
        super(cause);
    }
}
