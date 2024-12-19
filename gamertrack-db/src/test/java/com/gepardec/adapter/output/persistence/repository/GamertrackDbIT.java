package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class GamertrackDbIT {

  void removeTableData(EntityManager em, UserTransaction utx, Class<?> tClass) throws Exception {
    utx.begin();
    em.joinTransaction();
    em.createQuery("delete from %s".formatted(tClass.getSimpleName())).executeUpdate();
    utx.commit();
  }

  @Deployment
  public static Archive<?> createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClasses(EntityManager.class)
            .addPackage(Score.class.getPackage())
            .addPackage(ScoreRepository.class.getPackage())
            .addPackage(ScoreRepositoryImpl.class.getPackage())
            .addPackage(ScoreDto.class.getPackage())
            .addPackage(Mapper.class.getPackage())
            .addPackage(TestFixtures.class.getPackage())
            .addAsManifestResource("beans.xml")
            .addAsManifestResource("persistence.xml");
  }

}
