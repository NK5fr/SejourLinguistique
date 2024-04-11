package languageStay.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import languageStay.*;
import languageStay.graph.AffectationUtil;

public class AppController {
    Stage stage;
    Scene ajouter, dashboard, ponderation, participants, appariements, modifier, fixerApp, eviterApp, suppression;
    Plateform plateform;
    Preferences prefs;
    static Country country1 = Country.FRANCE;
    static Country country2 = Country.SPAIN;
    final String teenagersPath = "res/teenagersData.csv";
    final String teenList = System.getProperty("user.dir") + File.separator + "res" + File.separator + "teenagersData.csv";
    final String affList = System.getProperty("user.dir") + File.separator + "res" + File.separator + "affectationData.csv";
    final String plateformPath = "plateformIhm";
    @FXML Button homeBtn, retour;

    // Pages d'accueil et ajouter/modifier
    @FXML Label accueilTitle, modifierTitle, desc;
    @FXML Button dashboardButton;
    @FXML Button addTeenagerButton;
    @FXML Button SaveCountry;
    @FXML TextField nameField, forenameField, ddnField, hobbiesField;
    @FXML CheckBox allergyField, hostNuts, hostVege, visitorVege, visitorNuts, hostAnimalField, keepField;
    @FXML ChoiceBox<String> genderField, countryField, prefGenderField, ChoiceHost, ChoiceVisitor;
    
    // Page "Éviter appariement" et "Fixer appariement"
    @FXML Label eviterTitle, fixerTitle;
    @FXML ChoiceBox<String> hostField, visitorField;
    @FXML CheckBox refresh;

    // Page "Liste des appariements"
    @FXML Label appariementsTitle, appInfos;
    @FXML ListView<String> ListeApp;
    @FXML ChoiceBox<String> yearField;
    @FXML Button newAppBtn, eviterAppBtn;

    // Page "Liste des participants"
    @FXML ListView<String> ListeTeen;
     @FXML Label CptSpain, CptFrance, CptGermany, CptItaly;
    @FXML Button EditTeen, DelTeen, ListTeenBack, multSuppr;

    // Page "Suppression multiple"
    @FXML TextField nbSupp;
    @FXML Button suppBtn;

    // Page "Définir pondération"
    @FXML Label ponderationTitle;
    @FXML TextField initialWeight, constraintWeight, preferenceWeight;
    @FXML Button savePonderation, defaultPonderation, ponderationBack;
    
    // Page "Tableau de bord"
    @FXML Button ponderationBtn, participantsBtn, appariementsBtn, newstayBtn;

    public void initialize() throws IOException {
        plateform = Plateform.fromFile(plateformPath);
        plateform.clear();
        Teenager.ResetCompteur();
        plateform.importer(teenagersPath);
        prefs = Preferences.userRoot().node("languageStay");
        //double p1 = prefs.getDouble("initialWeight", AffectationUtil.INITIAL_WEIGHT);
        //int p2 = prefs.getInt("constraintWeight", AffectationUtil.REDHIBITOIRE);
        //int p3 = prefs.getInt("preferenceWeight", AffectationUtil.PREF);

        if (genderField != null){
            genderField.getItems().addAll("Male", "Female", "Other");
            prefGenderField.getItems().addAll("", "Male", "Female", "Other");
            countryField.getItems().addAll("FRANCE", "GERMANY", "SPAIN", "ITALY");
        } else if (ListeTeen != null){
            for (Teenager t: plateform) {
                ListeTeen.getItems().add(t.toViewString());
            }
            updateCount();
        } 
        if (modifierTitle != null) {
            ddnField.setDisable(true);
            Teenager teenager = getCurrentTeenager();
            ddnField.setText(teenager.getBirthday().format(Teenager.DATE_FORMATTER).replace('-', '/'));
            nameField.setText(teenager.getName());
            forenameField.setText(teenager.getFirstname());
            genderField.setValue(teenager.getCriterion(CriterionName.GENDER));
            countryField.setValue(teenager.getCountry().name());
            if (teenager.getCriterion(CriterionName.GUEST_ANIMAL_ALLERGY).equals("yes")) allergyField.setSelected(true);
            if (teenager.getCriterion(CriterionName.GUEST_FOOD).contains("nonuts")) visitorNuts.setSelected(true);
            if (teenager.getCriterion(CriterionName.GUEST_FOOD).contains("vege")) visitorVege.setSelected(true);
            if (teenager.getCriterion(CriterionName.HOST_HAS_ANIMAL).equals("yes")) hostAnimalField.setSelected(true);
            if (teenager.getCriterion(CriterionName.HOST_FOOD).contains("nonuts")) hostNuts.setSelected(true);
            if (teenager.getCriterion(CriterionName.HOST_FOOD).contains("vege")) hostVege.setSelected(true);
            hobbiesField.setText(teenager.getCriterion(CriterionName.HOBBIES));
            if (teenager.getCriterion(CriterionName.PAIR_GENDER) != null) prefGenderField.setValue(teenager.getCriterion(CriterionName.PAIR_GENDER));
            if (teenager.getCriterion(CriterionName.HISTORY).equals("yes")) keepField.setSelected(true);

        } else if (ponderationTitle != null) {
            if (prefs.get("initialWeight", null) != null) {
                plateform.setInitialWeight(Double.parseDouble(prefs.get("initialWeight", null)));
                plateform.setRedhibitoire(Integer.parseInt(prefs.get("constraintWeight", null)));
                plateform.setPref(Integer.parseInt(prefs.get("preferenceWeight", null)));
            }
            fillPonderations(plateform.getInitialWeight(), plateform.getRedhibitoire(), plateform.getPref());
        } else if (appariementsTitle != null) {
            Affectations aff = Affectations.importer("" + country1 + "_" + country2 + ".bin");
            for (String line: aff.getListViewItems()) {
                ListeApp.getItems().add(line);
            }
        } else if (eviterTitle != null || fixerTitle != null) {
            for (Teenager t: plateform) {
                hostField.getItems().add(t.toShortString());
                visitorField.getItems().add(t.toShortString());
            }
        } else if (accueilTitle != null) {
            ChoiceHost.setValue(country1.name());
            ChoiceVisitor.setValue(country2.name());
            ChoiceHost.getItems().addAll("FRANCE", "GERMANY", "SPAIN", "ITALY");
            ChoiceVisitor.getItems().addAll("FRANCE", "GERMANY", "SPAIN", "ITALY");
            desc.setText("L'hôte provient : "+country1.name() + "\nLe visiteur provient : " + country2.name());
        } else if (suppBtn != null) {
            ChoiceHost.getItems().addAll("FRANCE", "GERMANY", "SPAIN", "ITALY");
        }
        if (homeBtn != null) homeBtn.setGraphic(new ImageView(new Image("file:res/app/home.png", 15, 15, true, true)));
        if (retour != null) retour.setGraphic(new ImageView(new Image("file:res/app/back.png", 15, 15, true, true)));
    }

    private void loadScene(String name) {
        /*
         * Charger la scene correspondant au nom passé en paramètre
         * @param name : nom de la scene à charger
         */
        try {
            if (name.equals("Ajouter")) {
                ajouter = new Scene(loadFXML(name));
            } else if (name.equals("Dashboard") || name.equals("Accueil")) {
                dashboard = new Scene(loadFXML(name));
            } else if (name.equals("Preference")) {
                ponderation = new Scene(loadFXML(name));
            } else if (name.equals("ListeAppariements")) {
                appariements = new Scene(loadFXML(name));
            } else if (name.equals("ListeTeenager")) {
                participants = new Scene(loadFXML(name));
            } else if (name.equals("Modifier")) {
                modifier = new Scene(loadFXML(name));
            } else if (name.equals("FixerAppariements")) {
                fixerApp = new Scene(loadFXML(name));
            } else if (name.equals("EviterAppariements")) {
                eviterApp = new Scene(loadFXML(name));
            } else if (name.equals("Supprimer")) {
                suppression = new Scene(loadFXML(name));
            }
        } catch (IOException ie) { ie.printStackTrace();}
    }

    private void setScene(Scene s) {
        /*
         * Changer la scene actuelle
         * @param s : nouvelle scene
         */
        try {
            stage = (Stage) homeBtn.getScene().getWindow();
        } catch (NullPointerException npe) {
            stage = (Stage) accueilTitle.getScene().getWindow();
        }
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.setScene(s);
        stage.show();
    }

    private void alert(AlertType type, String title, String header, String content) {
        /*
         * Afficher une boîte de dialogue
         * @param type : type de la boîte de dialogue
         * @param title : titre de la boîte de dialogue
         * @param header : en-tête de la boîte de dialogue
         * @param content : contenu de la boîte de dialogue
         */
        Alert alert = new Alert(type);
        alert.setResizable(true);
        alert.setHeight(300);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private void updateCount() {
        if (ListeTeen == null) return;
        CptFrance.setText(plateform.getNbCountry(Country.FRANCE) + "");
        CptGermany.setText(plateform.getNbCountry(Country.GERMANY) + "");
        CptItaly.setText(plateform.getNbCountry(Country.ITALY) + "");
        CptSpain.setText(plateform.getNbCountry(Country.SPAIN) + "");
    }

    public static Parent loadFXML(String fxml) throws IOException {
        /*
         * Charger le fichier fxml correspondant au nom passé en paramètre
         * @param fxml : nom du fichier fxml à charger
         */
        FXMLLoader fxmlLoader = new FXMLLoader(AppController.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public void plateformSave(){
        plateform.exporterTeenagers(teenagersPath);
        try {
            plateform.exporter(affList, country1, country2);
        } catch (IllegalArgumentException e) {
            alert(AlertType.WARNING, "Attention", "Problème de nombre", "Vous n'avez pas autant de participants de " + country1 + " que de " + country2 + ", les appariements ne seront mis à jour que lorsque vous aurez ajouté/supprimé un/des participant(s).");
        }
        plateform.toFile(plateformPath);
    }

    public void setCurrentTeenager(Teenager teenager) {
        /*
         * Définir le teenager actuel
         * @param teenager : nouveau teenager actuel
         */
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("res/currentTeenager.txt"))) {
            bw.write(""+plateform.getTeenagerIndex(teenager));
        } catch (IOException ie) { ie.printStackTrace(); }
    }

    public Teenager getCurrentTeenager(){
        /*
         * Récupérer le teenager actuel
         * @return : le teenager actuel
         */
        try (BufferedReader br = new BufferedReader(new FileReader("res/currentTeenager.txt"))) {
            int idx = Integer.parseInt(br.readLine());
            return plateform.getByIndex(idx);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void pressedDashboard(ActionEvent event) {
        /*
         * Déclenché lors de l'appui sur le bouton "Tableau de bord"
         */
        if (stage == null) {
            if (accueilTitle != null) stage = (Stage) accueilTitle.getScene().getWindow();
            else stage = (Stage) homeBtn.getScene().getWindow();
        }
        loadScene("Dashboard");
        setScene(dashboard);
    }

    public void pressedAddTeenager(ActionEvent event) {
        /*
         * Déclenché lors de l'appui sur le bouton "Ajouter un ado"
         */
        if (stage == null) stage = (Stage) addTeenagerButton.getScene().getWindow();
        loadScene("Ajouter");
        setScene(ajouter);
    } 

    public void pressedHome() {
        if (stage == null) stage = (Stage) homeBtn.getScene().getWindow();
        loadScene("Dashboard");
        setScene(dashboard);
    }

    // Page "Dashboard"

    public void pressedPonderation(ActionEvent event) {
        loadScene("Preference");
        setScene(ponderation);
    }

    public void pressedParticipants(ActionEvent event) {
        loadScene("ListeTeenager");
        setScene(participants);
    }

    public void pressedAppariements(ActionEvent event) {
        String filename = "" + country1 + "_" + country2 + ".bin";
        if (Affectations.importer(filename) == null) {
            Affectations a = null;
            try {
                a = new Affectations(plateform.getAretes(country1, country2));
            } catch (IllegalArgumentException iae) {
                alert(AlertType.ERROR, "Erreur lors de l'opération", "", "Une erreur est survenue lors de la génération des appariements, vérifiez qu'il y a autant de participants dans chaque pays (" + country1 + " et " + country2 + ") dans la page \"Participants\"");
                return;
            }
            Affectations.exporter(a, filename);
        }
        loadScene("ListeAppariements");
        setScene(appariements);}

    // Page "Liste des participants"

    public void editTeenager(ActionEvent event) {
        if (ListeTeen.getSelectionModel().getSelectedItem() == null) {
            alert(AlertType.ERROR, "Erreur lors de l'opération", "Veuillez s\u00E9lectionner un participant pour le visualiser ou le modifier", "");
            return;
        }
        String id = ListeTeen.getSelectionModel().getSelectedItem().toString().split("\t")[0];
        Teenager teenager = plateform.getById(Integer.valueOf(id));
        setCurrentTeenager(teenager);
        loadScene("Modifier");
        setScene(modifier);
    }

    public void delTeenager(ActionEvent event) {
        if (ListeTeen.getSelectionModel().getSelectedItem() == null) {
            alert(AlertType.ERROR, "Erreur lors de l'opération", "Veuillez s\u00E9lectionner un participant pour le supprimer", "");
            return;
        }
        String id = ListeTeen.getSelectionModel().getSelectedItem().toString().split("\t")[0];
        Teenager teenager = plateform.getById(Integer.valueOf(id));
        if (teenager != null) {
            ListeTeen.getItems().remove(ListeTeen.getSelectionModel().getSelectedItem());
            plateform.supprimer(teenager);
            plateformSave();
            updateCount();
        }
    }

    public void pressedMultSuppr(ActionEvent event) {
        System.out.println("pressedMultSuppr");
        if (plateform.size() == 0) {
            alert(AlertType.ERROR, "Erreur lors de l'opération", "Il n'y a aucun participant à supprimer", "");
            return;
        }
        loadScene("Supprimer");
        setScene(suppression);
    }

    // Page "Définir les pondérations"

    public void pressedSavePref(ActionEvent event) {
        if (checkIntField(initialWeight) && checkIntField(constraintWeight) && checkIntField(preferenceWeight)) {
            plateform.setInitialWeight(Double.parseDouble(initialWeight.getText()));
            plateform.setRedhibitoire(Integer.parseInt(constraintWeight.getText()));
            plateform.setPref(Integer.parseInt(preferenceWeight.getText()));
            System.out.println(AffectationUtil.initialWeight + " " +AffectationUtil.pref + " " + AffectationUtil.redhibitoire);
            plateform.toFile(plateformPath);
            prefs.put("initialWeight", initialWeight.getText());
            prefs.put("constraintWeight", constraintWeight.getText());
            prefs.put("preferenceWeight", preferenceWeight.getText());
            alert(AlertType.INFORMATION, "Opération réussie", "Les pondérations ont été enregistrées", "Si vous souhaitez qu'elles soient prises en compte, n'oubliez pas de regénérer les appariements (onglet \"Tableau de bord\")");
            System.out.println(AffectationUtil.initialWeight + " " +AffectationUtil.pref + " " + AffectationUtil.redhibitoire);
        }
    }

    public void pressedDefPref(ActionEvent event) {
        fillPonderations();
        plateform.toFile(plateformPath);
    }

    private void fillPonderations(double initialWeight, int constraintWeight, int preferenceWeight) {
        this.initialWeight.setText(""+initialWeight);
        this.constraintWeight.setText(""+constraintWeight);
        this.preferenceWeight.setText(""+preferenceWeight);
    }

    private void fillPonderations() {
        fillPonderations(AffectationUtil.INITIAL_WEIGHT, AffectationUtil.REDHIBITOIRE, AffectationUtil.PREF);
    }

    // Page "Liste des appariements"

    public void pressedNewstay(ActionEvent event) {
        try {
            if (plateform.nouveauSejour(country1, country2)) {
                plateformSave();
                alert(AlertType.INFORMATION, "Opération réussie", "", "Les appariements ont été générés pour le séjour " + country1 + " -> " + country2);
            } else { 
                alert(AlertType.ERROR, "Erreur lors de l'opération", "", "Une erreur est survenue lors de la génération des appariements, vérifiez qu'il y a autant de participants dans chaque pays (" + country1 + " et " + country2 + ") dans la page \"Participants\"");
            }            
        } catch (Exception e) {
                alert(AlertType.ERROR, "Erreur lors de l'opération", "", "Une erreur est survenue lors de la génération des appariements, vérifiez qu'il y a autant de participants dans chaque pays (" + country1 + " et " + country2 + ") dans la page \"Participants\"");
        }

    }

    public void pressedNewApp(ActionEvent event) {
        loadScene("FixerAppariements");
        setScene(fixerApp);
    }

    public void pressedEviterApp(ActionEvent event) {
        loadScene("EviterAppariements");
        setScene(eviterApp);
    }

    public void pressedRefresh(ActionEvent event) {
        pressedNewstay(event);
        loadScene("ListeAppariements");
        setScene(appariements);
    }

    public void appListClicked(MouseEvent event) {
        if (ListeApp.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String[] s = ListeApp.getSelectionModel().getSelectedItem().toString().split("->");
        Teenager host = plateform.getById(getIdFromString(s[0]));
        Teenager guest = plateform.getById(getIdFromString(s[1]));
        StringBuilder infos = new StringBuilder();
        infos.append("Hôte   : " + host.getFirstname() + " " + host.getName() + " | " + host.getBirthday() + " " + host.getCountry() + "\n");
        infos.append("Invité : " + guest.getFirstname() + " " + guest.getName() + " | " + guest.getBirthday() + " " + guest.getCountry() + "\n");
        infos.append("Contrainte rédhibitoire : " + host.getRedhibitoire(guest));
        appInfos.setText(infos.toString());
    }

    // Page "Fixer/éviter des appariements"

    /*
    * Récupérer l'id d'un teenager à partir de sa représentation sous forme de chaîne de caractères
    * @param s : chaîne de caractères représentant un teenager
    * @return : l'id du teenager
    */
    private int getIdFromString(String s) {
        String id = "";
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c >= '0' && c <= '9') id += c;
            else if (c == ')') return Integer.parseInt(id);
        }
        return Integer.parseInt(id);
    }

    public void pressedEviterSave(ActionEvent event) {
        if (checkChoice(hostField) && checkChoice(visitorField)) {
            Teenager host = plateform.getById(getIdFromString(hostField.getValue().toString()));
            Teenager guest = plateform.getById(getIdFromString(visitorField.getValue().toString()));
            host.putFixerEviter(guest, 0);
            guest.putFixerEviter(host, 0);

            if (refresh.isSelected()) plateform.nouveauSejour(country1, country2);
            plateformSave();
            alert(AlertType.INFORMATION, "Opération réussie", "", "L'appariement sera désormais évité");            
        }

    }

    public void pressedFixerSave(ActionEvent event) {
        if (checkChoice(hostField) && checkChoice(visitorField)) {
            Teenager host = plateform.getById(getIdFromString(hostField.getValue().toString()));
            Teenager guest = plateform.getById(getIdFromString(visitorField.getValue().toString()));
            host.putFixerEviter(guest, 1);
            guest.putFixerEviter(host, 1);

            plateform.nouveauSejour(country1, country2);
            plateformSave();
            alert(AlertType.INFORMATION, "Opération réussie", "", "L'appariement a été fixé");
        }
    }

    // Page "Suppression multiple"

    public void pressedSupp(ActionEvent event) {
        System.out.println("pressedSupp");
        if (checkIntField(nbSupp)) {
            System.out.println("" + country1 + " " + country2);
            if (ChoiceHost.getSelectionModel().getSelectedItem() != null) 
                plateform.supprimer(Integer.parseInt(nbSupp.getText()), Country.valueOf(ChoiceHost.getSelectionModel().getSelectedItem().toString()));
            else plateform.supprimer(Integer.parseInt(nbSupp.getText()));
            plateformSave();
            alert(AlertType.INFORMATION, "Opération réussie", "", "Suppression de " + nbSupp.getText() + " participants effectuée");
        }
    }

    private String yesOrNo(boolean b) {
        /*
         * Retourne "yes" si b est vrai, "no" sinon
         * @param b : booléen à tester
         */
        if (b) return "yes";
        return "no";
    }

    private boolean unvalidField(Node field) {
        /*
         * Met en rouge le champ passé en paramètre et retourne false
         * @param field : champ à mettre en rouge
         */
        field.setStyle("-fx-border-color: red");
        return false;
    }

    private boolean validField(Node field) {
        /*
         * Met en vert le champ passé en paramètre et retourne true
         * @param field : champ à mettre en vert
         */
        field.setStyle("-fx-border-color: inherit");
        return true;
    }

    private boolean checkField(TextField field) {
        /*
         * Vérifie que le champ passé en paramètre n'est pas vide
         * @param field : champ à vérifier
         */
        if (field.getText().isEmpty()) {
            return unvalidField(field);
        }
        return validField(field);
    }

    /*
     * Vérifie que le champ passé en paramètre n'est pas vide et qu'il contient un double
     * @param field : champ à vérifier
     * @return : true si le champ est valide, false sinon
     */
    private boolean checkIntField(TextField field) {
        if (field.getText().isEmpty()) {
            return unvalidField(field);
        }
        try {
            Double.parseDouble(field.getText());
        } catch (Exception e) {
            return unvalidField(field);
        }
        return validField(field);
    }

    private boolean checkDateField(TextField field) {
        /*
         * Vérifie que le champ passé en paramètre n'est pas vide et qu'il contient une date valide
         * @param field : champ à vérifier
         */
        if (field.getText().isEmpty()) {
            return unvalidField(field);
        }
        String[] date = field.getText().split("/");
        if (date.length != 3) {
            return unvalidField(field);
        }
        try {
            LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
        } catch (Exception e) {
            return unvalidField(field);
        }
        return validField(field);
    }

    private boolean checkChoice(ChoiceBox<String> box) {
        /*
         * Vérifie que le champ passé en paramètre n'est pas vide
         * @param box : champ à vérifier
         */
        if (box.getValue() == null || box.getValue().toString().isEmpty()) {
            return unvalidField(box);
        }
        return validField(box);
    }

    public void saveTeenager(ActionEvent event) {
        /*
         * Déclenché lors de l'appui sur le bouton "Enregistrer" dans la scène "Ajouter"
         */
        Teenager teen;
        if (checkField(nameField) && checkField(forenameField) && checkDateField(ddnField) && checkChoice(genderField) && checkChoice(countryField)) {
            String[] date = ddnField.getText().split("/");
            String food = "";
            if (modifierTitle == null) teen = new Teenager(nameField.getText(), forenameField.getText(), LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0])), Country.valueOf(countryField.getValue().toString()));
            else teen = getCurrentTeenager();
            teen.addCriterion(CriterionName.GUEST_ANIMAL_ALLERGY, yesOrNo(allergyField.isSelected()));
            teen.addCriterion(CriterionName.GENDER, genderField.getValue().toString().toLowerCase());
            if (visitorNuts.isSelected()) food += "nonuts,";
            if (visitorVege.isSelected()) food += "vege,";
            if (food.length() > 0) teen.addCriterion(CriterionName.GUEST_FOOD, food.substring(0, food.length()-1));
            else teen.addCriterion(CriterionName.GUEST_FOOD, "");
            teen.addCriterion(CriterionName.HOST_HAS_ANIMAL, yesOrNo(hostAnimalField.isSelected()));
            food = "";
            if (hostNuts.isSelected()) food += "nonuts,";
            if (hostVege.isSelected()) food += "vege,";
            if (food.length() > 0) teen.addCriterion(CriterionName.HOST_FOOD, food.substring(0, food.length()-1));
            else teen.addCriterion(CriterionName.HOST_FOOD, "");
            teen.addCriterion(CriterionName.HISTORY, yesOrNo(keepField.isSelected()));
            teen.addCriterion(CriterionName.HOBBIES, hobbiesField.getText().replace(" ", ""));
            if (prefGenderField.getValue() != null) teen.addCriterion(CriterionName.PAIR_GENDER, prefGenderField.getValue().toString().toLowerCase());
            teen.purgeInvalidRequierement();
            if (modifierTitle == null) plateform.ajouter(teen);
            else setCurrentTeenager(teen);
            plateformSave();

            alert(AlertType.INFORMATION, "Enregistrement effectué !", "Participant enregistré (ID : " + teen.getID() + ")", "Vous pouvez maintenant ajouter un autre participant ou retourner au dashboard.");
        }
    }

     public void saveCountry(ActionEvent event) {
        /*
         * Déclenché lors de l'appui sur le bouton "Enregistrer" dans la scène "Accueil"
         */
        if (ChoiceHost.getValue()==null || ChoiceVisitor.getValue()==null || Country.valueOf(ChoiceHost.getValue().toString()) == Country.valueOf(ChoiceVisitor.getValue().toString())) {
            alert(AlertType.ERROR, "Erreur", "Les pays doivent séléctionnés et différents", "Veuillez choisir deux pays différents");
            return;
        } else {
            country1 = Country.valueOf(ChoiceHost.getValue().toString());
            country2 = Country.valueOf(ChoiceVisitor.getValue().toString());
            desc.setText("L'hôte provient : "+country1.name() + "\nLe visiteur provient : " + country2.name());
            alert(AlertType.INFORMATION, "Enregistrement effectué !", "Pays enregistrés", "");
        }
    }
}

