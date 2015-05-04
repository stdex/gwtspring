package ru.userBase.server.dao;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.userBase.shared.dto.UserDTO;

@Repository("userDAO")
public class UserDAO
  extends JpaDAO<Long, UserDTO>
{
  @Autowired
  EntityManagerFactory entityManagerFactory;
  
  @PostConstruct
  public void init()
  {
    super.setEntityManagerFactory(this.entityManagerFactory);
  }
}
