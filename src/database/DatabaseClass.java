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



public class DatabaseClass {

    public static void main(String[] args) {

        //Création d'un client
        MongoClient mongo = new MongoClient( "localhost" , 27017 );

        //Authentification (sécurité)
        MongoCredential authentification;
        authentification = MongoCredential.createCredential("admin", "DBtest",
                "password".toCharArray());
        System.out.println("Connexion à la base de données réussie.");

        //Accès à la base de données
        MongoDatabase database = mongo.getDatabase("DBtest");
        System.out.println("Authentification : "+authentification);


        /* Mémo personnel : en comparaison avec le système MySQL :
        *
        * Les Tables en SQL deviennent des Collections avec Mongo
        * Les Lignes (Rows) deviennent des Documents
        * Les Colonnes (Columns) deviennent des Champs (Fields)
        *
        * */

        //Création de la collection (table) des utilisateurs
        try {

            database.createCollection("users", null);
        } catch (MongoCommandException e) {

            database.getCollection("users").drop();
        }

        DBCollection collection = (DBCollection) database.getCollection("users");

        BasicDBObject document = new BasicDBObject();

        document.put("Nom", "Tony");
        document.put("Prenom", "Stark");
        document.put("Pseudo", "YouKnowWhoIAm");
        document.put("Mail", "ironman@starkindustries.com");

        collection.insert(document);





        //Fermeture du client Mongo
        mongo.close();





    }

    //Méthodes : ajout et suppression d'utilisateurs dans la BDD (inscription et suppression de compte)

    public void ajouterUtilisateur(){

    }
    public void supprimerUtilisateur(){

    }
}