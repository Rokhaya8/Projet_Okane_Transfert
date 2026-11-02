# Étape 1 : Build de l'application avec Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Déploiement dans Tomcat
FROM tomcat:10.1-jdk17-openjdk-slim
WORKDIR /usr/local/tomcat/webapps/

# Supprimer les applications par défaut de Tomcat pour éviter les conflits
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le fichier .war généré et le renommer ROOT.war pour qu'il soit à la racine (/)
COPY --from=build /app/target/*.war ./ROOT.war

# Exposer le port par défaut de Tomcat
EXPOSE 8080

# Lancer Tomcat
CMD ["catalina.sh", "run"]