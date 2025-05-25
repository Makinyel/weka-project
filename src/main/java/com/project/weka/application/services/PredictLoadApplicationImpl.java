package com.project.weka.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.weka.application.input.port.PredicLoadApplication;
import com.project.weka.domain.model.LoadRequest;
import com.project.weka.domain.model.PredictionResult;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

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
  public double predict(LoadRequest loanRequest) {
    String dataSetUrl = "dataSets/load_approval.arff";
    String modelUrl = "/models/loanAmountLinearRegression.model";

    try {
        // Cargar dataset base para obtener estructura y definir clase
        Instances data = loadDataSetApplicationImpl.getInstanceDataForm(dataSetUrl);
        data.setClassIndex(5);

        // Configurar filtro NominalToBinary igual que en entrenamiento
        NominalToBinary nominalToBinary = new NominalToBinary();
        nominalToBinary.setInputFormat(data);

        // Filtrar dataset para obtener la estructura transformada
        Instances filteredData = Filter.useFilter(data, nominalToBinary);

        // Crear instancia nueva con la estructura filtrada
        Instance newInstance = new DenseInstance(filteredData.numAttributes());
        newInstance.setDataset(filteredData);

        // Setear valores en newInstance (los índices corresponden a atributos antes de filtro)
        // IMPORTANTE: Los atributos nominales deben estar con los valores originales, el filtro se aplica después
        newInstance.setValue(filteredData.attribute("_no_of_dependents"), loanRequest.getNoOfDependents());
        newInstance.setValue(filteredData.attribute("_education"), loanRequest.getEducation());
        newInstance.setValue(filteredData.attribute("_self_employed"), loanRequest.getSelfEmployed());
        newInstance.setValue(filteredData.attribute("_income_annum"), loanRequest.getIncomeAnnum());
        newInstance.setMissing(filteredData.attribute("_loan_amount")); // atributo objetivo
        newInstance.setValue(filteredData.attribute("_loan_term"), loanRequest.getLoanTerm());
        newInstance.setValue(filteredData.attribute("_cibil_score"), loanRequest.getCibilScore());
        newInstance.setValue(filteredData.attribute("_residential_assets_value"), loanRequest.getResidentialAssetsValue());
        newInstance.setValue(filteredData.attribute("_commercial_assets_value"), loanRequest.getCommercialAssetsValue());
        newInstance.setValue(filteredData.attribute("_luxury_assets_value"), loanRequest.getLuxuryAssetsValue());
        newInstance.setValue(filteredData.attribute("_bank_asset_value"), loanRequest.getBankAssetValue());

        // Aplicar el filtro NominalToBinary a la instancia antes de predecir
        nominalToBinary.input(newInstance);
        Instance filteredInstance = nominalToBinary.output();

        // Cargar modelo
        Classifier model = loadModelApplicationImpl.loadModel(modelUrl);

        if (!(model instanceof LinearRegression)) {
            throw new IllegalStateException("El modelo cargado no es de tipo LinearRegression");
        }

        // Predecir con la instancia filtrada
        return model.classifyInstance(filteredInstance);

    } catch (Exception e) {
        log.error("Error durante la predicción de préstamo", e);
        return Double.NaN;
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
