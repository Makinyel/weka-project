package com.project.weka.domain.model;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResult {

  private String prediction;
  private double probability;
  private String advice;
  private File file;
}