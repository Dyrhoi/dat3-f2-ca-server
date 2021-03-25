package facades;

import dtos.HobbyDTO;
import entities.Hobby;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

public class HobbyFacade {
    private static HobbyFacade instance;
    private static EntityManagerFactory emf;

    //Private constructor to ensure Singleton
    private HobbyFacade() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class
     */
    public static HobbyFacade getHobbyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<HobbyDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> q = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
            return q.getResultList().stream().map(HobbyDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public HobbyDTO getByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            Hobby hobby = em.find(Hobby.class, name );
            if(hobby == null) {
                throw new WebApplicationException("No hobby found", 404);
            }
            return new HobbyDTO(hobby);
        } finally {
            em.close();
        }
    }

    public List<HobbyDTO> searchByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> q = em.createQuery("SELECT h FROM Hobby h WHERE h.name LIKE :name", Hobby.class);
            q.setParameter("name", "%" + name + "%");
            return q.getResultList().stream().map(HobbyDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}
