FROM openjdk:8-stretch

RUN \
  apt-get update && \
  apt-get install -y binutils clang gcc build-essential

RUN \
  curl -Ls https://git.io/sbt > /usr/local/bin/sbt && \
  chmod 0755 /usr/local/bin/sbt

WORKDIR /app
VOLUME /app/jars

RUN sbt -v -212 -sbt-create -sbt-version 1.2.8 about

COPY . .

RUN sbt -batch '+test'
RUN sbt -batch '+package'

RUN cp /app/target/scala-*/*.jar /app/jars/
