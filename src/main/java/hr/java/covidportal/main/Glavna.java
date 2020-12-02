package main.java.hr.java.covidportal.main;

import main.java.hr.java.covidportal.enumeracija.VrijednostSimptoma;
import main.java.hr.java.covidportal.genericsi.KlinikaZaInfektivneBolesti;
import main.java.hr.java.covidportal.iznimke.BolestIstihSimptoma;
import main.java.hr.java.covidportal.iznimke.DuplikatKontaktiraneOsobe;
import main.java.hr.java.covidportal.model.*;
import main.java.hr.java.covidportal.sort.CovidSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSOutput;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Služi za unos Županija, Simptoma, Bolesti, Osoba i služi za ispis Osoba
 *
 * @author Mislav Srečec
 * @version 3.0
 * @see <a href="https://grader.tvz.hr/">Odrediste svih verzija</a>
 */

public class Glavna {

    private static final Logger logger = LoggerFactory.getLogger(Glavna.class);

    /**
     * Služi za pokretanje programa koji će od korisnika tražiti unos Županija <code>Zupanija[] zupanije</code>,
     * Simptoma <code>Simptom[] simptomi</code>, Bolesti <code>Bolest[] bolesti</code>
     * preko metoda <code>unosZupanija(input, zupanije);</code> <code>unosSimptoma(input, simptomi);</code> i <code>unosBolesti(input, simptomi, bolesti);</code>
     * na osnovi kojih će tražiti unos Osoba <code>Osoba[] osobe</code> preko metode <code>unosOsoba(input, zupanije, bolesti, osobe);</code>
     *
     * @param args argumenti komandne linije (ne koriste se)
     */

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        SortedSet<Zupanija> zupanije = new TreeSet<>(new CovidSorter());
        Set<Simptom> simptomi = new HashSet<>();
        Set<Bolest> bolesti = new HashSet<>();
        List<Osoba> osobe = new ArrayList<>();
        Map<Bolest, List<Osoba>> osobeZarazeneVirusima = new HashMap<>();

        // Unos Zupanija

        unosZupanija(input, zupanije);

        // Unos Simptoma

        unosSimptoma(input, simptomi);

        // Unos Bolesti

        unosBolesti(input, simptomi, bolesti);

        // Unos osoba

        unosOsoba(input, zupanije, bolesti, osobe);

//
//
//        osobe.stream()
//                .sorted(((Comparator
//                        .comparing(Osoba::getPrezime)
//                        .thenComparing(Osoba::getKorisnickoIme))))
//                .forEach(System.out::println);
//
//
//
//        System.out.println(osobe.stream()
//                .min(Comparator
//                        .comparing(Osoba::getIme)
//                        .thenComparing(Osoba::getPrezime)));
//        System.out.println(osobe.stream()
//                .max(Comparator
//                        .comparing(Osoba::getPrezime)
//                        .thenComparing(Osoba::getIme)));


        // Populacija Mape OsobeZarazeneVirusima

        populacijaMapeOsobeZarazeneVirusima(osobe, osobeZarazeneVirusima);

        // Ispis osoba

        ispisOsoba(osobe);

        // Ispis Virusa/Bolesti i osoba koje su njima zaražene

        ispisVirusaIOsobaZarazenihVirusima(osobeZarazeneVirusima);

        // Ispis županije sa najviše zaraženih

        ispisZupanijeSaNajviseZarazenih(zupanije);

        // Izvedba pete laboratorijske vjezbe

        izvedbaPetogLabosa(bolesti, osobe, input);

    }

    // Metoda izvedbe pete laboratorijske vjezbe

    /**
     * Izvodi sve zadatke navedene u petoj laboratorijskoj vjezbi
     *
     * @param bolesti unesene bolesti
     * @param osobe uneseni ljudi oboljeli od bolesti
     */

    private static void izvedbaPetogLabosa(Set<Bolest> bolesti, List<Osoba> osobe, Scanner sc) {
        KlinikaZaInfektivneBolesti<Virus, Osoba> klinika;
        // Zadatak 2 - instanciranje klinike

        klinika = new KlinikaZaInfektivneBolesti(
        bolesti
            .stream()
            .filter(el -> el instanceof Virus)
            .collect(Collectors.toList()),
        osobe
            .stream()
            .filter(el -> el.getZarazenBolescu() instanceof Virus)
            .collect(Collectors.toList())
        );

        // Sa lambda izrazima

        Instant start1 = Instant.now();
        List<Virus> sortiraniVirusi1 = klinika
            .getUneseniVirusi()
            .stream()
//            .sorted(Comparator.comparing(Virus::getNaziv).reversed()) // ovo je kao "lijepa lambda" ali nije isti algoritam kao i u trećem zadatku jer radimo reverse i sort posebno
            .sorted((e1,e2)->e2.getNaziv().compareTo(e1.getNaziv())) // ima vise smisla jer radimo samo compare između drugog i prvog koji daje obrnut poredak
            .collect(Collectors.toList());
        Instant end1 = Instant.now();

        System.out.println("Virusi sortirani po nazivu suprotno od poretka abecede: ");

        sortiraniVirusi1
                .stream()
                .map(e -> e.getNaziv())
                .forEach(System.out::println);

        List<Virus> sortiraniVirusi2 = new ArrayList<>(klinika.getUneseniVirusi());

        // Lista bez lambda izraza ?

        Instant start2 = Instant.now();
        Collections.sort(sortiraniVirusi2, new Comparator<Virus>() {
            @Override
            public int compare(Virus v1, Virus v2) {
                return v2.getNaziv().compareTo(v1.getNaziv());
            }
        });
        Instant end2 = Instant.now();

        System.out.println("Sortiranje objekata korištenjem lambdi traje "
                + Duration.between(start1,end1)
                + " milisekundi, a bez lambdi traje "
                + Duration.between(start2,end2)
                + " milisekundi");

        System.out.print("Unesite string za pretragu po prezimenu: ");

        String nekoPrezime = sc.nextLine();

        System.out.println("Osobe čije prezime sadrži \"" + nekoPrezime + "\" su slijedeće: ");

        // Full hacky sa ovim ternarnim operatorom no nakon diskusije sa kolegama uistinu ne znam kako drugacije, jer lista i da je prazna nikako nece biti null vrijednost
        // tako da sam namjerno isforsirao null da se mogu posluziti sa ifPresentOrElse metodom od Optional klase jer ovo je tehnicki lambda no full cudna lol :D
        // Mogao sam i bez lambdi, no bojao sam se da ako ne koristim iskljucivo lambde (jer je tako navedeno u zadatku) nego if else sa usporedbom Optional tipa da ce
        // mi priprema biti odbacena

        Optional.ofNullable(
                osobe
                    .stream()
                    .filter(el->el.getPrezime().contains(nekoPrezime))
                    .collect(Collectors.toList()).isEmpty() ?
                        null :
                        osobe
                            .stream()
                            .filter(el->el.getPrezime().contains(nekoPrezime))
                            .collect(Collectors.toList())
        ).ifPresentOrElse(
                el -> el.stream().map(ele -> ele.getIme() + " " + ele.getPrezime()).forEach(System.out::println),
                () -> System.out.println("Lista je prazna")
        );

//        // Ovo bi bio drugi nacin rjesavanja ovakvog problema, no ne koristi lambde u potpunosti
//
//        Optional<List<Osoba>> nekaOsoba = Optional.of(osobe.stream().filter(el->el.getPrezime().contains(nekoPrezime)).collect(Collectors.toList()));
//
//        if (nekaOsoba.get().isEmpty()) {
//             System.out.println("Lista osoba je prazna");
//        } else {
//            nekaOsoba.get().stream().map(ele -> ele.getIme() + " " + ele.getPrezime()).forEach(System.out::println);
//        }

//        nekaOsoba.stream().map(el->el.getIme()).forEach(System.out::println);

        bolesti
            .stream()
            .map(el->el.getNaziv() + " ima " + el.getSimptomi().size() + " simptoma")
            .forEach(System.out::println);
    }


    /**
     * Ispisuje županiju sa najvećim postotkom zaraženih
     *
     * @param zupanije sortirane županije
     */

    private static void ispisZupanijeSaNajviseZarazenih(SortedSet<Zupanija> zupanije) {
        BigDecimal prviBrojZarazenih = new BigDecimal(zupanije.last().getBrojZarazenih());
        BigDecimal prviBrojStanovnika = new BigDecimal(zupanije.last().getBrojStanovnika());
        BigDecimal postotakBrojaZarazenih = (prviBrojZarazenih.divide(prviBrojStanovnika))
                .multiply(new BigDecimal(100));

        System.out.println("Najviše zaraženih osoba ime u županiji "
                + zupanije.last().getNaziv() + ": " + postotakBrojaZarazenih + "%.");

    }

    /**
     * Ispisuje Bolesti/Viruse i osobe koje ih imaju
     *
     * @param osobeZarazeneVirusima mapa osoba i virusa
     */

    private static void ispisVirusaIOsobaZarazenihVirusima(Map<Bolest, List<Osoba>> osobeZarazeneVirusima) {
        for (Bolest bolest : osobeZarazeneVirusima.keySet()) {

            System.out.print("Od " + ((bolest instanceof Virus) ? "virusa" : "bolesti") + " " + bolest.getNaziv());

            if (osobeZarazeneVirusima.get(bolest).size() > 1) {
                System.out.print(" boluju: ");
                for (Osoba osoba : osobeZarazeneVirusima.get(bolest)) {
                    System.out.print(osoba.getIme() + " " + osoba.getPrezime() + ", ");
                }
                System.out.print("\n");
            } else if (osobeZarazeneVirusima.get(bolest).size() == 1) {
                System.out.print(" boluje: ");
                System.out.println(osobeZarazeneVirusima.get(bolest).get(0).getIme() + " " + osobeZarazeneVirusima.get(bolest).get(0).getPrezime());
            }
        }
    }

    /**
     * Popunjava mapu osobeZarazeneVirusima sa osobama i virusima kojima su zarazene
     *
     * @param osobe                 unesene osobe
     * @param osobeZarazeneVirusima konacna mapa
     */

    private static void populacijaMapeOsobeZarazeneVirusima(List<Osoba> osobe, Map<Bolest, List<Osoba>> osobeZarazeneVirusima) {

        for (Osoba osoba : osobe) {

            List<Osoba> zarazeneOsobe;

            if (osobeZarazeneVirusima.containsKey(osoba.getZarazenBolescu())) {

                zarazeneOsobe = osobeZarazeneVirusima.get(osoba.getZarazenBolescu());

            } else {
                zarazeneOsobe = new ArrayList<>();

            }
            zarazeneOsobe.add(osoba);
            osobeZarazeneVirusima.put(osoba.getZarazenBolescu(), zarazeneOsobe);
        }
    }

    /**
     * Unosi županije u polje županija <code>Zupanija[] zupanije</code>
     * <p>
     * Unosi nazive županija <code>String nazivZupanije</code> i broj stanovnika <code>int brojStanovnika</code>
     * iz korisnickog unosa <code>Scanner input</code>.
     * <p>
     * Ao je uneseni broj stanovnika <code>int brojStanovnika</code> manji od 0 , u logger upisuje ispisuje gresku
     * <code>logger.error("Prilikom unosa broja stanovnika, unesen je negativan broj")</code>
     *
     * @param input    korisnicki unos
     * @param zupanije referenca na polje županija
     */

    private static void unosZupanija(Scanner input, SortedSet<Zupanija> zupanije) {
        String nazivZupanije;
        Long id = null;
        int brojStanovnika = 0, brojZupanija = 0, brojZarazenih = 0;
        boolean ispravanUnos = true;

        // Unos broja županija i validacija unosa

        do {

            try {

                System.out.printf("Unesite broj županija koje želite unijeti: ");

                brojZupanija = input.nextInt();

                input.nextLine();

                if (brojZupanija < 1) {

                    System.out.println("Pogrešan unos! Molimo unesite broj veci od 1.");

                    logger.error("Prilikom unosa broja županija unesen je negativan broj.");

                    ispravanUnos = false;

                } else {

                    logger.info("Unesen je broj županija: " + Integer.toString(brojZupanija));

                    ispravanUnos = true;

                }

            } catch (InputMismatchException ex) {

                logger.error("Prilikom unosa broja županija je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                input.nextLine();

                ispravanUnos = false;

            }

        } while (!ispravanUnos);


        // Unos županija

        System.out.printf("Unesite podatke o %d zupanije:%n", brojZupanija);
        for (int i = 0; i < brojZupanija; ++i) {

            // Unos naziva županija

            System.out.printf("Unesite naziv zupanije: ");

            nazivZupanije = input.nextLine();

            // Unos i validacija unosa broja stanovnika

            do {

                try {

                    System.out.printf("Unesite broj stanovnika: ");

                    brojStanovnika = input.nextInt();

                    input.nextLine();

                    if (brojStanovnika < 0) {

                        System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                        logger.error("Prilikom unosa broja stanovnika unesen je negativan broj.");

                        ispravanUnos = false;

                    } else {

                        logger.info("Unesen je broj stanovnika: " + Integer.toString(brojStanovnika));

                        ispravanUnos = true;

                    }


                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa broja stanovnika je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;
                }
            } while (!ispravanUnos);

            // Unos i validacija broja zaraženih osoba

            do {

                try {

                    System.out.printf("Unesite broj zaraženih stanovnika: ");

                    brojZarazenih = input.nextInt();

                    input.nextLine();

                    if (brojZarazenih < 0) {

                        System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                        logger.error("Prilikom unosa broja zaraženih stanovnika unesen je negativan broj.");

                        ispravanUnos = false;

                    } else {

                        logger.info("Unesen je broj zaraženih stanovnika: " + Integer.toString(brojZupanija));

                        ispravanUnos = true;

                    }

                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa broja zaraženih stanovnika je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;

                }

            } while (!ispravanUnos);

            zupanije.add(new Zupanija(id, nazivZupanije, brojStanovnika, brojZarazenih));

        }
    }

    /**
     * Unosi simptome u polje simptoma <code>Simptom[] simptomi</code>
     * <p>
     * Unosi nazive simptoma <code>String nazivSimptoma</code> i vrijednosti simptoma <code>String vrijednostSimptoma</code>
     * (RIJETKO, SREDNJE, CESTO) iz korisnickog unosa <code>Scanner input</code> i sprema ih u polje Simptoma <code>Simptom[] simptomi</code>
     * <p>
     * Ako je unesena vrijednost simptoma izvan dozvoljenog raspona (RIJETKO, SREDNJE, ČESTO), u log upisuje gresku
     * <code>logger.error("Prilikom unosa pojave vrijednosti simptoma je broj izvan raspona dopustenih vrijednosti.")</code>
     *
     * @param input    korisnički unos
     * @param simptomi referenca na polje simptoma
     */

    private static void unosSimptoma(Scanner input, Set<Simptom> simptomi) {
        Long id = null;
        String nazivSimptoma;
        String vrijednostSimptoma;
        int brojSimptoma = 0;
        boolean ispravanUnos = true;

        // Unos broja simptoma i validacija unosa

        do {

            try {

                System.out.printf("Unesite broj simptoma koje želite unijeti: ");

                brojSimptoma = input.nextInt();

                input.nextLine();

                if (brojSimptoma < 0) {

                    System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                    logger.error("Prilikom unosa broja simptoma unesen je negativan broj.");

                    ispravanUnos = false;

                } else {

                    logger.info("Unesen je broj simptoma: " + Integer.toString(brojSimptoma));

                    ispravanUnos = true;

                }

            } catch (InputMismatchException ex) {

                logger.error("Prilikom unosa broja simptoma je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                input.nextLine();

                ispravanUnos = false;

            }

        } while (!ispravanUnos);

        // Unos simptoma

        System.out.printf("Unesite podatke o %d simptoma:%n", brojSimptoma);

        for (int i = 0; i < brojSimptoma; ++i) {

            // Unos naziva simptoma

            System.out.printf("Unesite naziv simptoma: ");

            nazivSimptoma = input.nextLine();

            // Unos i validacija unosa vrijednosti simptoma

            do {

                System.out.printf("Unesite vrijednost simptoma(%s, %s, %s): ",
                        VrijednostSimptoma.RIJETKO.getVrijednost(),
                        VrijednostSimptoma.SREDNJE.getVrijednost(),
                        VrijednostSimptoma.CESTO.getVrijednost());
                vrijednostSimptoma = input.nextLine();

                // if(!vrijednostSimptoma.in([RIJETKO, SREDNJE, CESTO])) Provjera pojave vrijednosti simptoma analogno IN Operatoru u SQL

                if (!Arrays.asList(VrijednostSimptoma.RIJETKO.getVrijednost(),
                        VrijednostSimptoma.SREDNJE.getVrijednost(),
                        VrijednostSimptoma.CESTO.getVrijednost())
                        .contains(vrijednostSimptoma)) {

                    System.out.println("Pogrešan unos simptoma !");

                    logger.error("Prilikom unosa pojave vrijednosti simptoma je broj izvan raspona dopuštenih vrijednosti.");

                }

            } while (!Arrays.asList(VrijednostSimptoma.RIJETKO.getVrijednost(),
                    VrijednostSimptoma.SREDNJE.getVrijednost(),
                    VrijednostSimptoma.CESTO.getVrijednost())
                    .contains(vrijednostSimptoma));

            // Dodavanje Simptoma Ovisno o Vrijednosti

            simptomi.add(new Simptom(id, nazivSimptoma,
                    vrijednostSimptoma.equals(VrijednostSimptoma.RIJETKO.getVrijednost()) ?
                            VrijednostSimptoma.RIJETKO :
                            vrijednostSimptoma.equals(VrijednostSimptoma.SREDNJE.getVrijednost()) ?
                                    VrijednostSimptoma.SREDNJE :
                                    VrijednostSimptoma.CESTO
            ));

        }
    }

    /**
     * Unosi bolesti u polje bolesti <code>Bolest[] bolesti</code>
     * <p>
     * Unosi nazive bolesti/virusa <code>String nazivBolestiIliVirusa</code> i simptome <code>Simptom[] simptomi</code>
     * koje sprema u polje bolesti <code>Bolest[] bolesti</code>
     * Ako je unesena vrijednost bolest/virus <code>int bolestIliVirus</code> izvan dozvoljenog raspona (1. Bolest 2. Virus) U log upisuje gresku
     * <code>logger.error("Prilikom unosa Bolesti ili Virusa unesen je broj izvan raspona dopustenih brojeva.");</code>
     * <p>
     * Ako unesena vrijednost bolesti/virusa <code>int bolestIliVirus</code> nije cijeli broj <code>int</code> obrađuje gresku i upisuje u log
     * <code>logger.error("Prilikom unosa bolesti ili virusa je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * <p>
     * Ako je unesena vrijednost bolest/virus <code>int bolestIliVirus</code> izvan dozvoljenog raspona (1. Bolest 2. Virus) u log upisuje gresku
     * <code>System.out.println("Pogresan unos broja simptoma ! Unesen je broj izvan raspona ukupnog broja mogućih simptoma.");</code>
     * <p>
     * Ako unesena vrijednost broja simptoma <code>int brojOdabranihSimptoma</code> nije cijeli broj <code>int</code> obrađuje gresku i upisuje u log
     * <code>logger.error("Prilikom unosa broja simptoma je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * <p>
     * Ako je unesena vrijednost broja simptoma <code>int brojOdabranihSimptoma</code> izvan dozvoljenog raspona (1, <code>simptomi.length</code>) u log upisuje gresku
     * <code>System.out.println("Pogresan unos broja simptoma ! Unesen je broj izvan raspona ukupnog broja mogućih simptoma.");</code>
     * <p>
     * Ako su uneseni simptomi <code>Simptom[] kopiraniSimptomi</code> već prisutni u prethodno navedenim bolestima ili virusima
     * <code>Bolest[] bolesti</code> preko provjere u metodi <code>provjeraBolestiIstihSimptoma(bolesti, kopiraniSimptomi, i);</code> baca gresku koju
     * upisuje u log <code>logger.error(ex.getMessage(), ex);</code>
     *
     * @param input    korisnički unos
     * @param simptomi referenca na polje simptoma
     * @param bolesti  referenca na polje bolesti
     */

    private static void unosBolesti(Scanner input, Set<Simptom> simptomi, Set<Bolest> bolesti) {
        Long id = null;
        String nazivBolestiIliVirusa;
        int brojOdabranihSimptoma = 0, odabraniSimptom;
        Set<Simptom> odabraniSimptomi;
        int bolestIliVirus = 0, brojBolesti = 0, brojVirusa = 0;
        boolean ispravanUnos;

        // Unos broja bolesti i validacija unosa

        do {

            try {

                System.out.printf("Unesite broj bolesti koje želite unijeti: ");

                brojBolesti = input.nextInt();

                input.nextLine();

                if (brojBolesti < 0) {

                    System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                    logger.error("Prilikom unosa broja bolesti unesen je negativan broj.");

                    ispravanUnos = false;

                } else {

                    logger.info("Unesen je broj bolesti: " + Integer.toString(brojBolesti));

                    ispravanUnos = true;

                }

            } catch (InputMismatchException ex) {

                logger.error("Prilikom unosa broja bolesti je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                input.nextLine();

                ispravanUnos = false;

            }

        } while (!ispravanUnos);

        // Unos broja virusa i validacija unosa

        do {

            try {

                System.out.printf("Unesite broj virusa koje želite unijeti: ");

                brojVirusa = input.nextInt();

                input.nextLine();

                if (brojVirusa < 0) {

                    System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                    logger.error("Prilikom unosa broja virusa unesen je negativan broj.");

                    ispravanUnos = false;

                } else {

                    logger.info("Unesen je broj virusa: " + Integer.toString(brojVirusa));

                    ispravanUnos = true;

                }

            } catch (InputMismatchException ex) {

                logger.error("Prilikom unosa broja virusa je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                input.nextLine();

                ispravanUnos = false;

            }

        } while (!ispravanUnos);


        System.out.printf("Unesite podatke o %d bolesti ili virusa:%n", brojBolesti + brojVirusa);

        for (int i = 0; i < brojBolesti + brojVirusa; ++i) {

            // Odabir unosa bolesti ili virusa i validacija unosa

            do {

                try {

                    System.out.printf("Unosite li bolest ili virus ?%n1)BOLEST%n2)VIRUS%n");

                    bolestIliVirus = input.nextInt();

                    input.nextLine();

                    if (bolestIliVirus != 1 && bolestIliVirus != 2) {

                        System.out.println("Pogresan unos! Molimo unesite jedan od ponuđenih brojeva.");

                        logger.error("Prilikom unosa Bolesti ili Virusa unesen je broj izvan raspona dopuštenih brojeva.");

                        ispravanUnos = false;

                    } else {

                        logger.info((bolestIliVirus == 1 ? "Unesena je  Bolest: " : "Unesen je Virus: ")
                                + Integer.toString(bolestIliVirus));

                        ispravanUnos = true;

                    }

                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa bolesti ili virusa je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;
                }

            } while (!ispravanUnos);

            do {

                // Ponovna inicijalizacija seta odabranih simptoma

                odabraniSimptomi = new HashSet<>();

                // Unos Bolesti ili Virusa

                System.out.printf("Unesite naziv bolesti ili virusa: ");

                nazivBolestiIliVirusa = input.nextLine();

                // Unos Broja Odabranih Simptoma i validacija unosa

                do {

                    try {
                        System.out.printf("Unesite broj simptoma: ");

                        brojOdabranihSimptoma = input.nextInt();

                        input.nextLine();

                        if (brojOdabranihSimptoma > simptomi.size() || brojOdabranihSimptoma < 1) {

                            System.out.println("Pogresan unos broja simptoma ! Unesen je broj izvan raspona ukupnog broja mogućih simptoma.");

                            logger.error("Prilikom unosa broja simptoma unesen je broj izvan raspona ukupnog broja mogućih simptoma.");

                            ispravanUnos = false;

                        } else {

                            logger.info("Uneseni broj simptoma: " + Integer.toString(brojOdabranihSimptoma));

                            ispravanUnos = true;

                        }

                    } catch (InputMismatchException ex) {

                        logger.error("Prilikom unosa broja simptoma je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                        System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                        input.nextLine();

                        ispravanUnos = false;
                    }

                } while (!ispravanUnos);

                // Unos odabranih simptoma i validacija

                for (int j = 0; j < brojOdabranihSimptoma; ++j) {

                    // Biranje Postojeceg Simptoma i validacija unosa

                    do {

                        System.out.printf("Odaberite %d. simptom:%n", j + 1);

                        // Ispis Postojecih Simptoma

                        Iterator<Simptom> iteratorSimptoma = simptomi.iterator();
                        Simptom simptom;

                        for (int k = 0; k < simptomi.size() && iteratorSimptoma.hasNext(); ++k) {
                            simptom = iteratorSimptoma.next();
                            System.out.printf("%d. %s %s%n", k + 1, simptom.getNaziv(), simptom.getVrijednost().getVrijednost());
                        }

                        try {

                            System.out.print("Odabir: ");

                            odabraniSimptom = input.nextInt();

                            input.nextLine();

                            if (odabraniSimptom > simptomi.size() || odabraniSimptom < 1) {

                                System.out.println("Neispravan unos, molimo pokusajte ponovno!");

                                logger.error("Prilikom biranja simptoma unesen je broj izvan raspona ukupnog broja postojećih simptoma.");

                                ispravanUnos = false;

                            } else {

                                // Iteracija simptoma po indeksu

                                ispravanUnos = true;

                                iteratorSimptoma = simptomi.iterator();
                                Simptom pronadeniOdabraniSimptom = null;

                                for (int k = 0; k < simptomi.size() && iteratorSimptoma.hasNext(); ++k) {
                                    simptom = iteratorSimptoma.next();
                                    if (k == (odabraniSimptom - 1)) {
                                        pronadeniOdabraniSimptom = simptom;
                                    }
                                }

                                logger.info("Odabran je (broj) simptom is postojećih simptoma: " + Integer.toString(odabraniSimptom));

                                odabraniSimptomi.add(pronadeniOdabraniSimptom);

                            }
                        } catch (InputMismatchException ex) {

                            logger.error("Prilikom unosa brojčane vrijednosti kod biranja postojećih simptoma je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                            System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                            input.nextLine();

                            ispravanUnos = false;

                        }

                    } while (!ispravanUnos);
                }

                // Provjera duplikata unosa Simptoma

                if (bolesti.size() > 0) {

                    try {

                        provjeraBolestiIstihSimptoma(bolesti, odabraniSimptomi);

                        ispravanUnos = true;

                    } catch (BolestIstihSimptoma ex) {

                        logger.error(ex.getMessage(), ex);

                        ispravanUnos = false;

                    }

                }

            } while (!ispravanUnos);

            // Provjera da li je unos bolest ili virus i unos u polje bolesti

            bolesti.add(bolestIliVirus == 1 ? new Bolest(id, nazivBolestiIliVirusa, odabraniSimptomi) : new Virus(id, nazivBolestiIliVirusa, odabraniSimptomi));
        }
    }

    /**
     * Provjerava postojanost unesenih simptoma <code>Simptom[] kopiraniSimptomi</code> u polju simptoma <code>bolesti[i].getSimptomi()</code> prethodno unesenih bolesti
     * <code>Bolest[] bolesti</code>
     * <p>
     * Ako su trenutno uneseni simptomi <code>Simptom[] kopiraniSimptomi</code> prisutni u simptomima polja prethodno unesenih bolesti <code>Bolest[] bolesti</code>
     * baca grešku <code>throw new BolestIstihSimptoma("Uneseni simptomi su duplikati iz prethodno unesenih bolesti!");</code>
     *
     * @param bolesti          referenca na polje bolesti koje su trenutno unesene
     * @param odabraniSimptomi referenca na polje simptoma za bolest koja se trenutno unosi
     * @throws BolestIstihSimptoma iznimka koja se baca u slučaju kad su trenutno uneseni simptomi <code>Simptom[] kopiraniSimptomi</code>
     *                             prisutni u prethodno unesenim bolestima <code>Bolest[] bolesti</code>
     */

    private static void provjeraBolestiIstihSimptoma(Set<Bolest> bolesti, Set<Simptom> odabraniSimptomi) throws BolestIstihSimptoma {

        for (Bolest bolest : bolesti) {

            if (odabraniSimptomi.size() == bolest.getSimptomi().size()) {

                if (bolest.getSimptomi().containsAll(odabraniSimptomi)) {

                    System.out.println("Unesena bolest ne smije imati simptome jednake prethodno unesenim bolestima!");

                    System.out.println("Molimo Vas da ponovno unesete bolest.");

                    throw new BolestIstihSimptoma("Uneseni simptomi su duplikati iz prethodno unesenih bolesti!");

                }
            }
        }
    }

    /**
     * Ispisuje osobe <code>Osoba[] osobe</code> koje su unesene u program
     *
     * @param osobe osobe koje su unesene u program
     */

    private static void ispisOsoba(List<Osoba> osobe) {
        System.out.println("Popis osoba:");

        for (Osoba osoba : osobe) {
            System.out.print(osoba.toString());
        }
    }

    /**
     * Unosi osobe u polje osoba <code>Osoba[] osobe</code>
     * <p>
     * Unosi ime osobe <code>String ime</code> i prezime osobe <code>String prezime</code> i unosi starost osobe <code>Integer starost</code>
     * <p>
     * Ako je starost osobe <code>Integer starost</code> manja od 0 u log upisuje gresku
     * <code>logger.error("Prilikom unosa starosti osobe, unesen je negativan broj: " + Integer.toString(starost));</code>
     * <p>
     * Ako u starost osobe <code>Integer starost</code> nije unesena brojčana vrijednost obrađuje iznimku <code>InputMismatchException ex</code>
     * i upisuje gresku u log <code>logger.error("Prilikom unosa brojčane vrijednosti kod starosti osobe je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * <p>
     * Unosi županiju osobe <code>Zupanija zupanija</code> i ako je odabrana županija <code>int odabranaZupanija</code> izvan raspona dostupnih županija <code>Zupanija[] zupanije</code>
     * u log upisuje gresku <code>logger.error("Prilikom unosa županije osobe, unesen je broj izvan prethodno navedenog raspona: " + Integer.toString(odabranaZupanija));</code>
     * <p>
     * Ako odabrana županija <code>int odabranaZupanija</code> nije cijeli broj <code>int</code> obrađuje iznimku <code>InputMismatchException ex</code>
     * i upisuje gresku u log <code>logger.error("Prilikom unosa brojčane vrijednosti kod biranja županije osobe je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * <p>
     * Unosi odabir bolest ili virus osobe <code>int odabranaBolest</code> i ako je unesena vrijednost izvan raspona dostupnih bolesti
     * u log upisuje gresku <code>logger.error("Prilikom unosa bolesti/virusa osobe, unesen je broj izvan prethodno navedenog raspona: " + Integer.toString(odabranaBolest));</code>
     * <p>
     * Ako odabrana bolest ili virus <code>int odabranaBolest</code> nije cijeli broj <code>int</code> obrađuje iznimku <code>InputMismatchException ex</code>
     * i upisuje gresku u log <code>logger.error("Prilikom unosa brojčane vrijednosti kod biranja bolesti/virusa osobe je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * <p>
     * Ako je broj trenutno unesenih osoba veći ili jednak 1 <code>if(i > 0)</code> unosi broj kontaktiranih osoba <code>int brojKontaktiranihOsoba</code> i sprema ih u polje
     * <code>int[] odabraneUneseneKontaktiraneOsobe</code>
     * <p>
     * Unosi odabir kontaktiranih osoba i upisuje trenutno kontaktiranu osobu u <code>int odabranaKontaktiranaOsoba</code> i ako je unesena vrijednost izvan raspona dostupnih prethodno unesenih
     * osoba <code>if (brojKontaktiranihOsoba > i || brojKontaktiranihOsoba < 0)</code> u log upisuje gresku
     * <code> logger.error("Prilikom unosa broja kontaktiranih osoba, unesen je broj izvan raspona unesenog broja osoba: " + Integer.toString(brojKontaktiranihOsoba));</code>
     * <p>
     * Ako uneseni broj kontaktirane osobe <code>int odabranaKontaktiranaOsoba</code> nije cijeli broj <code>int</code> obrađuje iznimku <code>InputMismatchException ex</code>
     * i upisuje gresku u log <code>logger.error("Prilikom unosa brojčane vrijednosti kod unosa odabrane kontaktirane osobe je doslo do pogreske. Unesen je String koji se ne može parsirati!", ex);</code>
     * Provjerava unos duplikata <code>provjeraDuplikataKontaktiranihOsoba(odabranaKontaktiranaOsoba, odabraneUneseneKontaktiraneOsobe);</code> i obrađuje iznimku <code>DuplikatKontaktiraneOsobe ex</code>
     * i upisuje gresku u log <code> logger.error(ex.getMessage(), ex);</code>
     *
     * @param input    korisnički unos
     * @param zupanije referenca na polje unesenih županija
     * @param bolesti  referenca na polje unesenih bolesti
     * @param osobe    referenca na polje unesenih osoba
     */

    private static void unosOsoba(Scanner input, SortedSet<Zupanija> zupanije, Set<Bolest> bolesti, List<Osoba> osobe) {
        boolean ispravanUnos = true;
        int odabranaZupanija = 0, brojOsoba = 0;
        int odabranaBolest = 0;
        int odabranaKontaktiranaOsoba = 0;
        List<Osoba> odabraneUneseneKontaktiraneOsobe = new ArrayList<>();
        Osoba odabranaUnesenaKontaktiranaOsoba = null;
        int brojKontaktiranihOsoba = 0;
        String ime, prezime;
        Integer starost = 0;
        Zupanija zupanija = null;
        Bolest zarazenBolescu = null, odabranaUnesenaBolest = null;
        List<Osoba> kontaktiraneOsobe = new ArrayList<>();

        // Unos broja osoba i validacija unosa

        do {

            try {

                System.out.printf("Unesite broj osoba koje želite unijeti: ");

                brojOsoba = input.nextInt();

                input.nextLine();

                if (brojOsoba < 0) {

                    System.out.println("Pogrešan unos! Molimo unesite pozitivan cijeli broj.");

                    logger.error("Prilikom unosa broja osoba unesen je negativan broj.");

                    ispravanUnos = false;

                } else {

                    logger.info("Unesen je broj osoba: " + Integer.toString(brojOsoba));

                    ispravanUnos = true;

                }

            } catch (InputMismatchException ex) {

                logger.error("Prilikom unosa broja osoba je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                input.nextLine();

                ispravanUnos = false;

            }

        } while (!ispravanUnos);

        for (int i = 0; i < brojOsoba; ++i) {

            // Unos imena

            System.out.printf("Unesite ime %d. osobe: ", i + 1);
            ime = input.nextLine();

            // Unos prezimena

            System.out.printf("Unesite prezime %d. osobe: ", i + 1);
            prezime = input.nextLine();

            // Unos starosti i validacija unosa

            do {

                try {

                    System.out.printf("Unesite starost osobe: ");

                    starost = input.nextInt();

                    input.nextLine();

                    if (starost < 0) {
                        System.out.println("Unesena vrijednost ne smije biti negativan broj! Molimo ponovite unos.");

                        logger.error("Prilikom unosa starosti osobe, unesen je negativan broj: " + Integer.toString(starost));

                        ispravanUnos = false;

                    } else {

                        logger.info("Unesena je starost osobe: " + Integer.toString(starost));

                        ispravanUnos = true;

                    }

                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa brojčane vrijednosti kod starosti osobe je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;
                }

            } while (!ispravanUnos);


            // Unos zupanije prebivalista i validacija

            do {

                try {

                    System.out.printf("Unesite županiju prebivališta osobe:%n");

                    // Ispis zupanija

                    Iterator<Zupanija> iteratorZupanija = zupanije.iterator();

                    for (int j = 0; j < zupanije.size(); ++j) {
                        System.out.printf("%d. %s%n", j + 1, iteratorZupanija.next().getNaziv());
                    }

                    System.out.print("Odabir: ");

                    odabranaZupanija = input.nextInt();

                    input.nextLine();

                    // Provjera ispravnosti unosa Odabrane Zupanije

                    if (odabranaZupanija < 1 || odabranaZupanija > zupanije.size()) {

                        System.out.println("Pogresan unos županije!");

                        logger.error("Prilikom unosa županije osobe, unesen je broj izvan prethodno navedenog raspona: "
                                + Integer.toString(odabranaZupanija));

                        ispravanUnos = false;

                    } else {

                        logger.info("Unesen je odabir županije: " + Integer.toString(odabranaZupanija));

                        ispravanUnos = true;

                    }

                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa brojčane vrijednosti kod biranja županije osobe je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;
                }


            } while (!ispravanUnos);

            // odabir zupanije iz seta zupanija po indeksu

            Iterator<Zupanija> iteratorZupanija = zupanije.iterator();

            for (int j = 0; j < zupanije.size() && iteratorZupanija.hasNext(); ++j) {
                zupanija = iteratorZupanija.next();
                if (j == (odabranaZupanija - 1)) {
                    break;
                }
            }

            // Unos bolesti osobe

            do {

                try {

                    System.out.println("Unesite bolest ili virus osobe:");

                    // ispis bolesti

                    Iterator<Bolest> iteratorBolesti = bolesti.iterator();

                    for (int j = 0; j < bolesti.size() && iteratorBolesti.hasNext(); ++j) {
                        System.out.printf("%d. %s%n", j + 1, iteratorBolesti.next().getNaziv());
                    }

                    System.out.print("Odabir: ");

                    odabranaBolest = input.nextInt();

                    input.nextLine();

                    // Provjera ispravnosti unosa Odabrane Bolesti Osobe

                    if (odabranaBolest < 1 || odabranaBolest > bolesti.size()) {

                        System.out.println("Pogrešan unos bolesti/virusa!");

                        logger.error("Prilikom unosa bolesti/virusa osobe, unesen je broj izvan prethodno navedenog raspona: "
                                + Integer.toString(odabranaBolest));

                        ispravanUnos = false;

                    } else {

                        // logiranje bolesti u ovisnosti o odabranoj bolesti

                        iteratorBolesti = bolesti.iterator();

                        for (int l = 0; l < bolesti.size() && iteratorBolesti.hasNext(); ++l) {
                            if (l == (odabranaBolest - 1)) {
                                logger.info(((iteratorBolesti.next() instanceof Virus) ? "Unesen je virus: " : "Unesena je bolest: ")
                                        + Integer.toString(odabranaBolest));
                            } else {
                                iteratorBolesti.next();
                            }
                        }

                        ispravanUnos = true;

                    }

                } catch (InputMismatchException ex) {

                    logger.error("Prilikom unosa brojčane vrijednosti kod biranja bolesti/virusa osobe je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                    System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                    input.nextLine();

                    ispravanUnos = false;

                }

            } while (!ispravanUnos);

            // spremanje odabrane bolesti po indeksu

            Iterator<Bolest> iteratorBolesti = bolesti.iterator();

            for (int j = 0; j < bolesti.size() && iteratorBolesti.hasNext(); ++j) {
                odabranaUnesenaBolest = iteratorBolesti.next();
                if (j == (odabranaBolest - 1)) {
                    zarazenBolescu = odabranaUnesenaBolest;
                    break;
                }
            }


            // Provjera osoba s kojim je osoba usla u kontakt u slucaju da nije prva osoba - prva se ne gleda

            if (osobe.size() > 0) {

                // Unos broja kontaktiranih osoba i validacija

                do {

                    try {

                        System.out.println("Unesite broj osoba koje su bile u kontaktu s tom osobom:");

                        brojKontaktiranihOsoba = input.nextInt();

                        input.nextLine();

                        // Provjera unosa broja kontaktiranih osoba

                        if (brojKontaktiranihOsoba > i || brojKontaktiranihOsoba < 0) {

                            System.out.println("Greska u unosu broja kontaktiranih osoba. Broj trenutno unesenih osoba je: " + Integer.toString(i));

                            logger.error("Prilikom unosa broja kontaktiranih osoba, unesen je broj izvan raspona unesenog broja osoba: "
                                    + Integer.toString(brojKontaktiranihOsoba));

                            ispravanUnos = false;

                        } else {

                            ispravanUnos = true;

                            logger.info("Unesen je broj kontaktiranih osoba: " + Integer.toString(brojKontaktiranihOsoba));

                        }

                    } catch (InputMismatchException ex) {

                        logger.error("Prilikom unosa brojčane vrijednosti kod biranja broja kontaktiranih osoba je došlo do pogreške. Unesen je String koji se ne može parsirati!", ex);

                        System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                        input.nextLine();

                        ispravanUnos = false;

                    }

                } while (!ispravanUnos);

                if (brojKontaktiranihOsoba > 0) {

                    // Unos i validacija Odabranih Kontaktiranih Osoba

                    odabraneUneseneKontaktiraneOsobe = new ArrayList<>();

                    for (int j = 0; j < brojKontaktiranihOsoba; ++j) {

                        do {

                            // Unos Odabrane Kontaktirane Osobe

                            try {

                                System.out.printf("Odaberite %d. osobu: %n", j + 1);

                                Iterator<Osoba> iteratorOsoba = osobe.iterator();
                                Osoba osoba;

                                for (int k = 0; k < i && iteratorOsoba.hasNext(); ++k) {
                                    osoba = iteratorOsoba.next();
                                    System.out.printf("%d. %s %s%n", k + 1, osoba.getIme(), osoba.getPrezime());
                                }

                                System.out.print("Odabir: ");

                                odabranaKontaktiranaOsoba = input.nextInt();

                                input.nextLine();

                                // Provjera unosa Odabrane Kontaktirane Osobe

                                if (odabranaKontaktiranaOsoba < 1 || odabranaKontaktiranaOsoba > i) {

                                    System.out.println("Greška pri unosu odabrane kontaktirane osobe");

                                    logger.error("Prilikom unosa odabira kontaktirane osobe, unesen je broj izvan raspona unesenog broja osoba: "
                                            + Integer.toString(odabranaKontaktiranaOsoba));

                                    ispravanUnos = false;

                                } else {

                                    // Provjera Duplikata Kontaktiranih Osoba i obrada greške

                                    odabranaUnesenaKontaktiranaOsoba = osobe.get(odabranaKontaktiranaOsoba - 1);

                                    provjeraDuplikataKontaktiranihOsoba(odabranaUnesenaKontaktiranaOsoba, odabraneUneseneKontaktiraneOsobe);

                                    ispravanUnos = true;

                                    logger.info("Unesen je odabir kontaktirane osobe: " + Integer.toString(odabranaKontaktiranaOsoba));

                                    odabraneUneseneKontaktiraneOsobe.add(odabranaUnesenaKontaktiranaOsoba);


                                }

                            } catch (InputMismatchException e) {

                                logger.error("Prilikom unosa brojčane vrijednosti kod unosa odabrane kontaktirane osobe je došlo do pogreške. Unesen je String koji se ne može parsirati!", e);

                                System.out.println("Došlo je do pogreške kod unosa brojčane vrijednosti! Molimo ponovite unos.");

                                input.nextLine();

                                ispravanUnos = false;

                            } catch (DuplikatKontaktiraneOsobe ex) {

                                logger.error(ex.getMessage(), ex);

                                ispravanUnos = false;

                            }

                        } while (!ispravanUnos);

                    }

                    // Spremanje Odabranih Kontaktiranih Osoba u polje Kontaktiranih Osoba

                    kontaktiraneOsobe = odabraneUneseneKontaktiraneOsobe;

                }
            }

            // Spremanje osoba u polje osoba

            if (i == 0) {
                osobe.add(new Osoba.Builder(ime).prezime(prezime).starost(starost).zupanija(zupanija)
                        .zarazenBolescu(zarazenBolescu).build());
            } else {
                osobe.add(new Osoba.Builder(ime).prezime(prezime).starost(starost).zupanija(zupanija)
                        .zarazenBolescu(zarazenBolescu).kontaktiraneOsobe(kontaktiraneOsobe).build());
            }
        }
    }

    /**
     * Provjerava postojanost odabrane kontaktirane osobe <code>int odabranaKontaktiranaOsoba</code> u polju
     * <code>int[] odabraneUneseneKontaktiraneOsobe</code> i provjerava duplikate
     * ako postoji duplikat baca iznimku <code>throw new DuplikatKontaktiraneOsobe("Prilikom unosa odabira kontaktirane osobe, unesena je prethodno odabrana osoba (duplikat): "
     * + Integer.toString(odabranaKontaktiranaOsoba));</code>
     *
     * @param odabranaUnesenaKontaktiranaOsoba unesena odabrana kontaktirana osoba
     * @param odabraneUneseneKontaktiraneOsobe polje prethodno odabranih kontaktiranih osoba
     * @throws DuplikatKontaktiraneOsobe iznimka koja se baca u slučaju kada su uneseni duplikati
     */

    private static void provjeraDuplikataKontaktiranihOsoba(Osoba odabranaUnesenaKontaktiranaOsoba, List<Osoba> odabraneUneseneKontaktiraneOsobe) throws DuplikatKontaktiraneOsobe {

        // (Provjera duplikata) Provjera postojanosti Odabrane Kontaktirane Osobe u prethodno Odabranim Kontaktiranim Osobama

        if (odabraneUneseneKontaktiraneOsobe.contains(odabranaUnesenaKontaktiranaOsoba)) {

            System.out.println("Osoba je već odabrana, molimo ponovno unesite!");

            throw new DuplikatKontaktiraneOsobe("Prilikom unosa odabira kontaktirane osobe, unesena je prethodno odabrana osoba (duplikat)");

        }
    }
}




























