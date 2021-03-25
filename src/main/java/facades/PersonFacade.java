package facades;

import dtos.CityInfoDTO;
import dtos.PersonDTO;
import entities.*;

import javax.persistence.*;
import javax.ws.rs.WebApplicationException;
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
        }
        catch (PersistenceException e) {
            throw new WebApplicationException("Noget gik galt da vi prøvede at indsætte dataen. Prøv igen senere eller kontakt en adminstrator.");
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
            Person person = em.find(Person.class, id);
            if(person == null) {
                throw new WebApplicationException("Personen med id (" + id + "), kunne ikke findes.", 404);
            }
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    public PersonDTO getByNumber(int number) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Phone.class, number).getPerson();
            if(person == null) {
                throw new WebApplicationException("Personen med telefonummeret (" + number + "), kunne ikke findes.", 404);
            }
            return new PersonDTO(person);
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
            if(cityInfo == null) {
                throw new WebApplicationException("Zip koden " + "(" + postalCode + "), blev ikke fundet.", 404);
            }

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
            Hobby foundHobby = em.find(Hobby.class, hobby);
            if(foundHobby == null) {
                throw new WebApplicationException("Vi kunne ikke finde en hobby med navn (" + hobby + ")", 404);
            }
            List<Person> personList = foundHobby.getPeople();
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
            Person person = em.find(Person.class, id);
            if(person == null) {
                throw new WebApplicationException("Personen med id (" + id + "), kunne ikke findes.", 404);
            }
            personDto.validate();

            updatePersonFields(person, personDto, em);

            em.getTransaction().begin();
            em.persist(person.getAddress());
            em.merge(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        }
        catch (PersistenceException e) {
            throw new WebApplicationException("Noget gik galt da vi prøvede at opdatere dataen. Prøv igen senere eller kontakt en adminstrator.");
        }
        finally {
            em.close();
        }
    }

    public PersonDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if(person == null) {
                throw new WebApplicationException("Personen med id (" + id + "), kunne ikke findes.", 404);
            }
            PersonDTO personDto = new PersonDTO(person);
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
        } catch (PersistenceException e) {
            throw new WebApplicationException("Noget gik galt da vi prøvede at slette dataen. Prøv igen senere eller kontakt en adminstrator.");
        }
        finally {
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
