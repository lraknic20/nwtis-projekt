package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci;

public class Radar {

  private int id;
  private String adresaRadara;
  private int mreznaVrataRadara;
  private int maksUdaljenost;
  private double gpsSirina;
  private double gpsDuzina;

  public Radar() {
    // TODO Auto-generated constructor stub
  }

  public Radar(int id, String adresaRadara, int mreznaVrataRadara, int maksUdaljenost,
      double gpsSirina, double gpsDuzina) {
    super();
    this.id = id;
    this.adresaRadara = adresaRadara;
    this.mreznaVrataRadara = mreznaVrataRadara;
    this.maksUdaljenost = maksUdaljenost;
    this.gpsSirina = gpsSirina;
    this.gpsDuzina = gpsDuzina;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAdresaRadara() {
    return adresaRadara;
  }

  public void setAdresaRadara(String adresaRadara) {
    this.adresaRadara = adresaRadara;
  }

  public int getMreznaVrataRadara() {
    return mreznaVrataRadara;
  }

  public void setMreznaVrataRadara(int mreznaVrataRadara) {
    this.mreznaVrataRadara = mreznaVrataRadara;
  }

  public int getMaksUdaljenost() {
    return maksUdaljenost;
  }

  public void setMaksUdaljenost(int maksUdaljenost) {
    this.maksUdaljenost = maksUdaljenost;
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

}
