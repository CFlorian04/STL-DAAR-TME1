# STL-DAAR-TME1
Pour lancer le programme, il faut prendre le fichier `project.jar` qui se trouve dans le dossier `./src/build/project.jar` et l'exécuter avec soit la commande suivante :

```bash
java -jar project.jar
```

ou bien :

```bash
java -jar project.jar "pattern" textPath methode
```

où :
- `pattern` correspond au motif à rechercher,
- `textPath` au chemin d'accès au fichier texte,
- `methode` peut être soit `Automate`, soit `KMP`.

Les paramètres ne sont pas obligatoires et peuvent être de 1, 2, ou 3 au total.
