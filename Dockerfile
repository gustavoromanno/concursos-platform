# Build em duas etapas: a primeira compila, a segunda só roda.
# Isso mantém a imagem final pequena — o Maven e o código-fonte não vão junto.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom primeiro: enquanto as dependências não mudam, o Docker
# reaproveita esta camada em cache e o build fica bem mais rápido.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---

FROM eclipse-temurin:21-jre
WORKDIR /app

# Rodar como usuário sem privilégios: se a aplicação for comprometida,
# o atacante não cai como root dentro do contêiner.
RUN useradd -r -u 1001 -m appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# A plataforma de hospedagem define a porta pela variável PORT.
EXPOSE 8080
# Ajustes para a maquina pequena do plano gratuito (fracao de CPU, 512 MB):
#   SerialGC          coletor de lixo de uma thread so, o mais leve para pouca CPU e memoria;
#   TieredStopAtLevel so o compilador JIT rapido (C1): sobe bem mais depressa, ao custo de um
#                     pouco de desempenho maximo, que este site nao chega a usar.
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -jar app.jar"]
