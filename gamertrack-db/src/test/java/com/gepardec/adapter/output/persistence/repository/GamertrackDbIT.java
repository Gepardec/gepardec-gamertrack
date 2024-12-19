package com.gepardec.adapter.output.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

public class GamertrackDbIT {

  void removeTableData(EntityManager em, UserTransaction utx, Class<?> tClass) throws Exception {
    utx.begin();
    em.joinTransaction();
    em.createQuery("delete from %s".formatted(tClass.getSimpleName())).executeUpdate();
    utx.commit();
  }

}
