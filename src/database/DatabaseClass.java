package database;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import sun.security.util.Password;


public class DatabaseClass {

    public static void main(String[] args) {

        /* ---------- Création d'un client ---------- */

        MongoClient mongo = new MongoClient( "localhost" , 27017 );


        /* ---------- Authentification (sécurité) ---------- */

        MongoCredential authentification;
        authentification = MongoCredential.createCredential("admin", "DBtest",
                "password".toCharArray());
        System.out.println("Connexion à la base de données réussie.");


        /* ---------- Accès à la base de données ---------- */

        MongoDatabase database = mongo.getDatabase("DBtest");
        System.out.println("Authentification : "+authentification);


        /* ---------- Création de la collection (table) des utilisateurs ---------- */

        try {

            database.createCollection("users", null);
        } catch (MongoCommandException e) {

            database.getCollection("users").drop();
        }

        DBCollection collection = (DBCollection) database.getCollection("users");



        /* ---------- Fermeture du client Mongo ---------- */

        mongo.close();


    }

    //Méthodes : ajout et suppression d'utilisateurs dans la BDD (inscription et suppression de compte)

    public void ajouterUtilisateurBDD(String nom, String prenom, String pseudo, String mail,
                                      Password password, DBCollection collection){

        BasicDBObject userToAdd = new BasicDBObject();

        userToAdd.put("Nom", nom);
        userToAdd.put("Prenom", prenom);
        userToAdd.put("Pseudo", pseudo);
        userToAdd.put("Mail", mail);
        userToAdd.put("Password", password);

        collection.insert(userToAdd);
    }
    public void supprimerUtilisateurBDD(String nom, String prenom, String pseudo, String mail,
                                        Password password, DBCollection collection){

        BasicDBObject userToDel = new BasicDBObject();

        userToDel.put("Nom", nom);
        userToDel.put("Prenom", prenom);
        userToDel.put("Pseudo", pseudo);
        userToDel.put("Mail", mail);
        userToDel.put("Password", password);

        collection.remove(userToDel);
    }

    public void initialisationBDD(){


    }
}