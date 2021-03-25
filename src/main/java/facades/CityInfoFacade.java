package facades;

import dtos.CityInfoDTO;
import dtos.HobbyDTO;
import entities.CityInfo;
import entities.Hobby;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

public class CityInfoFacade {
    private static CityInfoFacade instance;
    private static EntityManagerFactory emf;

    //Private constructor to ensure Singleton
    private CityInfoFacade() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class
     */
    public static CityInfoFacade getCityInfoFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CityInfoFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<CityInfoDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<CityInfo> q = em.createQuery("SELECT c FROM CityInfo c", CityInfo.class);
            return q.getResultList().stream().map(CityInfoDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public CityInfoDTO getByPostalCode(String postalcode) {
        EntityManager em = emf.createEntityManager();
        try {
            CityInfo cityInfo = em.find(CityInfo.class, postalcode );
            if(cityInfo == null) {
                throw new WebApplicationException("No postalcode found", 404);
            }
            return new CityInfoDTO(cityInfo);
        } finally {
            em.close();
        }
    }

}
