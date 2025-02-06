FROM eclipse-temurin:21-jre

COPY ./target/lraknic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar /usr/app/
COPY ./NWTiS_DZ1_CS.txt /usr/app/
COPY ./NWTiS_DZ1_R1.txt /usr/app/
COPY ./NWTiS_DZ1_R2.txt /usr/app/
COPY ./NWTiS_DZ1_PK.txt /usr/app/
COPY ./NWTiS_DZ1_SV.txt /usr/app/
COPY ./NWTiS_DZ1_V1.csv /usr/app/

WORKDIR /usr/app

EXPOSE 8000 8001 8002 8010 8011 8020

COPY ./docker-app.sh /usr/app/

RUN chmod -R 777 /usr/app/docker-app.sh

CMD ["/usr/app/docker-app.sh"]