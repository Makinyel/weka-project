package com.project.weka.application.services;

import java.io.InputStream;
import java.io.ObjectInputStream;

import org.springframework.stereotype.Service;

import com.project.weka.application.input.port.LoadModelApplication;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import weka.classifiers.Classifier;

@Log4j2
@Service
@AllArgsConstructor
public class LoadModelApplicationImpl implements LoadModelApplication {

    @Override
    public Classifier loadModel(String url) throws Exception{
        log.info("Intentando cargar modelo desde la URL: {}", url);
        InputStream modelStream = getClass().getResourceAsStream(url);

        if (modelStream == null) {
            String errorMessage = "No se pudo encontrar el archivo del modelo en la ruta especificada: " + url;
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try (ObjectInputStream ois = new ObjectInputStream(modelStream)) {
            Classifier model = (Classifier) ois.readObject();
            log.info("Modelo cargado exitosamente desde: {}", url);
            return model;
        } catch (ClassNotFoundException e) {
            log.error("Error al cargar el modelo. No se encontró la clase del objeto serializado.", e);
            throw e;
        } catch (Exception e) {
            log.error("Error general al cargar el modelo desde: {}", url, e);
            throw e;
        }
    }
      
}

