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

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        File serijaliziraniVirusi = new File("dat/serijaliziraniVirus.dat");

        // 1. zadatak - zadatak na vjezbi

        if(serijaliziraniVirusi.exists()) {
            System.out.println("Postoji");
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("dat/serijaliziraniVirus.dat")
            )) {
                while(true) {
                    Virus mojVirus = (Virus)in.readObject();
                    System.out.println(mojVirus.getId());
                    System.out.println(mojVirus.getNaziv());
                }
            } catch(IOException ex) {
                logger.error("Greska prilikom serijalizacije.", ex);
            } catch (ClassNotFoundException exe) {
                logger.error("Greska prilikom serijalizacije, klasa nije pronadjena.", exe);
            }
        }

        // 2. zadatak - zadatak na vjezbi

        File mojZapisani = new File("dat/mojZapis.txt");

        try (
                FileReader fileReader = new FileReader(mojZapisani);
                BufferedReader reader = new BufferedReader(fileReader);
        ) {

            Integer suma = Integer.parseInt(reader.readLine());
            Integer najdulji = Integer.parseInt(reader.readLine());
            Integer najkraci = Integer.parseInt(reader.readLine());
            Double prosjek = Double.parseDouble(reader.readLine());

            System.out.println(suma);
            System.out.println(najdulji);
            System.out.println(najkraci);
            System.out.println(prosjek);

        } catch (IOException ex) {

            logger.error("Ne mogu otvoriti datoteku.", ex);

        } catch (NumberFormatException exe) {
            logger.error("Ne mogu pretvoriti broj.", exe);
        }



        // Unos Zupanija

        unosZupanija(input, zupanije);

        // zupanije.stream().map(el-> el.getNaziv()).forEach(System.out::println);

        // Unos Simptoma

        unosSimptoma(input, simptomi);

        // Unos Bolesti

        unosBolesti(input, simptomi, bolesti);

//        bolesti.stream().filter(e -> e.getNaziv().compareTo("COVID-19") == 0)
//                .flatMap(el -> Stream.of(el.getSimptomi()))
//                .collect(Collectors.toList()).get(0)
//                .stream().map(e->e.getNaziv()).forEach(System.out::println);

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

        // Izvedba serijalizacije seste laboratorijske vjezbe

        serijalizacijaSestaVjezba(zupanije);

        // 1. zadatak - zadatak na vjezbi

        bolesti.stream().filter(e-> e instanceof Virus)
                .filter(e ->
                    (e.getNaziv().toLowerCase().charAt(0) == 'e') &&
                    (e.getNaziv().toLowerCase().contains("bola"))
                )
                .forEach(e->{
                    try (ObjectOutputStream serializator = new ObjectOutputStream(
                            new FileOutputStream("dat/serijaliziraniVirus.dat")
                    )) {
                        System.out.println(e.getId());
                        System.out.println(e.getNaziv());
                        serializator.writeObject(e);
                    } catch(IOException ex) {
                        logger.error("Greska prilikom serijalizacije.", ex);
                    }
                });

        // 2. zadatak - zadatak na vjezbi

        // Unos id županija

        File mojZapis = new File("dat/mojZapis.txt");

        try (
                FileWriter fileWriter = new FileWriter(mojZapis);
                BufferedWriter writer = new BufferedWriter(fileWriter);
        ) {

            Integer suma = bolesti.stream()
                    .filter(e -> e instanceof Virus)
                    .map(e -> e.getNaziv().length())
//                    .flatMapToInt(IntStream::of)
                    .mapToInt(e->e)
                    .sum();

//            System.out.println(suma);

            Integer najdulji = bolesti.stream()
                    .filter(e -> e instanceof Virus)
                    .map(e -> e.getNaziv().length())
//                    .flatMapToInt(IntStream::of)
                    .mapToInt(e->e)
                    .max()
                    .getAsInt();

//            System.out.println(najdulji);

            Integer najkraci = bolesti.stream()
                    .filter(e -> e instanceof Virus)
                    .map(e -> e.getNaziv().length())
//                    .flatMapToInt(IntStream::of)
                    .mapToInt(e->e)
                    .min()
                    .getAsInt();

//            System.out.println(najkraci);

            Double prosjek = bolesti.stream()
                    .filter(e -> e instanceof Virus)
                    .map(e -> e.getNaziv().length())
//                    .flatMapToInt(IntStream::of)
                    .mapToInt(e->e)
                    .average()
                    .getAsDouble();

//            System.out.println(prosjek);

            writer.write(String.valueOf(suma)+"\n");
            writer.write(String.valueOf(najdulji)+"\n");
            writer.write(String.valueOf(najkraci)+"\n");
            writer.write(String.valueOf(prosjek)+"\n");



        } catch (IOException ex) {

            logger.error("Ne mogu kreirati datoteku.", ex);

        }

    }

    private static void serijalizacijaSestaVjezba(SortedSet<Zupanija> zupanije) {
        zupanije.stream().forEach(e -> {
            try (ObjectOutputStream serializator = new ObjectOutputStream(
                    new FileOutputStream("dat/serijalizirani.dat")
            )) {

                BigDecimal brojZarazenih = new BigDecimal(e.getBrojZarazenih());
                BigDecimal brojStanovnika = new BigDecimal(e.getBrojStanovnika());
                BigDecimal postotakBrojaZarazenih = (brojZarazenih.divide(brojStanovnika))
                        .multiply(new BigDecimal(100));

                if(postotakBrojaZarazenih.compareTo(new BigDecimal(2)) > 0) {

                    serializator.writeObject(e);

                }

            } catch (IOException ex) {
                logger.error("Greska prilikom serijalizacije.", ex);
            }
        });
    }

    // Metoda izvedbe pete laboratorijske vjezbe

    /**
     * Izvodi sve zadatke navedene u petoj laboratorijskoj vjezbi
     *
     * @param bolesti unesene bolesti
     * @param osobe   uneseni ljudi oboljeli od bolesti
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
                .sorted((e1, e2) -> e2.getNaziv().compareTo(e1.getNaziv())) // ima vise smisla jer radimo samo compare između drugog i prvog koji daje obrnut poredak
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
                + Duration.between(start1, end1)
                + " milisekundi, a bez lambdi traje "
                + Duration.between(start2, end2)
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
                        .filter(el -> el.getPrezime().contains(nekoPrezime))
                        .collect(Collectors.toList()).isEmpty() ?
                        null :
                        osobe
                                .stream()
                                .filter(el -> el.getPrezime().contains(nekoPrezime))
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
                .map(el -> el.getNaziv() + " ima " + el.getSimptomi().size() + " simptoma")
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
        File unosZupanija = new File("dat/zupanije.txt");
        String procitanaLinija;
        Long idZupanije;
        String nazivZupanije;
        int brojStanovnika = 0, brojZarazenih = 0;

        System.out.println("Učitavanje podataka o županijama...");

        // Unos id županija

        try (
                FileReader filereader = new FileReader(unosZupanija);
                BufferedReader reader = new BufferedReader(filereader);
        ) {

            while ((procitanaLinija = reader.readLine()) != null) {

                idZupanije = Long.parseLong(procitanaLinija);

                logger.info("Unesen je id županija: " + idZupanije);

                nazivZupanije = reader.readLine();

                logger.info("Unesen je naziv zupanije: " + nazivZupanije);

                brojStanovnika = Integer.parseInt(reader.readLine());

                logger.info("Unesen je broj stanovnika: " + brojStanovnika);

                brojZarazenih = Integer.parseInt(reader.readLine());

                logger.info("Unesen je broj zaraženih stanovnika: " + brojZarazenih);

                zupanije.add(new Zupanija(idZupanije, nazivZupanije, brojStanovnika, brojZarazenih));

            }

        } catch (IOException ex) {

            logger.error("Ne mogu pronaci datoteku.", ex);

        } catch (NumberFormatException  exe) {

            logger.error("Greska prilikom citanja broja!", exe);

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
        File unosSimptoma = new File("dat/simptomi.txt");
        String procitanaLinija;
        Long idSimptoma;
        String nazivSimptoma;
        String vrijednostSimptoma;

        System.out.println("Učitavanje podataka o simptomima...");

        // Unos broja simptoma i validacija unosa

        try (
                FileReader filereader = new FileReader(unosSimptoma);
                BufferedReader reader = new BufferedReader(filereader);
        ) {

            while ((procitanaLinija = reader.readLine()) != null) {

                idSimptoma = Long.parseLong(procitanaLinija);

                logger.info("Unesen je id simptoma: " + idSimptoma);

                nazivSimptoma = reader.readLine();

                logger.info("Unesen je naziv simptoma: " + nazivSimptoma);

                vrijednostSimptoma = reader.readLine();

                logger.info("Unesena je vrijednost simptoma: " + vrijednostSimptoma);

                // Dodavanje Simptoma Ovisno o Vrijednosti

                simptomi.add(new Simptom(idSimptoma, nazivSimptoma,
                        vrijednostSimptoma.equals(VrijednostSimptoma.RIJETKO.getVrijednost()) ?
                                VrijednostSimptoma.RIJETKO :
                                vrijednostSimptoma.equals(VrijednostSimptoma.SREDNJE.getVrijednost()) ?
                                        VrijednostSimptoma.SREDNJE :
                                        VrijednostSimptoma.CESTO
                ));

            }

        } catch (IOException ex) {

            logger.error("Ne mogu pronaci datoteku.", ex);

        } catch (NumberFormatException  exe) {

            logger.error("Greska prilikom citanja broja!", exe);

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
        File unosBolesti = new File("dat/bolesti.txt");
        File unosVirusa = new File("dat/virusi.txt");
        String procitanaLinija;
        Long idBolesti, idVirusa;
        String nazivBolesti, nazivVirusa;

        System.out.println("Učitavanje podataka o bolestima...");

        try (
                FileReader filereader = new FileReader(unosBolesti);
                BufferedReader reader = new BufferedReader(filereader);
        ) {

            while ((procitanaLinija = reader.readLine()) != null) {

                Set<Simptom> odabraniSimptomi = new HashSet<>();

                idBolesti = Long.parseLong(procitanaLinija);

                logger.info("Unesen je id simptoma: " + idBolesti);

                nazivBolesti = reader.readLine();

                logger.info("Unesen je naziv bolesti: " + nazivBolesti);

                Arrays.stream(reader.readLine().split(",")).forEach(el -> {

                    // Iteracija simptoma po indeksu

                    int element = Integer.parseInt(el);

                    Simptom simptom;

                    Iterator<Simptom> iteratorSimptoma = simptomi.iterator();
                    Simptom pronadeniOdabraniSimptom = null;

                    for (int k = 0; k < simptomi.size() && iteratorSimptoma.hasNext(); ++k) {
                        simptom = iteratorSimptoma.next();
                        if (k == (element - 1)) {
                            pronadeniOdabraniSimptom = simptom;
                        }
                    }

                    odabraniSimptomi.add(pronadeniOdabraniSimptom);

                } );

                // Provjera duplikata unosa Simptoma

                if (bolesti.size() > 0) {

                    provjeraBolestiIstihSimptoma(bolesti, odabraniSimptomi);

                }

                // Provjera da li je unos bolest ili virus i unos u polje bolesti

                bolesti.add(new Bolest(idBolesti, nazivBolesti, odabraniSimptomi));

            }

        } catch (IOException ex) {

            logger.error("Ne mogu pronaci datoteku.", ex);

        } catch (NumberFormatException  exe) {

            logger.error("Greska prilikom citanja broja!", exe);

        }  catch (BolestIstihSimptoma exc) {

            logger.error(exc.getMessage(), exc);

        }

        System.out.println("Učitavanje podataka o virusima...");


        try (
                FileReader filereader = new FileReader(unosVirusa);
                BufferedReader reader = new BufferedReader(filereader);
        ) {

            while ((procitanaLinija = reader.readLine()) != null) {

                Set<Simptom> odabraniSimptomi = new HashSet<>();

                idVirusa = Long.parseLong(procitanaLinija);

                logger.info("Unesen je id simptoma: " + idVirusa);

                nazivVirusa = reader.readLine();

                logger.info("Unesen je naziv virusa: " + nazivVirusa);

                Arrays.stream(reader.readLine().split(",")).forEach(el -> {

                    // Iteracija simptoma po indeksu

                    int element = Integer.parseInt(el);

                    Simptom simptom;

                    Iterator<Simptom> iteratorSimptoma = simptomi.iterator();
                    Simptom pronadeniOdabraniSimptom = null;

                    for (int k = 0; k < simptomi.size() && iteratorSimptoma.hasNext(); ++k) {
                        simptom = iteratorSimptoma.next();
                        if (k == (element - 1)) {
                            pronadeniOdabraniSimptom = simptom;
                        }
                    }

                    odabraniSimptomi.add(pronadeniOdabraniSimptom);

                } );

                // Provjera duplikata unosa Simptoma

                if (bolesti.size() > 0) {

                    provjeraBolestiIstihSimptoma(bolesti, odabraniSimptomi);

                }

                // Provjera da li je unos bolest ili virus i unos u polje bolesti

                bolesti.add(new Virus(idVirusa, nazivVirusa, odabraniSimptomi));

            }

        } catch (IOException ex) {

            logger.error("Ne mogu pronaci datoteku.", ex);

        } catch (NumberFormatException  exe) {

            logger.error("Greska prilikom citanja broja!", exe);

        }  catch (BolestIstihSimptoma exc) {

            logger.error(exc.getMessage(), exc);

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
        File unosOsoba = new File("dat/osobe.txt");
        String procitanaLinija;
        Long odabranaZupanija, odabranaUnesenaBolest, idOsobe;
        String ime, prezime;
        Integer starost = 0;
        Zupanija zupanija = null;
        Bolest bolest = null;

        System.out.println("Učitavanje osoba...");

        try (
                FileReader filereader = new FileReader(unosOsoba);
                BufferedReader reader = new BufferedReader(filereader);
        ) {

            while ((procitanaLinija = reader.readLine()) != null) {

                List<Osoba> finalKontaktiraneOsobe = new ArrayList<>();

                idOsobe = Long.parseLong(procitanaLinija);

                logger.info("Unesen je id osobe: " + idOsobe);

                ime = reader.readLine();

                logger.info("Unesen je ime osobe: " + ime);

                prezime = reader.readLine();

                logger.info("Unesen je ime osobe: " + prezime);

                starost = Integer.parseInt(reader.readLine());

                // odabir zupanije iz seta zupanija po indeksu

                odabranaZupanija = Long.parseLong(reader.readLine());

                Iterator<Zupanija> iteratorZupanija = zupanije.iterator();

                for (int j = 0; j < zupanije.size() && iteratorZupanija.hasNext(); ++j) {
                    zupanija = iteratorZupanija.next();
                    if (zupanija.getId().compareTo(odabranaZupanija) == 0) {
                        break;
                    }
                }

                odabranaUnesenaBolest = Long.parseLong(reader.readLine());

                Iterator<Bolest> iteratorBolesti = bolesti.iterator();

                for (int j = 0; j < bolesti.size() && iteratorBolesti.hasNext(); ++j) {
                    bolest = iteratorBolesti.next();
                    if (bolest.getId().compareTo(odabranaUnesenaBolest) == 0) {
                        break;
                    }
                }

                if(osobe.size() > 0) {

                    procitanaLinija = reader.readLine();

                    if(procitanaLinija.compareTo("nema") != 0) {

                        Arrays.stream(procitanaLinija.split(",")).forEach(el -> {

                            for(Osoba o : osobe) {
                                if(o.getId().compareTo(Long.parseLong(el)) == 0) {
                                    finalKontaktiraneOsobe.add(o);
                                }
                            }

                        });

                        osobe.add(new Osoba.Builder(idOsobe).ime(ime).prezime(prezime).starost(starost).zupanija(zupanija)
                                .zarazenBolescu(bolest).kontaktiraneOsobe(finalKontaktiraneOsobe).build());

                    } else {
                        osobe.add(new Osoba.Builder(idOsobe).ime(ime).prezime(prezime).starost(starost).zupanija(zupanija)
                                .zarazenBolescu(bolest).build());
                    }

                } else {
                    osobe.add(new Osoba.Builder(idOsobe).ime(ime).prezime(prezime).starost(starost).zupanija(zupanija)
                            .zarazenBolescu(bolest).build());
                }


            }

        } catch (IOException ex) {

            logger.error("Ne mogu pronaci datoteku.", ex);

        } catch (NumberFormatException  exe) {

            logger.error("Greska prilikom citanja broja!", exe);

        }
    }

//    /**
//     * Provjerava postojanost odabrane kontaktirane osobe <code>int odabranaKontaktiranaOsoba</code> u polju
//     * <code>int[] odabraneUneseneKontaktiraneOsobe</code> i provjerava duplikate
//     * ako postoji duplikat baca iznimku <code>throw new DuplikatKontaktiraneOsobe("Prilikom unosa odabira kontaktirane osobe, unesena je prethodno odabrana osoba (duplikat): "
//     * + Integer.toString(odabranaKontaktiranaOsoba));</code>
//     *
//     * @param odabranaUnesenaKontaktiranaOsoba unesena odabrana kontaktirana osoba
//     * @param odabraneUneseneKontaktiraneOsobe polje prethodno odabranih kontaktiranih osoba
//     * @throws DuplikatKontaktiraneOsobe iznimka koja se baca u slučaju kada su uneseni duplikati
//     */
//
//    private static void provjeraDuplikataKontaktiranihOsoba(Osoba odabranaUnesenaKontaktiranaOsoba, List<Osoba> odabraneUneseneKontaktiraneOsobe) throws DuplikatKontaktiraneOsobe {
//
//        // (Provjera duplikata) Provjera postojanosti Odabrane Kontaktirane Osobe u prethodno Odabranim Kontaktiranim Osobama
//
//        if (odabraneUneseneKontaktiraneOsobe.contains(odabranaUnesenaKontaktiranaOsoba)) {
//
//            System.out.println("Osoba je već odabrana, molimo ponovno unesite!");
//
//            throw new DuplikatKontaktiraneOsobe("Prilikom unosa odabira kontaktirane osobe, unesena je prethodno odabrana osoba (duplikat)");
//
//        }
//    }
}




























