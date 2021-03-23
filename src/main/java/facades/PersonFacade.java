package facades;

import dtos.PersonDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class PersonFacade {
    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private constructor to ensure Singleton
    private PersonFacade(){
    }


    /**
     * @param _emf
     * @return an instance of this facade class
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf){
        if (instance == null){
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    public PersonDTO create(String firstname, String lastname, PersonDTO.AddressDTO address, List<PersonDTO.PhoneDTO> phone, String email, List<PersonDTO.HobbyDTO> hobbies){
        EntityManager em = emf.createEntityManager();
        Person tmpPerson = new Person(email, firstname, lastname);
        Address tmpAddress = new Address(address.getStreet());
        CityInfo tmpCityInfo = em.find(CityInfo.class, address.getPostalcode());
        for (PersonDTO.PhoneDTO p : phone){
            tmpPerson.addPhone(em.find(Phone.class, p.getNumber()));
        }
        for (PersonDTO.HobbyDTO h : hobbies){
            tmpPerson.addHobby(em.find(Hobby.class, h.getName()));
        }
        tmpAddress.setCityInfo(tmpCityInfo);
        tmpPerson.setAddress(tmpAddress);

        try{
            em.getTransaction().begin();
            em.persist(tmpPerson);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(tmpPerson);
    }

    public PersonDTO getPersonById(long id){
        EntityManager em = emf.createEntityManager();
        return new PersonDTO(em.find(Person.class, id));
    }

    public List<PersonDTO> getAll(){
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> personList = q.getResultList();
        return PersonDTO.toList(personList);
    }

    /*public List<PersonDTO> getPersonsByCity(int zipCode){
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> q = em.createQuery("SELECT c FROM CityInfo c WHERE c.zipCode = :zipCode", CityInfo.class);
        q.setParameter("zipCode", zipCode);
        CityInfo cityInfo = q.getSingleResult();
        List<Person> personList = new ArrayList<>();
        cityInfo.getAddresses().forEach(address -> {
            address.get
            personList.add()
        });
    }

     */


}
