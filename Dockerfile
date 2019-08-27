FROM clojure

RUN mkdir -p /app /app/src

WORKDIR /app/src
COPY . .
RUN lein uberjar

WORKDIR /app
RUN cp src/target/uberjar/*-standalone.jar transactions.jar

ENTRYPOINT [ "java", "-jar", "transactions.jar" ]