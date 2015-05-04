package ru.userBase.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

import ru.userBase.shared.dto.UserDTO;

@RemoteServiceRelativePath("springGwtServices/userService")
public interface UserService
        extends RemoteService {
    public UserDTO findUser(long paramLong);

    public void saveUser(long paramLong, String paramString1, String paramString2) throws Exception;

    public void saveOrUpdateUser(long paramLong, String paramString1, String paramString2) throws Exception;

    public void deleteUser(long paramLong) throws Exception;

    public List<UserDTO> allUser() throws Exception;
}
