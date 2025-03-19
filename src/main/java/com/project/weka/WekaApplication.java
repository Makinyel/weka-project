package com.project.weka;

import com.project.weka.domain.model.Patient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WekaApplication {

  public static void main(String[] args) {
    SpringApplication.run(WekaApplication.class, args);

    Patient p = new Patient();
    p.setAge(10);
    System.out.println("Age" + p.getAge());

  }

}
