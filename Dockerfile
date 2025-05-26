FROM openjdk:23-jdk-slim

# Установка зависимостей и Tesseract
RUN apt-get update && apt-get install -y \
    ca-certificates \
    tesseract-ocr \
    wget \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/share/tesseract-ocr/4.00/tessdata \
    && mkdir -p /usr/local/share/ca-certificates/gosuslugi

# Скачивание моделей Tesseract
ADD https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata /usr/share/tesseract-ocr/4.00/tessdata/eng.traineddata
ADD https://github.com/tesseract-ocr/tessdata_best/raw/main/rus.traineddata /usr/share/tesseract-ocr/4.00/tessdata/rus.traineddata

# Скачивание сертификатов Минцифры
RUN wget -O /usr/local/share/ca-certificates/gosuslugi/mincifry-root.crt https://gu-st.ru/content/lending/russian_trusted_root_ca_pem.crt && \
    wget -O /usr/local/share/ca-certificates/gosuslugi/mincifry-sub.crt https://gu-st.ru/content/lending/russian_trusted_sub_ca_pem.crt && \
    update-ca-certificates && \
    for cert in /usr/local/share/ca-certificates/gosuslugi/*.crt; do \
      keytool -importcert -trustcacerts -noprompt \
      -keystore "${JAVA_HOME}/lib/security/cacerts" \
      -storepass changeit \
      -file "$cert" \
      -alias "$(basename "$cert" .crt)"; \
    done

# Копирование JAR
ARG JAR_FILE=target/app.jar
COPY ${JAR_FILE} app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]