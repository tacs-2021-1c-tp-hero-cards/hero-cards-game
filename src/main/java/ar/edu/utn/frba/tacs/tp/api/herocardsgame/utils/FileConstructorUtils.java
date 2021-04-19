package ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileConstructorUtils {

    public static <T> T createFromFile(String file, Class<T> classReturn) {

        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, classReturn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
