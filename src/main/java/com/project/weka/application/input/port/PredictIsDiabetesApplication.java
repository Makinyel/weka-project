package com.project.weka.application.input.port;

import com.project.weka.domain.model.Patient;
import com.project.weka.domain.model.PredictionResult;

public interface PredictIsDiabetesApplication {

  PredictionResult predict(Patient patient);

  PredictionResult predictUsingJ48(Patient patient);
}
