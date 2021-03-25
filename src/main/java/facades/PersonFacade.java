package facades;

import dtos.CityInfoDTO;
import dtos.PersonDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonFacade {
    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private constructor to ensure Singleton
    private PersonFacade() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonDTO save(PersonDTO personDto) {
        EntityManager em = emf.createEntityManager();
        try {
            personDto.validate();
            Person tmpPerson = new Person();

            updatePersonFields(tmpPerson, personDto, em);

            em.getTransaction().begin();
            em.persist(tmpPerson);
            em.getTransaction().commit();
            return new PersonDTO(tmpPerson);
        } finally {
            em.close();
        }
    }

    public PersonDTO save(String firstname, String lastname, PersonDTO.AddressDTO address, List<PersonDTO.PhoneDTO> phone, String email, List<PersonDTO.HobbyDTO> hobbies) {
        return save(new PersonDTO(firstname, lastname, address, phone, email, hobbies));
    }

    public PersonDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return new PersonDTO(em.find(Person.class, id));
        } finally {
            em.close();
        }
    }

    public PersonDTO getByNumber(int number) {
        EntityManager em = emf.createEntityManager();
        try {
            return new PersonDTO(em.find(Phone.class, number).getPerson());
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> personList = q.getResultList();
            return PersonDTO.toList(personList);
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getByPostalCode(String postalCode) {
        EntityManager em = emf.createEntityManager();
        try {
            CityInfo cityInfo = em.find(CityInfo.class, postalCode);

            List<Person> personList = new ArrayList<>();
            cityInfo.getAddresses().forEach(address -> {
                personList.addAll(address.getPeople());
            });
            return PersonDTO.toList(personList);
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getByHobby(String hobby) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> q = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
            q.setParameter("name", hobby);
            Hobby hobbies = q.getSingleResult();
            List<Person> personList = hobbies.getPeople();
            return PersonDTO.toList(personList);
        } finally {
            em.close();
        }
    }

    public List<CityInfoDTO> getAllZipCodes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<CityInfo> q = em.createQuery("SELECT z FROM CityInfo z", CityInfo.class);
            return q.getResultList().stream().map(CityInfoDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public PersonDTO update(long id, PersonDTO personDto) {
        EntityManager em = emf.createEntityManager();
        try {
            personDto.validate();
            Person person = em.find(Person.class, id);
            updatePersonFields(person, personDto, em);
            em.getTransaction().begin();
            em.persist(person.getAddress());
            em.merge(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    public PersonDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        PersonDTO personDto = new PersonDTO(person);
        try {
            em.getTransaction().begin();
            person.removeAllHobbies();
            if (person.getAddress() != null) {
                CityInfo cityInfo = em.find(CityInfo.class, person.getAddress().getCityInfo().getPostalCode());
                cityInfo.removeAddress(person.getAddress());
                em.merge(cityInfo);
            }
            em.remove(person);
            em.getTransaction().commit();
            return personDto;
        } finally {
            em.close();
        }
    }

    private void updatePersonFields(Person person, PersonDTO dto, EntityManager em) {
        // Remove all the existing bidirectional
        person.removeAllHobbies();
        person.removeAllPhone();

        // -- UPDATE ADDRESS FIELD ---
        // Multiple people can live on ONE address.
        // When we update a person, we need to make that:
        // 1. We find their old address in the system and remove them from it.
        // 2. If that address is empty, we delete it.
        //      2.1 Then we remove from our CityInfo address list as well.
        if (person.getAddress() != null) {
            Address oldAddress = person.getAddress();
            CityInfo oldCityInfo = oldAddress.getCityInfo();

            oldAddress.removePerson(person);

            if (oldAddress.getPeople().size() == 0) {
                oldCityInfo.removeAddress(oldAddress);
            }
        }
        Address newAddress = new Address(dto.getAddress().getStreet());
        CityInfo newCityInfo = em.find(CityInfo.class, dto.getAddress().getPostalcode());

        newAddress.setCityInfo(newCityInfo);
        person.setAddress(newAddress);

        person.setFirstName(dto.getFirstname());
        person.setLastName(dto.getLastname());
        person.setEmail(dto.getEmail());

        for (PersonDTO.PhoneDTO p : dto.getPhone()) {
            Phone ph1 = new Phone(p.getNumber(), p.getDescription());
            person.addPhone(ph1);
        }
        for (PersonDTO.HobbyDTO h : dto.getHobbies()) {
            person.addHobby(em.find(Hobby.class, h.getName()));
        }
    }

}
