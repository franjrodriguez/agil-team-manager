FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libgl1-mesa-glx \
    libglu1-mesa \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/Agilteammanager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "Xvfb :99 -screen 0 1280x800x24 & sleep 2 && DISPLAY=:99 java -Djava.awt.headless=false -Dprism.order=sw -jar app.jar"]