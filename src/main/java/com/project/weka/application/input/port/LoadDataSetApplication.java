package com.project.weka.application.input.port;

import weka.core.Instances;

public interface LoadDataSetApplication {

  String loadDataSet(String urlDataSet);

  Instances getInstanceDataForm(String urlDataSet);

}
