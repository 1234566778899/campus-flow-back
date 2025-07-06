FROM eclipse-temurin:21-alpine

# Instala dependencias necesarias para ejecutar Maven
RUN apk add --no-cache bash curl

WORKDIR /app

# Copia todo el proyecto al contenedor
COPY . .

# Da permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Compila el proyecto (sin ejecutar tests)
RUN ./mvnw clean package -DskipTests

# Expone el puerto que usará la app
EXPOSE 8080

# Ejecuta el .jar generado
ENTRYPOINT ["java", "-jar", "target/CampusFlow-0.0.1-SNAPSHOT.jar"]
