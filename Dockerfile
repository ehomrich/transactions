FROM python:3.7-slim-buster

RUN mkdir /usr/src/app
WORKDIR /usr/src/app

COPY . /usr/src/app
RUN chmod +x /usr/src/app/authorize
ENTRYPOINT [ "/usr/src/app/authorize" ]