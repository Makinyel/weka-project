package com.project.weka.application.services;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.weka.application.input.port.PredicLoadApplication;
import com.project.weka.domain.model.LoadRequest;
import com.project.weka.domain.model.PredictionResult;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

@Log4j2
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
  public String predict(LoadRequest loanRequest) {
    String dataSetUrl = "dataSets/load_approval.arff";
    String modelUrl = "/models/loanAmountLinearRegression.model";

    try {
        // Cargar dataset (estructura)
        Instances data = loadDataSetApplicationImpl.getInstanceDataForm(dataSetUrl);
        // Definir índice de la clase (loanAmount)
        data.setClassIndex(data.attribute("_loan_amount").index());

        // Cargar modelo de regresión lineal
        Classifier model = loadModelApplicationImpl.loadModel(modelUrl);

        // Crear nueva instancia con estructura del dataset
        Instance newInstance = new DenseInstance(data.numAttributes());
        newInstance.setDataset(data);

        // Setear atributos en la instancia
        newInstance.setValue(data.attribute("_education"), loanRequest.getEducation());
        newInstance.setValue(data.attribute("_self_employed"), loanRequest.getSelfEmployed());
        newInstance.setValue(data.attribute("_income_annum"), loanRequest.getIncomeAnnum());
        // Como la clase es loanAmount, se deja missing para predecir
        newInstance.setMissing(data.classIndex());
        newInstance.setValue(data.attribute("_loan_term"), loanRequest.getLoanTerm());
        newInstance.setValue(data.attribute("_cibil_score"), loanRequest.getCibilScore());
        newInstance.setValue(data.attribute("_residential_assets_value"), loanRequest.getResidentialAssetsValue());
        newInstance.setValue(data.attribute("_commercial_assets_value"), loanRequest.getCommercialAssetsValue());
        newInstance.setValue(data.attribute("_luxury_assets_value"), loanRequest.getLuxuryAssetsValue());
        newInstance.setValue(data.attribute("_bank_asset_value"), loanRequest.getBankAssetValue());

        // Predecir el valor numérico (loanAmount)
        double predictedLoanAmount = model.classifyInstance(newInstance);

        String formattedAmount = String.format(Locale.US, "%.2f", predictedLoanAmount);

        log.info("Monto del préstamo predicho (formateado): " + formattedAmount);

        return formattedAmount;

    } catch (Exception e) {
        log.error("Error al predecir el monto del préstamo", e);
        return "00000"; // Retornar un valor por defecto en caso de error
    }
  }

  @Override
  public PredictionResult predictUsingJ48(LoadRequest loadRequest) {
    String dataSetUrl = "dataSets/load_approval.arff";
    String modelUrl = "/models/LoanApprovalJ48.model";
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

      log.info("¿Su prestamo será aprobado? \n" + (predictionIndex == 1.0 ? "Sí" : "No"));
      result.setPrediction(predictionClass);
      result.setProbability(probability * 100);

      String adviceIA;

      if (predictionIndex == 1.0) {
          adviceIA = geminiApplicationImpl.getText(
            "Brinda un consejo empático y motivador a una persona que ha sido aprobada para un prestamo."
                + "El consejo debe ser sencillo y corto no mas de 45 palabras");
        result.setAdvice(adviceIA);
        log.info("Consejo --> " + adviceIA);
      } else{

         adviceIA = geminiApplicationImpl.getText(
            "Brinda un consejo empático y motivador a una persona que le ha negado un prestamo."
                + "El consejo debe ser sencillo y corto no mas de 45 palabras");
        result.setAdvice(adviceIA);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
