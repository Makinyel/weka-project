package com.project.weka.application.input.port;

import com.project.weka.domain.model.LoadRequest;
import com.project.weka.domain.model.PredictionResult;

public interface PredicLoadApplication {

  double predict(LoadRequest LoanRequest);

  PredictionResult predictUsingJ48(LoadRequest LoanRequest);
}
