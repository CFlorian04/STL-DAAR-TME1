# STL-DAAR-TME1

[Github du projet](https://github.com/CFlorian04/STL-DAAR-TME1)

Pour lancer le programme, il faut prendre le fichier projet.jar qui se trouve à la racine du projet et l'executer avec soit le commande:
```bash
java -jar projet.jar
```
soit

```bash
java -jar projet.jar "pattern" textPath methode
```
Où le pattern correspond au motif à rechercher, le textPath au chemin pour accéder au fichier texte. La méthode est soit Automate soit KMP. 

Si cela ne fonctionne pas, le programme peut être lancé avec Ant Apache : 
```bash
ant -buildfile build.xml
```

