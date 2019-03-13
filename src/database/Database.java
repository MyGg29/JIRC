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


public class Database {
    /* ---------- Création d'un client ---------- */
    MongoClient mongo = new MongoClient( "localhost" , 27017 );

    /* ---------- Accès à la base de données ---------- */
    MongoDatabase jIrcDatabase = mongo.getDatabase("JIRC");

    /* ---------- Création de la collection (table) des utilisateurs ---------- */


    public void createCollection(){
        try {

            jIrcDatabase.createCollection("users", null);
        } catch (MongoCommandException e) {

            jIrcDatabase.getCollection("users").drop();
        }
    }

    DBCollection collection = (DBCollection) jIrcDatabase.getCollection("users");


    //Méthodes : ajout et suppression d'utilisateurs dans la BDD (inscription et suppression de compte)

    public void ajouterUtilisateurBDD(String nom, String prenom, String pseudo,
                                      String password, String age){

        BasicDBObject userToAdd = new BasicDBObject();
        userToAdd.put("Nom", nom);
        userToAdd.put("Prenom", prenom);
        userToAdd.put("Pseudo", pseudo);
        userToAdd.put("Password", password);
        userToAdd.put("Age", age);

        collection.insert(userToAdd);
    }
    public void supprimerUtilisateurBDD(String nom, String prenom, String pseudo,
                                         String password, String age){

        BasicDBObject userToDel = new BasicDBObject();
        userToDel.put("Nom", nom);
        userToDel.put("Prenom", prenom);
        userToDel.put("Pseudo", pseudo);
        userToDel.put("Password", password);
        userToDel.put("Age", age);

        collection.remove(userToDel);
    }

    public void parcourirBase(String nom, String prenom, String pseudo,
                                        String password, String age){

        BasicDBObject user = new BasicDBObject();
        user.put("Nom", nom);
        user.put("Prenom", prenom);
        user.put("Pseudo", pseudo);
        user.put("Password", password);
        user.put("Age", age);

        collection.find(user);
    }



    public void closeMongo(){
        mongo.close();
    }


    //TODO : export, peut-être avec mongoexport (mais c'est une ligne de commande)



}
