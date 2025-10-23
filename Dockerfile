# Etapa 1: Compilación del proyecto con Gradle
FROM gradle:8.10.2-jdk17 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos primero los archivos de configuración de Gradle para aprovechar la caché
COPY build.gradle settings.gradle ./
# Si tu proyecto usa el wrapper, cópialo también
COPY gradlew ./
COPY gradle ./gradle

# Descargamos dependencias para aprovechar el cache de Docker
RUN gradle dependencies --no-daemon || return 0

# Ahora copiamos el código fuente
COPY src ./src

# Compilamos el proyecto (sin ejecutar tests)
RUN gradle clean build -x test --no-daemon

# Etapa 2: Imagen final con solo el JRE (más ligera)
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiamos el JAR compilado desde la etapa de build
# Si usas Gradle, por defecto se genera en /app/build/libs/
COPY --from=build /app/build/libs/*.jar app.jar

# Exponemos el puerto estándar de Spring Boot
EXPOSE 8080

# Variable opcional para argumentos JVM
ENV JAVA_OPTS=""

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
