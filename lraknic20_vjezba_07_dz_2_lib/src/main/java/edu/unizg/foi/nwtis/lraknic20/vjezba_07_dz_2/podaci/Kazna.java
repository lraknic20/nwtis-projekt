package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci;

public class Kazna {

  private int id;
  private long vrijemePocetak;
  private long vrijemeKraj;
  private double brzina;
  private double gpsSirina;
  private double gpsDuzina;
  private double gpsSirinaRadar;
  private double gpsDuzinaRadar;

  public Kazna() {
    // TODO Auto-generated constructor stub
  }

  public Kazna(int id, long vrijemePocetak, long vrijemeKraj, double brzina, double gpsSirina,
      double gpsDuzina, double gpsSirinaRadar, double gpsDuzinaRadar) {
    super();
    this.id = id;
    this.vrijemePocetak = vrijemePocetak;
    this.vrijemeKraj = vrijemeKraj;
    this.brzina = brzina;
    this.gpsSirina = gpsSirina;
    this.gpsDuzina = gpsDuzina;
    this.gpsSirinaRadar = gpsSirinaRadar;
    this.gpsDuzinaRadar = gpsDuzinaRadar;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getVrijemePocetak() {
    return vrijemePocetak;
  }

  public void setVrijemePocetak(long vrijemePocetak) {
    this.vrijemePocetak = vrijemePocetak;
  }

  public long getVrijemeKraj() {
    return vrijemeKraj;
  }

  public void setVrijemeKraj(long vrijemeKraj) {
    this.vrijemeKraj = vrijemeKraj;
  }

  public double getBrzina() {
    return brzina;
  }

  public void setBrzina(double brzina) {
    this.brzina = brzina;
  }

  public double getGpsSirina() {
    return gpsSirina;
  }

  public void setGpsSirina(double gpsSirina) {
    this.gpsSirina = gpsSirina;
  }

  public double getGpsDuzina() {
    return gpsDuzina;
  }

  public void setGpsDuzina(double gpsDuzina) {
    this.gpsDuzina = gpsDuzina;
  }

  public double getGpsSirinaRadar() {
    return gpsSirinaRadar;
  }

  public void setGpsSirinaRadar(double gpsSirinaRadar) {
    this.gpsSirinaRadar = gpsSirinaRadar;
  }

  public double getGpsDuzinaRadar() {
    return gpsDuzinaRadar;
  }

  public void setGpsDuzinaRadar(double gpsDuzinaRadar) {
    this.gpsDuzinaRadar = gpsDuzinaRadar;
  }
}
