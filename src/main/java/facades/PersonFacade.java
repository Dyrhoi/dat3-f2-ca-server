package facades;

import dtos.CityInfoDTO;
import dtos.PersonDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public PersonDTO save(String firstname, String lastname, PersonDTO.AddressDTO address, List<PersonDTO.PhoneDTO> phone, String email, List<PersonDTO.HobbyDTO> hobbies){
        EntityManager em = emf.createEntityManager();
        Person tmpPerson = new Person(email, firstname, lastname);
        Address tmpAddress = new Address(address.getStreet());
        CityInfo tmpCityInfo = em.find(CityInfo.class, address.getPostalcode());
        for (PersonDTO.PhoneDTO p : phone){
            Phone ph1 = new Phone(p.getNumber(), p.getDescription());
            tmpPerson.addPhone(ph1);
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

    public PersonDTO getById(long id){
        EntityManager em = emf.createEntityManager();
        return new PersonDTO(em.find(Person.class, id));
    }

    public List<PersonDTO> getAll(){
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> personList = q.getResultList();
        return PersonDTO.toList(personList);
    }

    public List<PersonDTO> getByPostalCode(String postalCode){
        EntityManager em = emf.createEntityManager();
        CityInfo cityInfo = em.find(CityInfo.class, postalCode);
        List<Person> personList = new ArrayList<>();
        cityInfo.getAddresses().forEach(address -> {
            personList.addAll(address.getPeople());
        });
        return PersonDTO.toList(personList);
    }

    public List<PersonDTO> getByHobby(String hobby){
        EntityManager em = emf.createEntityManager();
        TypedQuery<Hobby> q = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
        q.setParameter("name", hobby);
        Hobby hobbies = q.getSingleResult();
        List<Person> personList = hobbies.getPeople();
        return PersonDTO.toList(personList);
    }

    public List<CityInfoDTO> getAllZipCodes(){
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> q = em.createQuery("SELECT z FROM CityInfo z", CityInfo.class);
        return q.getResultList().stream().map(CityInfoDTO::new).collect(Collectors.toList());
    }

    public PersonDTO updatePerson(int id, PersonDTO pDTO){
        return null;
    }

    public PersonDTO deletePerson(int id){
        return null;
    }

}
