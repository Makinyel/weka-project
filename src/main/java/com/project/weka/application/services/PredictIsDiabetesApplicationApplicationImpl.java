package com.project.weka.application.services;

import com.project.weka.application.input.port.PredictIsDiabetesApplication;
import com.project.weka.domain.model.Patient;
import com.project.weka.domain.model.PredictionResult;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

@AllArgsConstructor
@Service
public class PredictIsDiabetesApplicationApplicationImpl implements PredictIsDiabetesApplication {

  @Autowired
  private LoadDataSetApplicationImpl loadDataSetApplicationImpl;
  @Autowired
  private GeminiApplicationImpl geminiApplicationImpl;

  @Override
  public PredictionResult predict(Patient patient) {
    String defaultUrl = "dataSets/diabetes.arff";
    Instances data = loadDataSetApplicationImpl.getInstanceDataForm(defaultUrl);
    PredictionResult result = new PredictionResult();

    try {
      Instance newInstance = new DenseInstance(data.numAttributes());
      newInstance.setDataset(data);

      newInstance.setValue(0, patient.getPreg());
      newInstance.setValue(1, patient.getPlas());
      newInstance.setValue(2, patient.getPres());
      newInstance.setValue(3, patient.getSkin());
      newInstance.setValue(4, patient.getInsu());
      newInstance.setValue(5, patient.getMass());
      newInstance.setValue(6, patient.getPedi());
      newInstance.setValue(7, patient.getAge());

      Classifier classifier = new RandomForest();
      classifier.buildClassifier(data);

      double predictionIndex = classifier.classifyInstance(newInstance);
      String predictionClass = data.classAttribute().value((int) predictionIndex);
      double probability = classifier.distributionForInstance(newInstance)[(int) predictionIndex];

      result.setPrediction(predictionClass);
      result.setProbability(probability);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public PredictionResult predictUsingJ48(Patient patient) {
    String defaultUrl = "dataSets/diabetes.arff";
    Instances data = loadDataSetApplicationImpl.getInstanceDataForm(defaultUrl);
    PredictionResult result = new PredictionResult();

    try {
      Instance newInstance = new DenseInstance(data.numAttributes());
      newInstance.setDataset(data);

      // Preprocesamiento: Normalización de datos
      Normalize normalize = new Normalize();
      normalize.setInputFormat(data);
      Instances normalizedData = Filter.useFilter(data, normalize);

      //  Entrenar el modelo J48 (Árbol de Decisión)
      J48 model = new J48();
      model.setUnpruned(false); // Permitir poda del árbol para evitar sobreajuste
      model.buildClassifier(normalizedData);

      // Evaluar el modelo con validación cruzada (10 folds)
      Evaluation eval = new Evaluation(normalizedData);
      eval.crossValidateModel(model, normalizedData, 10, new Random(1));

      // Imprimir resultados
      System.out.println(eval.toSummaryString("\nResultados de Evaluación\n", false));
      System.out.println("Precisión: " + eval.pctCorrect() + "%\n");
      System.out.println("Matriz de Confusión: \n" + eval.toMatrixString());
      System.out.println("Árbol de Decisión generado por J48:\n" + model);

      newInstance.setValue(0, patient.getPreg());
      newInstance.setValue(1, patient.getPlas());
      newInstance.setValue(2, patient.getPres());
      newInstance.setValue(3, patient.getSkin());
      newInstance.setValue(4, patient.getInsu());
      newInstance.setValue(5, patient.getMass());
      newInstance.setValue(6, patient.getPedi());
      newInstance.setValue(7, patient.getAge());

      double predictionIndex = model.classifyInstance(newInstance);
      String predictionClass = data.classAttribute().value((int) predictionIndex);
      double probability = model.distributionForInstance(newInstance)[(int) predictionIndex];

      System.out.println("¿Tiene diabetes? \n" + (predictionIndex == 1.0 ? "Sí" : "No"));
      result.setPrediction(predictionClass);
      result.setProbability(probability * 100);

      if (predictionIndex == 1.0) {
        String adviceIA = geminiApplicationImpl.getText(
            "Brinda un consejo empático y motivador a una persona que ha sido diagnosticada "
                + "con diabetes recientemente. El consejo debe ser sencillo y corto no mas de "
                + "20 palabras");
        result.setAdvice(adviceIA);
        System.out.println("Consejo --> " + adviceIA);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
