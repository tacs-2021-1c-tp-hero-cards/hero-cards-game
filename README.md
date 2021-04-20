# UTN FRBA TACS 2021-1C
###  Trabajo Practico 2021 1C
* [TACS Home Page](https://www.tacs-utn.com.ar/)
* [Documento del enunciado](https://docs.google.com/document/d/e/2PACX-1vSDeXS8A44GMMKxL47FTspYC6_4BXiWP2_lwo2Oiy4P7oRXORfseOdQ9F3K8vZ_xyHNPf6euMP1wEIV/pub)

### Requisitos
* git `2.25.1`
* jdk 11 `11.0.7+8-LTS`
* Docker `19.03.12` o superior

### Como importar el proyecto
* `git clone git@github.com:tacs-2021-1c-tp-hero-cards/hero-cards-game.git`
* Luego importar el archivo `settings.gradle` desde el IDE

## Instrucciones para iniciar la aplicación con Docker
* Ejecutar estos comandos en el directorio donde se clonó el proyecto:
  - `gradle clean build`
  - `sudo docker-compose up`