package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.dto.LoginDTO;
import com.caiwusys.dto.RegisterDTO;
import com.caiwusys.dto.UpdatePasswordDTO;
import com.caiwusys.entity.User;
import com.caiwusys.mapper.UserMapper;
import com.caiwusys.service.UserService;
import com.caiwusys.util.JwtUtil;
import com.caiwusys.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!Md5Util.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public boolean register(RegisterDTO registerDTO) {
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(Md5Util.md5(registerDTO.getPassword()));
        if (StringUtils.isBlank(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        return save(user);
    }

    @Override
    public boolean updatePassword(Long userId, UpdatePasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!Md5Util.verify(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        user.setPassword(Md5Util.md5(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    @Override
    public boolean updateUserInfo(User user) {
        user.setPassword(null);
        user.setUsername(null);
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
