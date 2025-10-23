# Imagen base de Java
FROM openjdk:21-jdk-slim

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR al contenedor
COPY ms-authentication-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto (debe coincidir con el configurado en application.properties)
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]