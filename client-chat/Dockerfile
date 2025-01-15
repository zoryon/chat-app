# base image
FROM eclipse-temurin:23-jdk

# set working directory
WORKDIR /home

# install git
RUN apt-get update && apt-get install -y git

# copy the source code
RUN git clone https://github.com/zoryon/client-chat.git

# set new working directory
WORKDIR /home/client-chat

# install maven
RUN apt-get install -y maven

# build the app
RUN mvn clean install
RUN mvn dependency:copy-dependencies
RUN mvn compile

# install packages & set display for the GUI app
RUN apt-get install -y xvfb libxi6 libx11-6 libxtst6 libxrender1 libxt6 fonts-dejavu-core x11-xkb-utils
ENV DISPLAY=:99

# run the GUI app
ENTRYPOINT ["sh", "-c", "Xvfb :99 -screen 0 1024x768x24 & java -cp target/classes:target/dependency/* com.client.chat.swing.Main"]
