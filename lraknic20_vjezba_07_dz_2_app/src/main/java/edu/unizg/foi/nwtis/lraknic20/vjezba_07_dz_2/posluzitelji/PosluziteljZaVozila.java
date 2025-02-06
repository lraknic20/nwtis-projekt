package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.radnici.RadnikZaVozila;

/**
 * Poslužitelj za vozila radi u višedretvenom načinu rada s asinkronim slanjem/primanjem poruka. Za
 * svakog klijenta stvara novu virtualnu dretvu koja pokreće radnika za vozila.
 */
public class PosluziteljZaVozila implements Runnable {

  /** Mrežna vrata. */
  private int mreznaVrata;

  /** Centralni sustav. */
  public CentralniSustav centralniSustav;

  /** Izvrsivac. */
  static ExecutorService izvrsivac;

  /** Broj dretvi. */
  volatile AtomicInteger brojDretvi = new AtomicInteger(0);

  /** Lista odgovora. */
  volatile List<Future<Integer>> odgovori = new ArrayList<Future<Integer>>();

  /**
   * Instancira novog poslužitelja za vozila.
   *
   * @param mreznaVrata the mrezna vrata
   * @param centralniSustav the centralni sustav
   */
  public PosluziteljZaVozila(int mreznaVrata, CentralniSustav centralniSustav) {
    super();
    this.mreznaVrata = mreznaVrata;
    this.centralniSustav = centralniSustav;
  }

  /**
   * Glavna metoda koja pokreće poslužitelja za vozila.
   */
  @Override
  public void run() {

    try {
      final AsynchronousServerSocketChannel posluzitelj =
          AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(this.mreznaVrata));
      izvrsivac = Executors.newVirtualThreadPerTaskExecutor();

      try {
        while (true) {
          Future<AsynchronousSocketChannel> prihvatiKanal = posluzitelj.accept();
          AsynchronousSocketChannel klijentskiKanal = prihvatiKanal.get();

          odgovori.add(izvrsivac.submit(() -> cekajObradiKlijenta(klijentskiKanal)));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      posluzitelj.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Metoda koja pokreće radnika za vozila.
   *
   * @param klijentskiKanal klijentski kanal
   * @return the integer
   */
  Integer cekajObradiKlijenta(AsynchronousSocketChannel klijentskiKanal) {
    int broj = brojDretvi.incrementAndGet();

    try {
      var obrada = new RadnikZaVozila(this, klijentskiKanal);
      var t = Thread.startVirtualThread(obrada);
      t.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    brojDretvi.decrementAndGet();
    return broj;
  }

}
