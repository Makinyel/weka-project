package com.project.weka.infrastructure.input.api.controller;

import com.project.weka.application.input.port.LoadDataSetApplication;
import com.project.weka.application.input.port.PredictIsDiabetesApplication;
import com.project.weka.application.services.GeminiApplicationImpl;
import com.project.weka.domain.model.Patient;
import com.project.weka.domain.model.PredictionResult;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api-weka")
public class DataSetController {

  @Autowired
  private LoadDataSetApplication loadDataSetApplicationImpl;
  @Autowired
  private PredictIsDiabetesApplication predictIsDiabetesApplicationImpl;
  @Autowired
  private GeminiApplicationImpl geminiApplicationImpl;

  @GetMapping("/load")
  public String loadDataset(@RequestParam String url) {
    return loadDataSetApplicationImpl.loadDataSet(url);
  }

  @PostMapping("/predic")
  public ResponseEntity<PredictionResult> predict(@RequestBody Patient patient) {
    return ResponseEntity.ok(predictIsDiabetesApplicationImpl.predict(patient));
  }

  @PostMapping("/predicJ48")
  public ResponseEntity<PredictionResult> predictUsingJ48(@RequestBody Patient patient) {
    return ResponseEntity.ok(predictIsDiabetesApplicationImpl.predictUsingJ48(patient));
  }

  @PostMapping("/ia")
  public ResponseEntity<String> ia() {
    return ResponseEntity.ok(geminiApplicationImpl.getText("Dime 10 ejemplos de IA"));
  }
}