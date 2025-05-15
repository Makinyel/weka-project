package com.project.weka.application.services;

import com.project.weka.application.input.port.PredicLoadApplication;
import com.project.weka.domain.model.LoadRequest;
import com.project.weka.domain.model.PredictionResult;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

@AllArgsConstructor
@Service
public class PredictLoadApplicationImpl implements PredicLoadApplication {

  @Autowired
  private LoadModelApplicationImpl loadModelApplicationImpl;
  @Autowired
  private LoadDataSetApplicationImpl loadDataSetApplicationImpl;
  @Autowired
  private GeminiApplicationImpl geminiApplicationImpl;
  
  @Override
  public PredictionResult predict(LoadRequest LoanRequest) {
    String dataSetUrl = "dataSets/load_approval.arff";
    Instances data = loadDataSetApplicationImpl.getInstanceDataForm(dataSetUrl);
    PredictionResult result = new PredictionResult();

    try {
      Instance newInstance = new DenseInstance(data.numAttributes());
      newInstance.setDataset(data);

      newInstance.setValue(1, LoanRequest.getNoOfDependents());
      newInstance.setValue(2, LoanRequest.getEducation());
      newInstance.setValue(3, LoanRequest.getSelfEmployed());
      newInstance.setValue(4, LoanRequest.getIncomeAnnum());
      newInstance.setValue(5, LoanRequest.getLoanAmount());
      newInstance.setValue(6, LoanRequest.getLoanTerm());
      newInstance.setValue(7, LoanRequest.getCibilScore());
      newInstance.setValue(8, LoanRequest.getResidentialAssetsValue());
      newInstance.setValue(9, LoanRequest.getCommercialAssetsValue());
      newInstance.setValue(10, LoanRequest.getLuxuryAssetsValue());
      newInstance.setValue(11, LoanRequest.getBankAssetValue());

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
  public PredictionResult predictUsingJ48(LoadRequest loadRequest) {
    String dataSetUrl = "dataSets/load_approval.arff";
    String modelUrl = "/models/zeroRModel.model";
    Instances data = loadDataSetApplicationImpl.getInstanceDataForm(dataSetUrl);
    PredictionResult result = new PredictionResult();

    try {
      Instance newInstance = new DenseInstance(data.numAttributes());
      newInstance.setDataset(data);

      Classifier model = loadModelApplicationImpl.loadModel(modelUrl);

      newInstance.setValue(1, loadRequest.getNoOfDependents());
      newInstance.setValue(2, loadRequest.getEducation());
      newInstance.setValue(3, loadRequest.getSelfEmployed());
      newInstance.setValue(4, loadRequest.getIncomeAnnum());
      newInstance.setValue(5, loadRequest.getLoanAmount());
      newInstance.setValue(6, loadRequest.getLoanTerm());
      newInstance.setValue(7, loadRequest.getCibilScore());
      newInstance.setValue(8, loadRequest.getResidentialAssetsValue());
      newInstance.setValue(9, loadRequest.getCommercialAssetsValue());
      newInstance.setValue(10, loadRequest.getLuxuryAssetsValue());
      newInstance.setValue(11, loadRequest.getBankAssetValue());

      double predictionIndex = model.classifyInstance(newInstance);
      String predictionClass = data.classAttribute().value((int) predictionIndex);
      double probability = model.distributionForInstance(newInstance)[(int) predictionIndex];

      System.out.println("¿Su prestamo será aprobado? \n" + (predictionIndex == 1.0 ? "Sí" : "No"));
      result.setPrediction(predictionClass);
      result.setProbability(probability * 100);

      if (predictionIndex == 1.0) {
        String adviceIA = geminiApplicationImpl.getText(
            "Brinda un consejo empático y motivador a una persona que ha sido aprobada para un prestamo."
                + "El consejo debe ser sencillo y corto no mas de 20 palabras");
        result.setAdvice(adviceIA);
        System.out.println("Consejo --> " + adviceIA);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
