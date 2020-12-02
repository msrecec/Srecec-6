package main.java.hr.java.covidportal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Služi za definiranje instanci klase Osoba
 *
 * @author Mislav Srečec
 * @version 1.0
 */

public class Osoba {


    // Implementacija "Builder Pattern"

    /**
     * Služi za definiranje instanci klase Builder
     *
     * @author Mislav Srečec
     * @version 1.0
     */
    public static class Builder {
        private String ime, prezime;
        private Integer starost;
        private Zupanija zupanija;
        private Bolest zarazenBolescu;
        private List<Osoba> kontaktiraneOsobe;

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         *
         * @param ime ime osobe
         */

        public Builder(String ime) {
            this.ime = ime;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         * Vraća samu instancu
         *
         * @param prezime prezime osobe
         * @return
         */
        public Builder prezime(String prezime) {
            this.prezime = prezime;
            return this;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         * Vraća samu instancu
         *
         * @param starost starost osobe
         * @return instanca
         */

        public Builder starost(Integer starost) {
            this.starost = starost;
            return this;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         * Vraća samu instancu
         *
         * @param zupanija županija prebivališta osobe
         * @return instanca
         */

        public Builder zupanija(Zupanija zupanija) {
            this.zupanija = zupanija;
            return this;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         * Vraća samu instancu
         *
         * @param zarazenBolescu bolest kojom je osoba zaražena
         * @return instanca
         */

        public Builder zarazenBolescu(Bolest zarazenBolescu) {
            this.zarazenBolescu = zarazenBolescu;
            return this;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Builder</code>
         * Vraća samu instancu
         *
         * @param kontaktiraneOsobe osobe koje su bile u kontaktu sa osobom ove instance
         * @return instanca
         */

        public Builder kontaktiraneOsobe(List<Osoba> kontaktiraneOsobe) {
            this.kontaktiraneOsobe = kontaktiraneOsobe;
            return this;
        }

        /**
         * Služi za instanciranje objekta klase <code>class Osoba</code>
         * vraća instancu objekta klase osoba
         *
         * @return osoba
         */

        public Osoba build() {
            Osoba osoba = new Osoba();
            osoba.ime = this.ime;
            osoba.prezime = this.prezime;
            osoba.starost = this.starost;
            osoba.zupanija = this.zupanija;
            osoba.zarazenBolescu = this.zarazenBolescu;
            osoba.kontaktiraneOsobe = this.kontaktiraneOsobe;

            // Zaraza osoba koje su bile u kontaktu s ovom osobom

            if (!Objects.isNull(osoba.kontaktiraneOsobe)) {
                if (zarazenBolescu instanceof Virus virus) {
                    for (Osoba kontaktiranaOsoba : osoba.kontaktiraneOsobe) {
                        virus.prelazakZarazeNaOsobu(kontaktiranaOsoba);
                    }
                }
            }
            return osoba;
        }


    }

    // Class Fields

    private String ime, prezime;
    private Integer starost;
    private Zupanija zupanija;
    private Bolest zarazenBolescu;
    private List<Osoba> kontaktiraneOsobe;

    // Constructor is now private

    /**
     * Privatni konstruktor klase osoba
     */

    private Osoba() {

    }

    // Getters and setters

    /**
     * Vraća ispis podataka za instancu klase Osoba <code>class Osoba</code>
     *
     * @return String podataka za osobu
     */

    @Override
    public String toString() {

        String ispisKontaktiranihOsoba = "";

        if (Objects.isNull(kontaktiraneOsobe)) {
            ispisKontaktiranihOsoba = "Nema kontaktiranih osoba.\n";
        } else {
            for (Osoba kontaktiranaOsoba : kontaktiraneOsobe) {
                ispisKontaktiranihOsoba += kontaktiranaOsoba.getIme() + " " + kontaktiranaOsoba.getPrezime() + "\n";
            }
        }


        return "Ime i prezime: " + ime + " " + prezime + "\n" +
                "Starost: " + starost + "\n" +
                "Županija prebivališta: " + zupanija.getNaziv() + "\n" +
                "Zaražen bolešću: " + zarazenBolescu.getNaziv() + "\n" +
                "Kontaktirane osobe: \n" + ispisKontaktiranihOsoba;
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
        if (!(o instanceof Osoba)) return false;
        Osoba osoba = (Osoba) o;
        return Objects.equals(getIme(), osoba.getIme()) &&
                Objects.equals(getPrezime(), osoba.getPrezime()) &&
                Objects.equals(getStarost(), osoba.getStarost()) &&
                Objects.equals(getZupanija(), osoba.getZupanija()) &&
                Objects.equals(getZarazenBolescu(), osoba.getZarazenBolescu()) &&
                Objects.equals(getKontaktiraneOsobe(), osoba.getKontaktiraneOsobe());
    }

    /**
     * Radi HashCode
     *
     * @return integer hashcode
     */

    @Override
    public int hashCode() {
        return Objects.hash(getIme(), getPrezime(), getStarost(), getZupanija(), getZarazenBolescu(), getKontaktiraneOsobe());
    }




    /**
     * Vraća ime osobe
     *
     * @return ime
     */

    public String getIme() {
        return ime;
    }

    /**
     * Postavlja ime osobe
     *
     * @param ime
     */

    public void setIme(String ime) {
        this.ime = ime;
    }

    /**
     * Vraća prezime osobe
     *
     * @return prezime
     */

    public String getPrezime() {
        return prezime;
    }

    /**
     * Postavlja prezime osobe
     *
     * @param prezime
     */

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    /**
     * Vraća starost osobe
     *
     * @return starost
     */

    public Integer getStarost() {
        return starost;
    }

    /**
     * Postavlja starost osobe
     *
     * @param starost
     */

    public void setStarost(Integer starost) {
        this.starost = starost;
    }

    /**
     * Vraća Županiju osobe
     *
     * @return zupanija
     */

    public Zupanija getZupanija() {
        return zupanija;
    }

    /**
     * Postavlja županiju osobe
     *
     * @param zupanija
     */

    public void setZupanija(Zupanija zupanija) {
        this.zupanija = zupanija;
    }

    /**
     * Vraća bolest kojom je osoba zaražena
     *
     * @return zarazenBolescu
     */

    public Bolest getZarazenBolescu() {
        return zarazenBolescu;
    }

    /**
     * Postavlja bolest kojom je osoba zaražena
     *
     * @param zarazenBolescu
     */

    public void setZarazenBolescu(Bolest zarazenBolescu) {
        this.zarazenBolescu = zarazenBolescu;
    }

    /**
     * Vraća kontaktirane osobe
     *
     * @return kontaktiraneOsobe
     */

    public List<Osoba> getKontaktiraneOsobe() {
        return kontaktiraneOsobe;
    }

    /**
     * Postavlja kontaktirane osobe
     *
     * @param kontaktiraneOsobe
     */

    public void setKontaktiraneOsobe(List<Osoba> kontaktiraneOsobe) {
        this.kontaktiraneOsobe = kontaktiraneOsobe;
    }
}
