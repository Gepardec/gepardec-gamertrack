package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.model.Score;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.UserTransaction;
import java.util.Arrays;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class GamertrackDbIT {

  @PersistenceContext
  private EntityManager em;

  @Inject
  private UserTransaction utx;

  void removeTableData(Class<?>... tClasses) throws Exception {
    utx.begin();
    em.joinTransaction();
    Arrays.stream(tClasses)
        .map(this::composeDeleteQuery)
        .forEach(Query::executeUpdate);
    utx.commit();
  }

  Query composeDeleteQuery(Class<?> tClass) {
    return em.createQuery("delete from %s".formatted(tClass.getSimpleName()));
  }

  @Deployment
  public static Archive<?> createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, "test.jar")
        .addClasses(EntityManager.class)
        .addPackage(com.gepardec.adapter.output.persistence.entity.Score.class.getPackage())
        .addPackage(ScoreRepository.class.getPackage())
        .addPackage(ScoreRepositoryImpl.class.getPackage())
        .addPackage(Score.class.getPackage())
        .addPackage(Mapper.class.getPackage())
        .addPackage(TestFixtures.class.getPackage())
        .addAsManifestResource("beans.xml")
        .addAsManifestResource("persistence.xml");
  }

}
