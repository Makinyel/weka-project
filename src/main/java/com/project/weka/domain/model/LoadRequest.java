package com.project.weka.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadRequest {

    private int noOfDependents;
    private String education;
    private String selfEmployed;
    private double incomeAnnum;
    private double loanAmount;
    private int loanTerm;
    private int cibilScore;
    private double residentialAssetsValue;
    private double commercialAssetsValue;
    private double luxuryAssetsValue;
    private double bankAssetValue;

}
