package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.dto.LoginDTO;
import com.caiwusys.dto.RegisterDTO;
import com.caiwusys.dto.UpdatePasswordDTO;
import com.caiwusys.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    Map<String, Object> login(LoginDTO loginDTO);

    boolean register(RegisterDTO registerDTO);

    boolean updatePassword(Long userId, UpdatePasswordDTO dto);

    boolean updateUserInfo(User user);

    User getByUsername(String username);
}
