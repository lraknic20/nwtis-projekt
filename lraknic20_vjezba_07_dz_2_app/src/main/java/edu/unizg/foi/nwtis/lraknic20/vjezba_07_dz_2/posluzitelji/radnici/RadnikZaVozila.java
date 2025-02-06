package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.radnici;

import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.PosluziteljZaVozila;

/**
 * Radnik za vozila prima naredbe simulatora vozila te provjerava ispravnost. Ako se vozilo nalazi u
 * području radara, šalje podatke o vozilo tom radaru.
 */
public class RadnikZaVozila implements Runnable {

  /** Poslužitelj za vozila. */
  private PosluziteljZaVozila posluziteljZaVozila;

  /** Klijentski kanal. */
  AsynchronousSocketChannel klijentskiKanal;

  /** Predložak vozila. */
  private Pattern predlozakVozila = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<snaga>-?\\d+([.]\\d+)?) "
          + "(?<struja>-?\\d+([.]\\d+)?) (?<visina>-?\\d+([.]\\d+)?) (?<gpsBrzina>-?\\d+([.]\\d+)?) "
          + "(?<tempVozila>\\d+) (?<postotakBaterija>\\d+) (?<naponBaterija>-?\\d+([.]\\d+)?) "
          + "(?<kapacitetBaterija>\\d+) (?<tempBaterija>\\d+) (?<preostaloKm>-?\\d+([.]\\d+)?) "
          + "(?<ukupnoKm>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

  private Pattern predlozakZaPokretanjePracenjaVozila =
      Pattern.compile("^VOZILO START (?<id>\\d+)$");

  private Pattern predlozakZaZaustavljanjePracenjaVozila =
      Pattern.compile("^VOZILO STOP (?<id>\\d+)$");

  /** Poklapanje vozila. */
  private Matcher poklapanjeVozila;

  /**
   * Instancira novi radnik za vozila.
   *
   * @param posluziteljZaVozila Poslužitelj za vozila
   * @param klijentskiKanal Klijentski kanal
   */
  public RadnikZaVozila(PosluziteljZaVozila posluziteljZaVozila,
      AsynchronousSocketChannel klijentskiKanal) {
    super();
    this.posluziteljZaVozila = posluziteljZaVozila;
    this.klijentskiKanal = klijentskiKanal;
  }

  /**
   * Pokretanje kanala koji služi za komunikaciju s klijetom. Ne šalju se odgovori klijentu.
   */
  @Override
  public void run() {
    SocketAddress klijentskaAdresa;
    try {
      klijentskaAdresa = this.klijentskiKanal.getRemoteAddress();

      try {
        while (true) {
          if ((this.klijentskiKanal != null) && (this.klijentskiKanal.isOpen())) {
            var buffer = ByteBuffer.allocate(262144);
            Future<Integer> readBuff = this.klijentskiKanal.read(buffer);
            readBuff.get();
            String poruka = new String(buffer.array()).trim();

            if (poruka.isEmpty())
              break;

            String porukaOdgovor = null;

            String[] naredbe = poruka.split("\\n");
            for (var naredba : naredbe) {
              porukaOdgovor = provjeriPoklapanje(naredba);
            }

            if (porukaOdgovor != null) {
              Future<Integer> writeBuff =
                  klijentskiKanal.write(ByteBuffer.wrap(porukaOdgovor.getBytes()));
              writeBuff.get();
            }

            buffer.clear();
          } else {
            break;
          }
        }
      } finally {
        this.klijentskiKanal.close();
      }
    } catch (Exception e) {
    }
  }

  /**
   * Metoda za provjeri poklapanja naredbe.
   *
   * @param poruka poruka
   * @return true, if successful
   */
  public String provjeriPoklapanje(String poruka) {
    this.poklapanjeVozila = this.predlozakVozila.matcher(poruka);
    var statusPoklapanja = poklapanjeVozila.matches();

    if (statusPoklapanja) {
      PodaciVozila vozilo = kreirajObjektVozilo();

      obrada(vozilo);

      return null;
    }

    var poklapanjeZaPokretanjePracenjaVozila =
        this.predlozakZaPokretanjePracenjaVozila.matcher(poruka);
    var statusPoklapanjaZaPokretanjePracenjaVozila = poklapanjeZaPokretanjePracenjaVozila.matches();

    if (statusPoklapanjaZaPokretanjePracenjaVozila) {
      pokreniPracenje(poklapanjeZaPokretanjePracenjaVozila);
      return "OK";
    }

    var poklapanjeZaZaustavljanjePracenjaVozila =
        this.predlozakZaZaustavljanjePracenjaVozila.matcher(poruka);
    var statusPoklapanjaZaZaustavljanjePracenjaVozila =
        poklapanjeZaZaustavljanjePracenjaVozila.matches();

    if (statusPoklapanjaZaZaustavljanjePracenjaVozila) {
      zaustaviPracenje(poklapanjeZaZaustavljanjePracenjaVozila);
      return "OK";
    }

    System.out.println("ERROR 20 Neispravna sintaksa komande.");
    return null;
  }

  private PodaciVozila kreirajObjektVozilo() {
    PodaciVozila vozilo = new PodaciVozila(Integer.valueOf(poklapanjeVozila.group("id")),
        Integer.valueOf(poklapanjeVozila.group("broj")),
        Long.valueOf(poklapanjeVozila.group("vrijeme")),
        Double.valueOf(poklapanjeVozila.group("brzina")),
        Double.valueOf(poklapanjeVozila.group("snaga")),
        Double.valueOf(poklapanjeVozila.group("struja")),
        Double.valueOf(poklapanjeVozila.group("visina")),
        Double.valueOf(poklapanjeVozila.group("gpsBrzina")),
        Integer.valueOf(poklapanjeVozila.group("tempVozila")),
        Integer.valueOf(poklapanjeVozila.group("postotakBaterija")),
        Double.valueOf(poklapanjeVozila.group("naponBaterija")),
        Integer.valueOf(poklapanjeVozila.group("kapacitetBaterija")),
        Integer.valueOf(poklapanjeVozila.group("tempBaterija")),
        Double.valueOf(poklapanjeVozila.group("preostaloKm")),
        Double.valueOf(poklapanjeVozila.group("ukupnoKm")),
        Double.valueOf(poklapanjeVozila.group("gpsSirina")),
        Double.valueOf(poklapanjeVozila.group("gpsDuzina")));
    return vozilo;
  }

  private void pokreniPracenje(Matcher poklapanjeZaPokretanjePracenjaVozila) {
    var id = Integer.valueOf(poklapanjeZaPokretanjePracenjaVozila.group("id"));
    var postojiVozilo = this.posluziteljZaVozila.centralniSustav.svaVozila.containsKey(id);

    if (!postojiVozilo)
      this.posluziteljZaVozila.centralniSustav.svaVozila.put(id, id);
  }

  private void zaustaviPracenje(Matcher poklapanjeZaZaustavljanjePracenjaVozila) {
    var id = Integer.valueOf(poklapanjeZaZaustavljanjePracenjaVozila.group("id"));
    var postojiVozilo = this.posluziteljZaVozila.centralniSustav.svaVozila.containsKey(id);

    if (postojiVozilo)
      this.posluziteljZaVozila.centralniSustav.svaVozila.remove(id);
  }

  /**
   * Metoda koja provjerava nalazi li se vozilo u području kojeg radara te ako se nalazi, šalje
   * podatke radaru.
   *
   * @param vozilo vozilo
   */
  public void obrada(PodaciVozila vozilo) {
    this.posluziteljZaVozila.centralniSustav.svaVozila.forEach((kljuc, vrijednost) -> {
      if (kljuc == vozilo.id()) {
        Gson gson = new Gson();
        String json = gson.toJson(vozilo);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://20.24.5.5:8080/nwtis/v1/api/vozila"))
            .header("Content-Type", "application/json").POST(BodyPublishers.ofString(json)).build();

        try {
          HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

          if (response.statusCode() != 200) {
            System.out
                .println("ERROR 21 POST metoda na RESTful web servis nije uspješno obavljena");
          }
        } catch (Exception e) {
          System.out.println("ERROR 21 POST metoda na RESTful web servis nije uspješno obavljena");;
        }
      }
    });

    for (PodaciRadara radar : this.posluziteljZaVozila.centralniSustav.sviRadari.values()) {
      var udaljenost = GpsUdaljenostBrzina.udaljenostKm(vozilo.gpsSirina(), vozilo.gpsDuzina(),
          radar.gpsSirina(), radar.gpsDuzina());
      var maksUdaljenost = radar.maksUdaljenost();
      if (udaljenost * 1000 < maksUdaljenost) {
        var komanda = new StringBuilder();
        var razmak = " ";
        komanda.append("VOZILO").append(razmak).append(vozilo.id()).append(razmak)
            .append(vozilo.vrijeme()).append(razmak).append(vozilo.brzina()).append(razmak)
            .append(vozilo.gpsSirina()).append(razmak).append(vozilo.gpsDuzina()).append("\n");

        MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(), radar.mreznaVrataRadara(),
            komanda.toString());
      }
    }
  }

}
