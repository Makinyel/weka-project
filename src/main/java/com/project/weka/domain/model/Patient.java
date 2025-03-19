package com.project.weka.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

  private double preg;
  private double plas;
  private double pres;
  private double skin;
  private double insu;
  private double mass;
  private double pedi;
  private double age;

}
