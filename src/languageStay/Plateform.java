package languageStay;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ulille.but.sae2_02.graphes.Arete;
import languageStay.exceptions.WrongLineFormatException;
import languageStay.graph.AffectationUtil;

/**
 * <strong>Permet de créer un objet Platform </strong>
 * @author Desmee Nathan
 * @author Ouhdda Anas
 * @author Belguebli Rayane
 */

public class Plateform implements Serializable, Iterable<Teenager>{

    private ArrayList<Teenager> promo;
    private AffectationUtil affectationUtil;
    public static String CSVExportHeader = "HOST;GUEST;REDIBITOIRE";
    public static String CSVImportHeader;
    public final static String TEENAGERS_HEADER = "FORENAME;NAME;COUNTRY;BIRTH_DATE;HOBBIES;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;GENDER;PAIR_GENDER;HISTORY";
    public final static String PATH = System.getProperty("user.dir") + File.separator + "res" + File.separator;

    static{
        try(BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + File.separator + "res" + File.separator + "teenagersData.csv")))){
            CSVImportHeader = br.readLine();
        }catch(Exception e){
            CSVImportHeader = "ratio";
        }
    }

    public Plateform(){
        this.affectationUtil = new AffectationUtil();
        this.promo = new ArrayList<>();
    }

    /**
     * Supprime de la platforme un nombre de teenagers.
     * @param nbSupp Nombre de teenager à supprimer
     * @return liste des étudiants supprimés
     */
    public List<Teenager> supprimer(int nbSupp){
        List<Teenager> liste = new ArrayList<>();
        int c = 0;
        for(Teenager t : promo){
            t.purgeInvalidRequierement();
            t.purgeIncoherentRequirement();
        }
        while(c < nbSupp && promo.size() > 0){
            int minCri = minimumCritere(promo);
            List<Teenager> aSupp = new ArrayList<>();
            for(Teenager etudiant : promo){
                if(etudiant.getNbCriterion() == minCri && c < nbSupp){
                    c++;
                    aSupp.add(etudiant);
                }
            }
            liste.addAll(aSupp);
            promo.removeAll(aSupp);
        }
        return liste;
    }

    /**
     * Supprime des teenagers d'un pays de la plateforme.
     * @param nbSupp nombre de teenagers du pays à supprimer
     * @param pays pays des teenagers à supprimer
     * @return liste des étudiants supprimés
     */
    public List<Teenager> supprimer(int nbSupp, Country pays){
        List<Teenager> liste = new ArrayList<>();
        int c = 0;
        for(Teenager t : promo){
            t.purgeInvalidRequierement();
            t.purgeIncoherentRequirement();
        }
        while(c < nbSupp && this.getNbCountry(pays) > 0){
            ArrayList<Teenager> etuCountry = new ArrayList<>();
            for(Teenager t : promo){
                if(t.getCountry() == pays){
                    etuCountry.add(t);
                }
            }
            int minCri = minimumCritere(etuCountry);
            List<Teenager> aSupp = new ArrayList<>();
            for(Teenager etudiant : promo){
                if(etudiant.getNbCriterion() == minCri && etudiant.getCountry() == pays && c < nbSupp){
                    c++;
                    aSupp.add(etudiant);
                }
            }
            liste.addAll(aSupp);
            promo.removeAll(aSupp);
        }
        return liste;
    }

    /**
     * Renvoie le nombre de critère minimum 
     * @param etudiants liste des étudiants 
     * @return le nombre minimal de critère
     */
    public int minimumCritere(ArrayList<Teenager> etudiants){
        int min = etudiants.get(0).getNbCriterion();
        for(Teenager etudiant : etudiants){
            if(min > etudiant.getNbCriterion()){
                min = etudiant.getNbCriterion();
            }
        }
        return min;
    }

    /**
     * Ajoute à la plateforme un teenager, à condition que celui-ci n'y est pas déjà présent.
     * @param teenager Teenager à ajouter $
     * @return true or false si le teenager est ajouté ou non
     */
    public boolean ajouter(Teenager teenager){
        if (!promo.contains(teenager)) return promo.add(teenager);
        return false;
    }

    /**
     * retourne l'ensemble des étudiants que contient la plateform
     * @return promo qui est une arrayList de teenagers
     */
    public ArrayList<Teenager> getPromo() {
        return promo;
    }

    /**
     * Ajoute à la plateforme des teenagers, à condition que ceux-ci n'y soient pas déjà.
     * @param teenagers Liste de Teenager à ajouter 
     */
    public void ajouter(List<Teenager> teenagers){
        for (Teenager t: teenagers){
            if (!promo.contains(t)) promo.add(t);
        }
    }

    /**
     * Ajoute à la plateforme des teenagers, à condition que ceux-ci n'y soient pas déjà.
     * @param teenagers Tableau de Teenager à ajouter 
     */
    public void ajouter(Teenager[] teenagers){
        for (Teenager t: teenagers){
            if (!promo.contains(t)) promo.add(t);
        }
    }

    /**
     * Retire tous les teenagers présents dans la plateforme.
     */
    public void clear(){
        promo.clear();
    }

    /**
     * Vérifie si la plateforme contient un teenager. 
     * @return true or false si le teenager est présent
     */
    public boolean contains(Teenager teenager){
        return promo.contains(teenager);
    }

    /**
     * Supprimer un teenager de la plateforme si celu-ci y est présent. 
     * @oaram teenager le teenager ) supprimer.
     * @return true si le teenager a correctement été supprimé, false sinon.
     */
    public boolean supprimer(Teenager teenager) {
        return promo.remove(teenager);
    }

    /**
     * Retourne le nombre de teenagers dans la plateforme.
     *  @return un int qui défini la taille
     */
    public int size(){
        return promo.size();
    }

    /**
     * Retourne l'index d'un teenager dans la plateforme.
     * @param teenager le teenager dont on veut l'index
     * @return l'index du teenager
     */
    public int indexOf(Teenager teenager){
        return promo.indexOf(teenager);
    }

    public void setInitialWeight(double initialWeight) {
        double i = initialWeight;
        if(initialWeight > 1000.0){
            i = 1000.0;
        }
        this.affectationUtil.setInitialWeight(i);
    }

    public void setRedhibitoire(int redhibitoire) {
        int i = redhibitoire;
        if(redhibitoire > 1000){
            i = 1000;
        }
        this.affectationUtil.setRedhibitoire(i);
    }

    public void setPref(int pref) {
        int i = pref;
        if(pref > 1000){
            i = 1000;
        }
        this.affectationUtil.setPref(i);
    }

    public double getInitialWeight() {
        return affectationUtil.getInitialWeight();
    }

    public int getRedhibitoire() {
        return affectationUtil.getRedhibitoire();
    }

    public int getPref() {
        return affectationUtil.getPref();
    }

    /**
     * Importer des teenagers à partir d'un fichier CSV.
     * @param filename Nom du fichier CSV
     */
    public void importer(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int line = 1;
            String header = br.readLine();
            while (br.ready()) {
                try {
                    promo.add(Teenager.parse(br.readLine(), header));
                } catch (WrongLineFormatException e) {
                    System.out.println("Erreur à la ligne " + line + " : " + e.getMessage());
                    System.out.println("Poursuite de l'importation...");
                }
                line ++;
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier " + filename + " : " + e.getMessage());
        }
    }

    public void exporterTeenagers(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(TEENAGERS_HEADER);
            bw.newLine();
            for (Teenager t: this) {
                bw.write(t.serialize());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture du fichier " + filename + " : " + e.getMessage());
        }
    }

    /**
     * Réalise une affectation entre deux pays et l'exporte. Chaque ligne contient les deux personnes liées et si elle contiennent un critère rédhibitoire.
     * @param filename Nom du fichier CSV
     */
    public void exporter(String filename, Country host, Country guest) {
        File file = new File(filename);
        if (!file.exists()) {
            try { file.createNewFile(); }
            catch (IOException e) {
                System.out.println("Erreur lors de la création du fichier " + filename + " : " + e.getMessage());
            }
        }
        List<Arete<Teenager>> liste = getAretes(host, guest);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(Plateform.CSVExportHeader);
            bw.newLine();
            for (Arete<Teenager> a : liste) {
                String chaine = a.getExtremite1() + ";" + a.getExtremite2() + ";" + !a.getExtremite1().compatibleWithGuest(a.getExtremite2());
                bw.write(chaine);
                bw.newLine();
            }
            Plateform.nouveauSejour(liste, host, guest);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture du fichier " + filename + " : " + e.getMessage());
        }
    }

    public List<Arete<Teenager>> getAretes(Country host, Country guest) {
        return AffectationUtil.affectation(promo, guest, host);
    }

    /**
     * Affecte les adolescents pour un séjour. 
     * @param liste Liste des arêtes
     * @param host Pays hôte
     * @param guest Pays visiteur
     * @return true ou false
     */
    public static boolean nouveauSejour(List<Arete<Teenager>> liste, Country host, Country guest) {
        Affectations affectations = new Affectations(liste);
        return Affectations.exporter(affectations, host + "_" + guest + ".bin");
    }

    /**
     * Affecte les adolescents pour un séjour. 
     * @param host Pays hôte
     * @param guest Pays visiteur
     * @return true ou false
     */
    public boolean nouveauSejour(Country host, Country guest) {
        Affectations affectations = new Affectations(getAretes(host, guest));
        return Affectations.exporter(affectations, host + "_" + guest + ".bin");
    }

    @Override
    public String toString() {
        return "Plateform [promo=" + promo + "]";
    }

    @Override
    public Iterator<Teenager> iterator() {
        return this.promo.iterator();
    }

    
    public int getIndexById(int id){
        for(Teenager t : this.promo){
            if(t.getID() == id){
                return this.promo.indexOf(t);
            }
        }
        return -1;
    }

    public void removeById(int id){
        Teenager aSupp = null;
        for(Teenager t : this.promo){
            if(t.getID() == id){
                aSupp = t;
            }
        }
        this.promo.remove(aSupp);
    }

    public Teenager getById(int id){
        for(Teenager t : this.promo){
            if(t.getID() == id){
                return t;
            }
        }
        return null;
    }

    public Teenager getByIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.promo.size()) {
            throw new IndexOutOfBoundsException();
        }
        return this.promo.get(index);
    }

    public int getTeenagerIndex(Teenager t) {
        return this.promo.indexOf(t);
    }

    public int getNbCountry(Country pays){
        int n = 0;
        for(Teenager t : this.promo){
            if(t.getCountry() == pays){
                n++;
            }
        }
        return n;
    }   
    
    public static Plateform fromFile(String filename) {
        Plateform result = new Plateform();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(Plateform.PATH + filename)))) {
            result = (Plateform) in.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if (result != null) System.out.println("Chargement effectué depuis " + Plateform.PATH + filename);
        return result;
    }

    public boolean toFile(String filename) {
        File file = new File(Plateform.PATH + filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Sauvegarde effectuée dans " + Plateform.PATH + filename);
        return true;
    }
}
