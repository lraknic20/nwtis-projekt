#!/bin/sh

java -cp lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.CentralniSustav NWTiS_DZ1_CS.txt &

sleep 1

java -cp lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R1.txt &

sleep 1

java -cp lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R2.txt &

sleep 1

java -cp lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.PosluziteljKazni NWTiS_DZ1_PK.txt

#sleep 1

#java -cp lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.klijenti.SimulatorVozila NWTiS_DZ1_SV.txt NWTiS_DZ1_V1.csv 1