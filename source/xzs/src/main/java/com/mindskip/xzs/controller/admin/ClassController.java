package com.mindskip.xzs.controller.admin;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ClassInfo;
import com.mindskip.xzs.domain.ClassUser;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.service.ClassService;
import com.mindskip.xzs.service.ClassUserService;
import com.mindskip.xzs.service.UserService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController("AdminClassController")
@RequestMapping(value = "/api/admin/class")
public class ClassController extends BaseApiController {

    private final ClassService classService;
    private final ClassUserService classUserService;
    private final UserService userService;

    @Autowired
    public ClassController(ClassService classService, ClassUserService classUserService, UserService userService) {
        this.classService = classService;
        this.classUserService = classUserService;
        this.userService = userService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<Map<String, Object>>> page(@RequestBody Map<String, Object> params) {
        String name = params.get("name") != null ? params.get("name").toString() : null;
        Integer pageIndex = params.get("pageIndex") != null ? (Integer) params.get("pageIndex") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        PageInfo<ClassInfo> pageInfo = classService.page(name, pageIndex, pageSize);

        PageInfo<Map<String, Object>> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());

        List<Map<String, Object>> list = pageInfo.getList().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("description", c.getDescription());
            map.put("createTime", DateTimeUtil.dateFormat(c.getCreateTime()));
            List<ClassUser> members = classUserService.selectByClassId(c.getId());
            map.put("studentCount", members.size());
            return map;
        }).collect(Collectors.toList());
        result.setList(list);

        return RestResponse.ok(result);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse<List<Map<String, Object>>> list() {
        List<ClassInfo> classes = classService.allClasses();
        List<Map<String, Object>> result = classes.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            return map;
        }).collect(Collectors.toList());
        return RestResponse.ok(result);
    }

    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<Map<String, Object>> select(@PathVariable Integer id) {
        ClassInfo classInfo = classService.selectById(id);
        if (classInfo == null) {
            return RestResponse.fail(2, "班级不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", classInfo.getId());
        map.put("name", classInfo.getName());
        map.put("description", classInfo.getDescription());
        return RestResponse.ok(map);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse edit(@RequestBody Map<String, Object> params) {
        Integer id = params.get("id") != null ? (Integer) params.get("id") : null;
        String name = (String) params.get("name");
        String description = (String) params.get("description");

        if (name == null || name.trim().isEmpty()) {
            return RestResponse.fail(2, "班级名称不能为空");
        }

        if (id == null) {
            ClassInfo classInfo = new ClassInfo();
            classInfo.setName(name.trim());
            classInfo.setDescription(description);
            classInfo.setCreateTime(new Date());
            classInfo.setDeleted(false);
            classService.insertByFilter(classInfo);
        } else {
            ClassInfo classInfo = classService.selectById(id);
            if (classInfo == null) {
                return RestResponse.fail(2, "班级不存在");
            }
            classInfo.setName(name.trim());
            classInfo.setDescription(description);
            classInfo.setModifyTime(new Date());
            classService.updateByIdFilter(classInfo);
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        ClassInfo classInfo = classService.selectById(id);
        if (classInfo == null) {
            return RestResponse.fail(2, "班级不存在");
        }
        classInfo.setDeleted(true);
        classInfo.setModifyTime(new Date());
        classService.updateByIdFilter(classInfo);
        return RestResponse.ok();
    }

    @RequestMapping(value = "/members/{classId}", method = RequestMethod.POST)
    public RestResponse<List<Map<String, Object>>> members(@PathVariable Integer classId) {
        List<ClassUser> classUsers = classUserService.selectByClassId(classId);
        List<Map<String, Object>> result = classUsers.stream().map(cu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("classUserId", cu.getId());
            map.put("userId", cu.getUserId());
            User user = userService.getUserById(cu.getUserId());
            if (user != null) {
                map.put("userName", user.getUserName());
                map.put("realName", user.getRealName());
                map.put("phone", user.getPhone());
                map.put("createTime", DateTimeUtil.dateFormat(cu.getCreateTime()));
            }
            return map;
        }).collect(Collectors.toList());
        return RestResponse.ok(result);
    }

    @RequestMapping(value = "/members/add", method = RequestMethod.POST)
    public RestResponse addMember(@RequestBody Map<String, Object> params) {
        Integer classId = (Integer) params.get("classId");
        Integer userId = (Integer) params.get("userId");
        if (classId == null || userId == null) {
            return RestResponse.fail(2, "参数不完整");
        }
        ClassUser classUser = new ClassUser();
        classUser.setClassId(classId);
        classUser.setUserId(userId);
        classUser.setCreateTime(new Date());
        try {
            classUserService.insertByFilter(classUser);
        } catch (Exception e) {
            return RestResponse.fail(2, "该学生已在班级中");
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/members/remove", method = RequestMethod.POST)
    public RestResponse removeMember(@RequestBody Map<String, Object> params) {
        Integer classId = (Integer) params.get("classId");
        Integer userId = (Integer) params.get("userId");
        if (classId == null || userId == null) {
            return RestResponse.fail(2, "参数不完整");
        }
        classUserService.deleteByClassIdAndUserId(classId, userId);
        return RestResponse.ok();
    }
}
