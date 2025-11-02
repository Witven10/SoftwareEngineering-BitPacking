# SoftwareEngineering-BitPacking

Implantation en Java de la compression d'entiers 32 bits par bit packing, avec trois strategies (sans recouvrement, recouvrement, gestion des depassements) ainsi qu'un banc d'essai pour mesurer l'efficacite et les performances.

## Apercu
- Objectif principal : reduire l'empreinte memoire de tableaux d'entiers en regroupant leurs bits dans des blocs de 32 bits.
- L'interface `BitPacking` definit l'API commune (`compress`, `decompress`, `get`).
- Trois variantes sont fournies via la fabrique `BitPackingFactory` :
  - `BitPackingNoOverlap` : decoupe fixe, aucun chevauchement entre les mots compresses (utile quand `k` divise 32).
  - `BitPackingOverlap` : autorise le chevauchement pour ne perdre aucun bit lorsque `k` ne divise pas 32.
  - `BitPackingOverflow` : selectionne dynamiquement la precision `k'` et stocke dans une zone reservee les valeurs trop grandes.
- `Main` orchestre tests de validite et benchmarks (temps de compression/decompression/acces direct et ratio de compression).

## Organisation du code
- `src/BitPacking.java` : interface commune.
- `src/BitPackingNoOverlap.java`, `src/BitPackingOverlap.java`, `src/BitPackingOverflow.java` : implantations concretes.
- `src/BitPackingFactory.java` : creation de l'instance adequat selon le mode.
- `src/BitUtils.java` : utilitaires de manipulation de bits (`maskK`, `getBits`, `setBits`, `getK`).
- `src/Test.java` : verifie que chaque implementation restitue les valeurs originales et que `get(i)` est coherent.
- `src/BenchmarkRunner.java` : protocole de mesure (warm-up JIT + moyennes).
- `src/Main.java` : point d'entree qui lance validations et benchmarks sur un jeu de donnees exemple.

## Prerequis
- JDK 17 ou version plus recente (projet teste avec une JVM standard, sans dependances externes).

## Compilation et execution
Depuis la racine du depot :

```bash
# Compilation (les classes generees sont placees dans out/)
javac -d out src/*.java

# Lancer le scenario complet (tests + benchmarks)
java -cp out Main
```

Par defaut `Main` :
- calcule `k_max` a partir des donnees d'entree via `BitUtils.getK`,
- cree tour a tour les compresseurs `nooverlap`, `overlap`, `overflow`,
- verifie la bonne decomposition/recomposition (`Test.testCompressionMethod`),
- mesure les temps moyens (compression, decompression, acces direct) et calcule le ratio de compression.

### Modifier les entrees ou repetitions
Editez `src/Main.java` :
- Tableaux d'entree : variable `input_bench`.
- Nombre de repetitions pour les benchmarks : constante `REPETITIONS`.
- Liste des modes testes : tableau `{"nooverlap", "overlap", "overflow"}`.

## Tests rapides
Pour isoler la validation fonctionnelle sans lancer les benchmarks, vous pouvez directement appeler la methode de test :

```java
BitPacking compressor = BitPackingFactory.create("overlap", BitUtils.getK(input));
Test.testCompressionMethod(compressor, input);
```

La methode affiche la taille compressee, verifie la coherence de `decompress` et `get`, puis signale toute divergence.

## Benchmarks
- `BenchmarkRunner` effectue 10 echauffements pour permettre l'optimisation JIT, puis repete la mesure `repetitions` fois.
- Le temps retourne pour `get` correspond au cout moyen par acces (`ns` par element).
- Le scenario `Main` compare egalement la taille du tableau compresse (nombre de mots de 32 bits) a la taille brute d'origine.

## Pistes d'amelioration
- Ajouter une CLI ou des options pour choisir facilement le mode, la valeur de `k` ou charger des donnees depuis un fichier.
- Etendre la suite de tests avec des cas limites (tableaux vides, valeurs max sur 32 bits, motifs repetitifs/extremes).
- Exporter les resultats de benchmark au format CSV/JSON pour faciliter l'analyse et la comparaison sur de grands jeux de donnees.

## Licence
Projet academique (Software Engineering Project 2025). Ajouter une licence formelle si necessaire avant toute diffusion.
