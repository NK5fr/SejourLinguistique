package languageStay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import languageStay.graph.AffectationUtil;

public class InterfaceCommande {
    
    private static Scanner scan = new Scanner(System.in);
    public static String teenList = System.getProperty("user.dir") + File.separator + "res" + File.separator + "teenagersData.csv";
    public static String affList = System.getProperty("user.dir") + File.separator + "res" + File.separator + "affectationData.csv";
    private static Country host = Country.FRANCE;
    private static Country guest = Country.SPAIN;
    private static Plateform plateform = new Plateform();

    /**
     * Permet à l'utilisateur d'entrer un caractère et le retourne
     * @return caractère choisit
     */
    private static char ecouterChar(){
        System.out.print("Veuillez entrer 1 caractère : ");
        String c = scan.next();
        while(c.length() != 1){
            System.out.println("invalide");
            System.out.print("Veuillez entrer 1 caractère : ");
            c = scan.next();
        }
        return c.charAt(0);
    }

    /**
     * Permet à l'utilisateur d'enregistrer un nouvel étudiants
     */
    private static void ajouterEtu(){
        clear();
        List<String> liste = new ArrayList<>();
        System.out.println("Ajouter étudiant :\n");
        String[] header = Plateform.CSVImportHeader.split(";");
        System.out.println("L'âge sécrit sous forme : année-mois-jour");
        System.out.println("Les loisirs sous forme : loisir1,loisir2,loisir3\n");
        for(int i = 0; i < header.length; i++){
            System.out.print("Veuillez la valeur qui coresspond au critère " + header[i] + "  (null si pas de valeur) : ");
            String s = scan.next();
            if(s.equals("null")){
                s = "";
            }
            liste.add(s);
        }
        String newEtu = "";
        for(String s : liste){
            newEtu += s + ";";
        }
        newEtu = newEtu.substring(0, newEtu.length()-1);
        File file = new File(teenList);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
            bw.write(newEtu);
            bw.newLine();
        }catch(Exception e){
            System.out.println("Erreur dans l'écriture du fichier");
        }
    }

    /**
     * permet de faire une affectation manuelle c'est à dire fixer ou éviter une affectation
     * @param affectations liste des affectations actuelles
     * @param n 0 si c'est pour éviter et 1 si c'est pour fixer
     */
    public static void appManuel(Affectations affectations, int n){
        clear();
        String mot = "";
        if(n == 0){
            mot = "eviter";
        }else if(n == 1){
            mot = "fixer";
        }
        Set<Teenager> hotes = affectations.getMap().keySet();
        Collection<Teenager> visiteurs = affectations.getMap().values();
        System.out.println(mot + " un appariement :");
        System.out.println("Voici la liste des hotes :");
        for(Teenager t : hotes){
            System.out.println(t);
        }
        System.out.println("Voici la liste des invités :");
        for(Teenager t : visiteurs){
            System.out.println(t);
        }
        int idHost = -1;
        int idGuest = -1;
        while(!containsId(hotes, idHost)){
            System.out.print("Entrez le numéro id de l'hote à " + mot + " : ");
            try{
                idHost = scan.nextInt();
            }catch(Exception e){

            }
        }
        while(!containsId(visiteurs, idGuest)){
            System.out.print("Entrez le numéro id du visiteur à " + mot + " : ");
            try{
                idGuest = scan.nextInt();
            }catch(Exception e){

            }
        }
        affectations.clear();
        plateform.getById(idHost).putFixerEviter(plateform.getById(idGuest), n);
        affectations.add(AffectationUtil.affectation(plateform.getPromo(), affectations.getGuest(), affectations.getHost()));
    }

    /**
     * permet de vérifier si une liste de teenager contient l'ID souhaité
     * @param liste liste de teenager
     * @param id id qu'on veut vérifier
     * @return true si présent et false sinon
     */
    public static boolean containsId(Collection<Teenager> liste, int id){
        for(Teenager t : liste){
            if(t.getID() == id){
                return true;
            }
        }
        return false;
    }

    /**
     * permet de choisir un étudiant déjà existant à modifier et de l'enregistrer dans un csv
     */
    public static void modifEtu(){
        clear();
        System.out.println("Liste des étudiants :");
        for(Teenager t : plateform){
            System.out.println(t);
        }
        int idEtu = -1;
        while(!containsId(plateform.getPromo(), idEtu)){
            System.out.print("Entrez le numéro id de l'étudiant à mofifier : ");
            try{
                idEtu = scan.nextInt();
            }catch(Exception e){

            }
        }
        Teenager teen = plateform.getById(idEtu);
        suppEtuCSV(teen);
        etuModif(teen);
        File file = new File(teenList);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
            bw.write(teen.chaineCSV());
            bw.newLine();
        }catch(Exception e){
            System.out.println("Erreur dans l'écriture du fichier");
        }
    }

    /**
     * modofie les données d'un teenager
     * @param teen teenager à modifier
     */
    public static void etuModif(Teenager teen){
        String[] header = Plateform.CSVImportHeader.split(";");
        for(String s : header){
            clear();
            System.out.println("Entrez une nouvelle valeur pour le critère " + s + " (null pour une chaine vide ou skip si il faut laisser comme actuellement) :");
            if(s.equals("FORENAME")){
                System.out.println("Valeur actuelle : " + teen.getFirstname());
                System.out.print("Nouvelle valeur : ");
                String chaine = scan.next();
                if(!chaine.equals("skip")){
                    if(chaine.equals("null")){
                        chaine = "";
                    }
                    teen.setFirstname(chaine);
                }
            }else if(s.equals("NAME")){
                System.out.println("Valeur actuelle : " + teen.getName());
                System.out.print("Nouvelle valeur : ");
                String chaine = scan.next();
                if(!chaine.equals("skip")){
                    if(chaine.equals("null")){
                        chaine = "";
                    }
                    teen.setName(chaine);
                }
            }else if(s.equals("BIRTH_DATE")){
                System.out.println("Valeur actuelle : " + teen.date());
                System.out.print("Nouvelle valeur : ");
                String chaine = scan.next();
                if(!chaine.equals("skip")){
                    if(chaine.equals("null")){
                        chaine = "";
                    }
                    teen.setBirthday(chaine);
                }
            }else if(s.equals("COUNTRY")){
                System.out.println("Valeur actuelle : " + teen.getCountry());
                System.out.print("Nouvelle valeur : ");
                String chaine = scan.next();
                if(!chaine.equals("skip")){
                    if(chaine.equals("null")){
                        chaine = "";
                    }
                    teen.setCountry(chaine);
                }
            }else{
                System.out.println("Valeur actuelle : " + teen.getCriterion(CriterionName.valueOf(s)));
                System.out.print("Nouvelle valeur : ");
                String chaine = scan.next();
                if(!chaine.equals("skip")){
                    if(chaine.equals("null")){
                        chaine = "";
                    }
                    teen.setCriterion(s, chaine);
                }
            }
        }
    }

    /**
     * Permet de modifier un certain nombre d'étudiants selon un pays ou non
     */
    public static void etuSupp(){
        int nb = -1;
        while(nb < 0){
            System.out.print("Entrez le nombre d'étudiants à supprimer : ");
            try{
                nb = scan.nextInt();
            }catch (Exception e){

            }
        }
        String country = "";
        System.out.print("Entrez l'éventuel pays d'où ils doivent être supprimés en MAJUSCULE (null sinon) : ");
        country = scan.next();
        List<Teenager> liste = new ArrayList<>();
        if(paysExist(country)){
            liste = plateform.supprimer(nb, Country.valueOf(country));
        }else{
            liste = plateform.supprimer(nb);
        }
        for(Teenager t : liste){
            suppEtuCSV(t);
        }
    }

    /**
     * supprime un étudiants au choix
     */
    public static void suppEtu(){
        clear();
        System.out.println("Liste des étudiants :");
        for(Teenager t : plateform){
            System.out.println(t);
        }
        int idEtu = -1;
        while(!containsId(plateform.getPromo(), idEtu)){
            System.out.print("Entrez le numéro id de l'étudiant à supprimer : ");
            try{
                idEtu = scan.nextInt();
            }catch(Exception e){

            }
        }
        Teenager teen = plateform.getById(idEtu);
        suppEtuCSV(teen);
        plateform.removeById(idEtu);
    }

    /**
     * supprimer un étudiants du fichier csv
     * @param teen étudiant à supprimer
     */
    public static void suppEtuCSV(Teenager teen){
        List<String> fichier = new ArrayList<>();
        fichier.add(Plateform.CSVImportHeader);
        try(BufferedReader br = new BufferedReader(new FileReader(new File(teenList)))){
            br.readLine();
            while(br.ready()){
                String line = br.readLine();
                Teenager t = Teenager.parse(line, fichier.get(0));
                if(!t.equals(teen)){
                    fichier.add(line);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(teenList)))){
            for(String s : fichier){
                bw.write(s);
                bw.newLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * change les pondérations pour le calcul des poids
     */
    public static void defPond(){
        clear();
        System.out.println("Changement pondérations :");
        System.out.print("Indiquez la pondération d'un critère rédhibitoire (actuellement " + AffectationUtil.redhibitoire + "): ");
        try{
            int n = scan.nextInt();
            if(n > 1000){
                n = 1000;
            }
            AffectationUtil.redhibitoire = n;
        }catch(Exception e){

        }
        System.out.print("Indiquez la pondération d'un critère préférentiel (actuellement " + AffectationUtil.pref + "): ");
        try{
            int n = scan.nextInt();
            if(n > 1000){
                n = 1000;
            }
            AffectationUtil.pref = n;
        }catch(Exception e){

        }
        System.out.print("Indiquez la pondération du poids de base (actuellement " + AffectationUtil.initialWeight + "): ");
        try{
            int n = scan.nextInt();
            if(n > 1000){
                n = 1000;
            }
            AffectationUtil.initialWeight = n;
        }catch(Exception e){

        }
    }

    /**
     * menu complet pour la gestion des étudiants
     */
    public static void gesEtu(){
        boolean continuer = true;
        while(continuer){
            clear();
            System.out.println("Liste des étudiants :");
            for(Teenager t : plateform){
                System.out.println(t);
            }
            System.out.println("\nIl y a actuellement " + plateform.getNbCountry(host) + " pour le pays " + host);
            System.out.println("Il y a actuellement " + plateform.getNbCountry(guest) + " pour le pays " + guest);
            System.out.println("\nCommandes :");
            System.out.println("- la commande q permet de quitter l'interface");
            System.out.println("- la commande s permet de supprimer un étudiant précis");
            System.out.println("- la commande p permet de supprimer un certain nombre d'étudiants");
            System.out.println("- la commande m permet de modifier un étudiant");
            System.out.println("- la commande a permet d'ajouter un étudiant");
            char c = ecouterChar();
            if(c == 'q'){
                continuer = false;
            }else if(c == 's'){
                suppEtu();
            }else if(c == 'p'){
                etuSupp();
            }else if(c == 'm'){
                modifEtu();
            }else if(c == 'a'){
                ajouterEtu();
            }
            Teenager.ResetCompteur();
            plateform.clear();
            plateform.importer(teenList);
        }
    }

    /**
     * menu complet pour la gestion des appariements
     */
    public static void gesApp(){
        clear();
        if(plateform.getNbCountry(host) != plateform.getNbCountry(guest)){
            System.out.println("Le nombre d'étudiants doit être identique pour les deux pays");
            System.out.println("Il y a actuellement " + plateform.getNbCountry(host) + " pour le pays " + host);
            System.out.println("Il y a actuellement " + plateform.getNbCountry(guest) + " pour le pays " + guest);
            System.out.println("Il est possible de modifier ça dans l'interface de gestion des étudiants");
            try{
                Thread.sleep(7500);
            }catch(Exception e){

            }
            return;
        }
        plateform.exporter(affList, host, guest);
        Affectations affectations = new Affectations(AffectationUtil.affectation(plateform.getPromo(), guest, host), host, guest);
        boolean continuer = true;
        while(continuer){
            clear();
            System.out.println("Affectations :");
            System.out.println(affectations);
            System.out.println("\nCommandes :");
            System.out.println("- la commande q permet de quitter l'interface");
            System.out.println("- la commande f permet de fixer un appariement");
            System.out.println("- la commande e permet d'éviter un appariement");
            char c = ecouterChar();
            if(c == 'q'){
                continuer = false;
            }else if(c == 'f'){
                appManuel(affectations, 1);
            }else if(c == 'e'){
                appManuel(affectations, 0);
            }
            plateform.exporter(affList, host, guest);
        }
    }

    /**
     * permet de changer les pays hôte et visiteur
     */
    public static void choixPays(){
        clear();
        String host = "";
        while(!paysExist(host)){
            System.out.print("Veuillez rentrer le nom du pays hote en MAJUSCULE : ");
            host = scan.next();
        }
        String guest = "";
        while(!paysExist(guest) || guest.equals(host)){
            System.out.print("Veuillez rentrer le nom du pays guest en MAJUSCULE différent du host : ");
            guest = scan.next();
        }
        InterfaceCommande.host = Country.valueOf(host);
        InterfaceCommande.guest = Country.valueOf(guest);
    }

    /**
     * Vérifier si un pays donné en chaine de caractères existe
     * @param pays pays à vérifier
     * @return true si existe et false sinon
     */
    public static boolean paysExist(String pays){
        for(Country c : Country.values()){
            if(c.name().equals(pays)){
                return true;
            }
        }
        return false;
    }

    /**
     * menu principal de l'application
     * @param args arguments
     */
    public static void main(String[] args) {
        Teenager.ResetCompteur();
        plateform.importer(teenList);
        boolean continuer = true;
        while(continuer){
            clear();
            System.out.println("Le pays hote est " + host.name() + ", le pays visiteur est " + guest.name() + "\n");
            System.out.println("Menu principal :");
            System.out.println("- la commande p définir les pondérations");
            System.out.println("- la commande t gérer les étudiants");
            System.out.println("- la commande a permet de gérer les appariements");
            System.out.println("- la commande c permet de changer les pays hote et guest");
            System.out.println("- la commande q permet de quitter l'application");
            char c = ecouterChar();
            if(c == 'p'){
                defPond();
            }else if(c == 't'){
                gesEtu();
            }else if(c == 'a'){
                gesApp();
            }else if(c == 'c'){
                choixPays();
            }else if(c == 'q'){
                continuer = false;
            }
        }
        scan.close();
    }

    /**
     * permet de clear le terminal de commande
     */
    public static void clear()
	{
        final String ESC = "\033[";
        System.out.print (ESC + "2J");
        System.out.print (ESC + "0;0H");
        System.out.flush();
	}

}
