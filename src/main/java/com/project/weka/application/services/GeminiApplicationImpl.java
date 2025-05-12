package com.project.weka.application.services;

import com.project.weka.application.input.port.GeminiApplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class GeminiApplicationImpl implements GeminiApplication {

  @Value("${gemini.api.key}")
  private String apiKey;
  @Value("${gemini.api.url}")
  private String apiUrl;

  @Override
  public String getText(String promt) {
    try {
          log.info("API---> "+apiKey);
     log.info("API---> "+apiUrl);
      URL url = new URL(apiUrl + apiKey);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      String prompt = String.format(promt
            /*
            "Paciente con Glucosa: %.1f mg/dL, Presión Arterial: %.1f mmHg, IMC: %.1f kg/m², Edad: %d años. ¿Cuáles son los 3 principales consejos de salud?",
            patient.getPlas(), patient.getPres(), patient.getMass(), patient.getAge()*/
      );

      String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";
      conn.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();

      JSONObject json = new JSONObject(response.toString());
      JSONArray candidates = json.optJSONArray("candidates");

      if (candidates != null && !candidates.isEmpty()) {
        JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
        JSONArray parts = content != null ? content.optJSONArray("parts") : null;

        if (parts != null && !parts.isEmpty()) {
          return parts.getJSONObject(0).optString("text", "No se pudo obtener un consejo.")
              .replace("**", "").replace("\n", " ");
        }
      }
      return "No se pudo obtener un consejo.";
    } catch (Exception e) {
      System.out.println(("❌ Error obteniendo consejo de IA: " + e.getMessage()));
      return "No se pudo obtener un consejo en este momento.";
    }
  }
}
