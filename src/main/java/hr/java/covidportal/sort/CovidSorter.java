package main.java.hr.java.covidportal.sort;

import main.java.hr.java.covidportal.model.Zupanija;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Služi za definiranje usporedbe klase Zupanija
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class CovidSorter implements Comparator<Zupanija> {

    /**
     * Uspoređuje dvije županije po postotku zaraženih stanovnika
     *
     * @param z1 prva županija
     * @param z2 druga županija
     * @return vrijednost usporedbe dvije županije
     */

    @Override
    public int compare(Zupanija z1, Zupanija z2) {
        BigDecimal prviBrojZarazenih = new BigDecimal(z1.getBrojZarazenih().toString());
        BigDecimal drugiBrojZarazenih = new BigDecimal(z2.getBrojZarazenih().toString());
        BigDecimal prviBrojStanovnika = new BigDecimal(z1.getBrojStanovnika().toString());
        BigDecimal drugiBrojStanovnika = new BigDecimal(z2.getBrojStanovnika().toString());

        BigDecimal prviPostotakBrojaZarazenih = prviBrojZarazenih.divide(prviBrojStanovnika);
        BigDecimal drugiPostotakBrojaZarazenih = drugiBrojZarazenih.divide(drugiBrojStanovnika);

        if (prviPostotakBrojaZarazenih.compareTo(drugiPostotakBrojaZarazenih) > 0) {
            return 1;
        } else if (prviPostotakBrojaZarazenih.compareTo(drugiPostotakBrojaZarazenih) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
