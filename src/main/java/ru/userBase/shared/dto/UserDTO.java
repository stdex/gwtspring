package ru.userBase.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(lazy = false)

@Table(name="UserBase")
public class UserDTO
  implements IsSerializable
{
  private static final long serialVersionUID = 7440297955003302414L;
  @Id
  @Column(name="user_id")
  private long userId;
  @Column(name="user_name", nullable=false, length=30)
  private String userName;
  @Column(name="phoneNumber", nullable=false, length=20)
  private String phoneNumber;

  public UserDTO() {}
  
  public UserDTO(long userId)
  {
    this.userId = userId;
  }

  public UserDTO(long userId, String userName,  String phoneNumber)
  {
    this.userId = userId;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
  }
  
  public long getUserId()
  {
    return this.userId;
  }
  
  public void setUserId(long userId)
  {
    this.userId = userId;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getPhoneNumber()
  {
    return this.phoneNumber;
  }
  
  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }
}
