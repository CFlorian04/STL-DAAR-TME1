# Variables
BIN_DIR = build
MAIN_CLASS = Main                  # Remplacez par le nom complet de votre classe principale si elle est dans un package
JAR_NAME = project.jar
MANIFEST_FILE = MANIFEST.MF

# Compilation
compile:
	@mkdir -p $(BIN_DIR)
	@javac -d $(BIN_DIR) *.java

# Création du fichier MANIFEST.MF
manifest:
	@echo "Manifest-Version: 1.0" > $(MANIFEST_FILE)
	@echo "Main-Class: $(MAIN_CLASS)" >> $(MANIFEST_FILE)

# Création du fichier JAR
jar: compile manifest
	@jar cfm $(BIN_DIR)/$(JAR_NAME) $(MANIFEST_FILE) -C $(BIN_DIR) .

# Exécution du programme
run: jar
	@java -jar $(BIN_DIR)/$(JAR_NAME)

# Nettoyage des fichiers générés
clean:
	@rm -rf $(BIN_DIR) $(MANIFEST_FILE)

.PHONY: compile jar manifest run clean