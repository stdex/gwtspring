package ru.userBase.server.services;

import java.io.*;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletOutputStream;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.userBase.server.dao.UserDAO;
import ru.userBase.shared.dto.UserDTO;
import ru.userBase.shared.services.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;

    @PostConstruct
    public void init()
            throws Exception {
    }

    @PreDestroy
    public void destroy() {
    }

    public UserDTO findUser(long userId) {
        return (UserDTO)userDAO.findById(Long.valueOf(userId));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void saveUser(long userId, String userName, String phoneNumber)
            throws Exception {
        UserDTO userDTO = (UserDTO) userDAO.findById(Long.valueOf(userId));
        if (userDTO == null) {
            userDTO = new UserDTO(userId, userName, phoneNumber);
            userDAO.persist(userDTO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public synchronized void deleteUser(long userId) throws Exception {
        UserDTO userDTO = (UserDTO) userDAO.findById(Long.valueOf(userId));
        if (userDTO != null) {
            userDAO.remove(userDTO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List allUser() throws Exception {
        List<UserDTO> userDTO = userDAO.findAll();
        System.out.println("count user = " + userDTO.size());
        return userDTO;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public synchronized void saveOrUpdateUser(long userId, String userName, String phoneNumber)
            throws Exception  {
        UserDTO userDTO = new UserDTO(userId, userName, phoneNumber);

        userDAO.merge(userDTO);
    }
}
