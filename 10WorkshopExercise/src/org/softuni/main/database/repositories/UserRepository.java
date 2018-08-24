package database.repositories;

import database.models.User;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class UserRepository implements Repository {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("Casebook");

    private EntityManager entityManager;

    public UserRepository(){

        this.entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public boolean create(String username, String password){

        EntityTransaction transaction = null;

        try{
            transaction = this.entityManager.getTransaction();
            transaction.begin();

            this.entityManager.persist (new User(username,password));
            transaction.commit();

            return false;

        }catch (Exception e){
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        }

        return false;

    }

    public User[] findAll(){


        return null;
    }


}
