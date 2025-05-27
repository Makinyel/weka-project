FROM eclipse-temurin:17.0.15_6-jdk

EXPOSE 8080

WORKDIR /root

COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root/mvnw
COPY ./mvnw.cmd /root/mvnw.cmd

# 🔐 Otorgar permisos de ejecución a mvnw
RUN chmod +x /root/mvnw

# 🔄 Descargar dependencias sin construir aún
RUN /root/mvnw dependency:go-offline

# 📁 Copiar el código fuente
COPY ./src /root/src

# ⚙️ Compilar la aplicación sin ejecutar tests
RUN /root/mvnw clean install -DskipTests

# 🚀 Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/root/target/weka-0.0.1-SNAPSHOT.jar"]