package com.project.weka.application.input.port;

import java.io.IOException;

import weka.classifiers.Classifier;

public interface LoadModelApplication {

    Classifier loadModel(String modelUrl) throws Exception;
}
