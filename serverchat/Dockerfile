# base image
FROM eclipse-temurin:17-jdk

# set working directory
WORKDIR /home

# install git
RUN apt-get update && apt-get install -y git

# copy the source code
RUN git clone https://github.com/VettoriDanteMeucci/serverchat.git

# set new working directory
WORKDIR /home/serverchat

# install maven
RUN apt-get install -y maven

# build the app
RUN mvn clean install
RUN mvn dependency:copy-dependencies
RUN mvn compile

# expose the port 3000 for the service
EXPOSE 3000

# run the server app
ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "com.serverchat.Main"]
