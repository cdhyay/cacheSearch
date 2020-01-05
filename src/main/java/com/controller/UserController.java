package com.controller;

import com.Util.Times;
import com.model.User;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userController")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("list")
    public String selAll(User user, Model model){
        System.out.println(user);
        System.out.println("list-selall");
        if(user.getName()==""&&user.getAge()==null){
            List<User> list = userService.selectAll();
            model.addAttribute("list",list);
            for(User i:list){
                System.out.println(i);
            }
        }
        else{
            List<User> list = userService.selectByNameOrAge(user);
            model.addAttribute("list",list);
            for(User i:list){
                System.out.println(i);
            }
        }
        return "index";
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("findone/{id}")
    public String findOne(@PathVariable Integer id,Model model) {
        System.out.println("findone");
        long startTime = System.currentTimeMillis();   //获取开始时间
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        String key=""+id;
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            User user = operations.get(key);
            System.out.println("==========从缓存中获得数据=========");
            model.addAttribute("user",user);
            System.out.println(user);
            long endTime=System.currentTimeMillis(); //获取结束时间
            Times times=new Times();
            times.user=user;
            times.time=endTime-startTime;
            System.out.println("从缓存中获取数据时间："+times.time+"毫秒");
            return "findone";
        }
        else {
            System.out.println("==========从数据库中获得数据=========");
            User user = userService.selectById(id);
            // 写入缓存
            operations.set(key, user);
            System.out.println("=========写入缓存===================");
            model.addAttribute("user",user);
            System.out.println(user);
            long endTime=System.currentTimeMillis(); //获取结束时间
            Times times=new Times();
            times.user=user;
            times.time=endTime-startTime;
            System.out.println("从数据库中获取数据时间："+times.time+"毫秒");
            return "findone";
        }

    }

    @GetMapping("toAdd")
    public String toAdd() {
        return "insert";
    }

    @GetMapping("insertByGet/{name}")
    public String insertByGet(@PathVariable  String name) {
        User user = new User();
        int number = (int)(Math.random()*100)+1;
        user.setName(name);
        user.setAge(number);
        userService.insert(user);
        return "index";
    }


    @PostMapping("insert")
    public String insert (User user) throws Exception{
        userService.insert(user);
        System.out.println("=============插入成功！==============");
        return "success";
    }

    @RequestMapping("delete/{id}")
    public String deleteById (@PathVariable long id) {
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        userService.deleteById(id);
        String key=id+"";
        boolean hasKey = redisTemplate.hasKey(key);
        if(hasKey)
        {
            redisTemplate.delete(key);
            System.out.println("删除缓存中的key=========>" + key);
        }
        return "success";
    }

    @GetMapping("toUpdate/{id}")
    public String toUpdate(@PathVariable long id,Model model){
        System.out.println("toUpdate");
        model.addAttribute("user", userService.selectById(id));
        return "update";
    }

    @PostMapping("update")
    public String update(User user) throws Exception{
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        userService.updateById(user);
        String key=user.getId()+"";
        boolean hasKey = redisTemplate.hasKey(key);
        if(hasKey)
        {
            redisTemplate.delete(key);
            System.out.println("删除缓存中的key=========>" + key);
            operations.set(key, user);
            System.out.println("更新缓存成功=========>" );
        }
        return "success";
    }

    /**
     * 判断用户名是否存在
     * Resultful风格:{name}传递的参数
     * @PathVariable:接收上面的参数
     * @ResponseBody:返回json数据
     * @param name
     * @return
     */
    @GetMapping("isExist/{name}")
    @ResponseBody
    public Map<String,Object> isExist(@PathVariable  String name) {
        Map<String,Object> result = new HashMap<>();
        result.put("status", this.userService.isExist(name));
        return result;
    }
}
