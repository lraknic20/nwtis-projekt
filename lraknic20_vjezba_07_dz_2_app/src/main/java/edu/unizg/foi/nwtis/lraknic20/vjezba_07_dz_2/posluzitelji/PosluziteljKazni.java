package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciKazne;

/**
 * Poslužitelj kazni radi u jednodretvenon načinu sa sinkronim slanjem/primanjem poruka. U njega se
 * upisiju evidencije kazni, ispisuje evidencija za pojedino vozilu u nekom intervalu, te ispisuje
 * statistika za sva vozila u nekom intervalu.
 */
public class PosluziteljKazni {

  /** sdf varijabla za prikaz datuma u obliku dd.MM.yyyy HH:mm:ss.SSS. */
  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

  /** Mrežna vrata. */
  int mreznaVrata;

  /** Predložak kazna za prepoznavanje uzorka za dodavanje nove evidencije kazne. */
  private Pattern predlozakKazna = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<vrijemePocetak>\\d+) (?<vrijemeKraj>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<gpsSirinaRadar>\\d+[.]\\d+) (?<gpsDuzinaRadar>\\d+[.]\\d+)$");

  /**
   * Predložak evidencija kazni za prepoznavanje uzorka za ispis evidencije za jedno vozilo u nekom
   * intervalu.
   */
  private Pattern predlozakEvidencijaKazni =
      Pattern.compile("^VOZILO (?<id>\\d+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");

  /**
   * Predložak statistika za prepoznavanje uzorka za ispis statistike za sva vozila u nekom
   * intervalu.
   */
  private Pattern predlozakStatistika =
      Pattern.compile("^STATISTIKA (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");

  private Pattern predlozakTest = Pattern.compile("^TEST$");

  /** Poklapanje kazna. */
  private Matcher poklapanjeKazna;

  /** Poklapanje evidencija kazni. */
  private Matcher poklapanjeEvidencijaKazni;

  /** Poklapanje statistika. */
  private Matcher poklapanjeStatistika;

  /** Popis svih kazni. */
  private volatile Queue<PodaciKazne> sveKazne = new ConcurrentLinkedQueue<>();

  /**
   * Glavna metoda u kojoj se provjerava broj argumenata, a zatim preuzima postavke iz txt datoteke
   * i pokreće poslužitelja kazni.
   *
   * @param argumenti konfiguracija
   */
  public static void main(String[] argumenti) {
    if (argumenti.length != 1) {
      System.out.println("Broj argumenata nije 1.");
      return;
    }

    PosluziteljKazni posluziteljKazni = new PosluziteljKazni();
    try {
      posluziteljKazni.preuzmiPostavke(argumenti);

      posluziteljKazni.pokreniPosluzitelja();

    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Pokreni poslužitelja metoda pokrece poslužitelja.
   */
  public void pokreniPosluzitelja() {
    boolean kraj = false;

    try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.mreznaVrata)) {
      while (!kraj) {
        var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
        BufferedReader citac =
            new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
        OutputStream out = mreznaUticnica.getOutputStream();
        PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, "utf8"), true);
        var redak = citac.readLine();

        mreznaUticnica.shutdownInput();
        pisac.println(obradaZahtjeva(redak));

        pisac.flush();
        mreznaUticnica.shutdownOutput();
        mreznaUticnica.close();
      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Obrada zahtjeva metoda sluzi za vraćanje greške ako je sintaksa neispravna, a inače obrađuje
   * zahtjev.
   *
   * @param zahtjev zahtjev klijenta
   * @return the string
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null) {
      return "ERROR 40 Neispravna sintaksa komande.";
    }
    var odgovor = obradaZahtjevaKazna(zahtjev);
    if (odgovor != null) {
      return odgovor;
    }

    return "ERROR 40 Neispravna sintaksa komande.";
  }

  /**
   * Obrada zahtjeva kazna je metoda u kojoj se izvršava glavna logike te se po određenom zahtjevu
   * vraća odgovarajući odgovor.
   *
   * @param zahtjev zahtjev klijenta
   * @return the string
   */
  public String obradaZahtjevaKazna(String zahtjev) {
    this.poklapanjeKazna = this.predlozakKazna.matcher(zahtjev);
    var statusKazna = poklapanjeKazna.matches();

    this.poklapanjeEvidencijaKazni = this.predlozakEvidencijaKazni.matcher(zahtjev);
    var statusEvidencijaKazni = poklapanjeEvidencijaKazni.matches();

    this.poklapanjeStatistika = this.predlozakStatistika.matcher(zahtjev);
    var statusStatistika = poklapanjeStatistika.matches();

    if (statusKazna) {
      return obradiKaznu();
    }

    if (statusEvidencijaKazni) {
      return obradiEvidenciju();
    }

    if (statusStatistika) {
      return obradiStatistiku();
    }

    var poklapanjeZaTest = this.predlozakTest.matcher(zahtjev);
    var statusPoklapanjaZaTest = poklapanjeZaTest.matches();

    if (statusPoklapanjaZaTest) {
      return "OK";
    }

    return null;
  }

  /**
   * Spremanje nove kazne.
   *
   * @return the string
   */
  private String obradiKaznu() {
    var kazna = new PodaciKazne(Integer.valueOf(this.poklapanjeKazna.group("id")),
        Long.valueOf(this.poklapanjeKazna.group("vrijemePocetak")),
        Long.valueOf(this.poklapanjeKazna.group("vrijemeKraj")),
        Double.valueOf(this.poklapanjeKazna.group("brzina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsSirina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsDuzina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsSirinaRadar")),
        Double.valueOf(this.poklapanjeKazna.group("gpsDuzinaRadar")));

    this.sveKazne.add(kazna);
    System.out.println("Id: " + kazna.id() + " Vrijeme od: " + sdf.format(kazna.vrijemePocetak())
        + "  Vrijeme do: " + sdf.format(kazna.vrijemeKraj()) + " Brzina: " + kazna.brzina()
        + " GPS: " + kazna.gpsSirina() + ", " + kazna.gpsDuzina());

    Gson gson = new Gson();
    String json = gson.toJson(kazna);

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create("http://20.24.5.5:8080/nwtis/v1/api/kazne"))
            .header("Content-Type", "application/json").POST(BodyPublishers.ofString(json)).build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        return "ERROR 42 POST metoda na RESTful web servis nije uspješno obavljena";
      }
    } catch (Exception e) {
      return "ERROR 42 POST metoda na RESTful web servis nije uspješno obavljena";
    }

    return "OK";
  }

  /**
   * Vraćanje evidencije za određeno vozilo u nekom intervalu.
   *
   * @return the string
   */
  private String obradiEvidenciju() {
    int id = Integer.valueOf(this.poklapanjeEvidencijaKazni.group("id"));
    long vrijemeOd = Long.valueOf(this.poklapanjeEvidencijaKazni.group("vrijemeOd"));
    long vrijemeDo = Long.valueOf(this.poklapanjeEvidencijaKazni.group("vrijemeDo"));

    List<PodaciKazne> sveKazneLista = new ArrayList<>(this.sveKazne);
    Collections.reverse(sveKazneLista);

    for (PodaciKazne kazna : sveKazneLista) {
      if (kazna.id() == id && kazna.vrijemeKraj() >= vrijemeOd
          && kazna.vrijemeKraj() <= vrijemeDo) {
        return "OK " + kazna.vrijemeKraj() + " " + kazna.brzina() + " " + kazna.gpsSirinaRadar()
            + " " + kazna.gpsDuzinaRadar();
      }
    }
    return "ERROR 41 e-vozilo nema kazne u zadanom vremenu.";
  }

  /**
   * Vraćanje statistike u nekom intervalu.
   *
   * @return the string
   */
  private String obradiStatistiku() {
    long vrijemeOd = Long.valueOf(this.poklapanjeStatistika.group("vrijemeOd"));
    long vrijemeDo = Long.valueOf(this.poklapanjeStatistika.group("vrijemeDo"));

    ConcurrentMap<Integer, Integer> listaKazni = new ConcurrentHashMap<>();
    for (PodaciKazne kazna : this.sveKazne) {
      if (listaKazni.get(kazna.id()) == null)
        listaKazni.put(kazna.id(), 0);
      if (kazna.vrijemeKraj() >= vrijemeOd && kazna.vrijemeKraj() <= vrijemeDo) {
        int brojKazni = 0;
        Integer brojKazniIzListe = listaKazni.get(kazna.id());
        if (brojKazniIzListe != null) {
          brojKazni = brojKazniIzListe;
        }
        listaKazni.put(kazna.id(), ++brojKazni);
      }
    }

    if (listaKazni.isEmpty())
      return "ERROR 49 Ne postoji evidencija kazni.";

    var ispis = new StringBuilder();
    ispis.append("OK");

    for (Map.Entry<Integer, Integer> kaznaZaVozilo : listaKazni.entrySet()) {
      int idVozilo = kaznaZaVozilo.getKey();
      int brojKazni = kaznaZaVozilo.getValue();
      ispis.append(" ").append(idVozilo).append(" ").append(brojKazni).append(";");
    }
    return ispis.toString();
  }

  /**
   * Preuzmanje postavki iz txt datoteke.
   *
   * @param argumenti konfiguracija
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public void preuzmiPostavke(String[] argumenti)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(argumenti[0]);

    this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));
  }
}

