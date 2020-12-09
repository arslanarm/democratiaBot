FROM gradle:latest
WORKDIR ~/democratiaBot
RUN gradle build
COPY . .
CMD ["gradle", "run"]