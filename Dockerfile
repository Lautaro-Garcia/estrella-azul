# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Stage 1: dependencias
# ---------------------------------------------------------------------------
FROM clojure:temurin-21-tools-deps-bookworm-slim AS deps

WORKDIR /app

COPY deps.edn ./
RUN clojure -P

# ---------------------------------------------------------------------------
# Stage 2: runtime
# ---------------------------------------------------------------------------
FROM clojure:temurin-21-tools-deps-bookworm-slim

WORKDIR /app

COPY --from=deps /root/.m2 /root/.m2

COPY deps.edn ./
COPY src ./src
COPY slides ./slides

EXPOSE 3100

CMD ["clojure", "-X:run"]
