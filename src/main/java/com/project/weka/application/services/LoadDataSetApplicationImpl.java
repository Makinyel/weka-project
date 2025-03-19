package com.project.weka.application.services;

import com.project.weka.application.input.port.LoadDataSetApplication;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class LoadDataSetApplicationImpl implements LoadDataSetApplication {

  private Classifier classifier;
  private Instances dataFormat;

  @Override
  public String loadDataSet(String urlDataSet) {
    try {
      // Cargar el dataset desde resources
      InputStream inputStream = getClass().getClassLoader()
          .getResourceAsStream(urlDataSet);
      assert inputStream != null;
      dataFormat = new Instances(new BufferedReader(new InputStreamReader(inputStream)));
      dataFormat.setClassIndex(dataFormat.numAttributes() - 1);

      // Entrenar modelo RandomForest
      classifier = new RandomForest();
      classifier.buildClassifier(dataFormat);

      // Mostrar información del dataset

      return "Número de instancias: " + dataFormat.numInstances() + "\n"
          + "Número de atributos: " + dataFormat.numAttributes() + "\n"
          + "Class: \n" + dataFormat.classAttribute() + "\n";
    } catch (
        Exception e) {
      e.printStackTrace();
      return "Error al cargar el dataset";
    }
  }

  @Override
  public Instances getInstanceDataForm(String urlDataSet) {
    try {
      // Cargar el dataset desde resources
      InputStream inputStream = getClass().getClassLoader()
          .getResourceAsStream(urlDataSet);
      assert inputStream != null;
      dataFormat = new Instances(new BufferedReader(new InputStreamReader(inputStream)));
      dataFormat.setClassIndex(dataFormat.numAttributes() - 1);

      // Entrenar modelo RandomForest
      classifier = new RandomForest();
      classifier.buildClassifier(dataFormat);

      // Mostrar información del dataset
      StringBuilder info = new StringBuilder();
      info.append("Número de instancias: ").append(dataFormat.numInstances()).append("\n");
      info.append("Número de atributos: ").append(dataFormat.numAttributes()).append("\n");

      return dataFormat;
    } catch (
        Exception e) {
      e.printStackTrace();
      System.out.println("Error al cargar el dataset!!!!!!");
      return null;
    }
  }
}
