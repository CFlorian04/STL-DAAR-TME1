# Variables
BIN_DIR = build
MAIN_CLASS = Main
JAR_NAME = project.jar

# Compilation
compile:
	@mkdir -p $(BIN_DIR)
	@javac -d $(BIN_DIR) *.java

# Création du fichier JAR
jar: compile
	@jar cf $(BIN_DIR)/$(JAR_NAME) -C $(BIN_DIR) .

# Exécution du programme
run: jar
	@java -cp $(BIN_DIR)/$(JAR_NAME) $(MAIN_CLASS)

# Nettoyage des fichiers générés
clean:
	@rm -rf $(BIN_DIR)

.PHONY: compile jar clean run
