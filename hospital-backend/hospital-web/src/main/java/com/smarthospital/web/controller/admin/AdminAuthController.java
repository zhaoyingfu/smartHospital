package com.smarthospital.web.controller.admin;

import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.response.R;
import com.smarthospital.dal.mapper.SysUserMapper;
import com.smarthospital.model.dto.LoginReq;
import com.smarthospital.model.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginReq req) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername()));
        if (user == null) {
            throw new BizException(401, "用户名或密码错误");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        result.put("token", "mock-jwt-token-" + user.getId());
        return R.ok(result);
    }
}