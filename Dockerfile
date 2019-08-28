FROM clojure

RUN mkdir /app
WORKDIR /app
COPY . .
RUN lein uberjar
ENTRYPOINT [ "java", "-jar", "target/uberjar/transactions-0.1.0-SNAPSHOT-standalone.jar" ]