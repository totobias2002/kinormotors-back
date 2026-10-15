package com.kinormotors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class KinormotorsApplication {

    public static void main(String[] args) {
        // Zona horaria fija: los turnos y reportes son de Argentina, sin importar donde corra el servidor.
        // (Tambien evita que Postgres rechace el alias viejo "America/Buenos_Aires" que reporta Windows.)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        SpringApplication.run(KinormotorsApplication.class, args);
    }
}
