# SAÉ 2.01 & 2.02 - Organisation de séjours linguistiques

**Auteurs : *Rayane BELGUEBLI*, *Nathan DESMEE*, *Anas OUHDDA***

Générer la documentation : 

```bash
$ javadoc -d ./doc -cp ./lib/sae2_02.jar --source-path ./src languageStay languageStay.exceptions languageStay.graph
```

Démarrer l'application en ligne de commande :

```bash
$ java -jar languageStayCLI.jar
```

Démarrer l'application en interface graphique :

```bash
$ java --module-path=lib/javafx-sdk-17.0.2/lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics -jar languageStayGUI.jar
```

Liens utiles :
- Sujet de la SAÉ [*PDF*](res/pdf/sujetSAE2.01-2.02-2023.pdf)
- Rapport de POO [*PDF*](RapportPOO.pdf), [*Markdown*](RapportPOO.md)
- Rapport d'IHM [*PDF*](RapportIHM.pdf), [*Markdown*](RapportIHM.md)
- Rapport de graphes [*PDF*](graphes/RapportGraphe.pdf), [*Markdown*](graphes/RapportGraphe.md)
- Mockup V1 de l'interface graphique [*PDF*](mockups/Maquette.pdf)
- Mockup V2 de l'interface graphique [*PDF*](mockups/MaquetteV2.pdf)
