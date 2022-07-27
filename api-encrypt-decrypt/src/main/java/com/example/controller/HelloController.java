package com.example.controller;

import cn.hutool.core.lang.Dict;
import com.example.dto.JsonResult;
import com.example.handle.Encrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wujiawei
 * @see
 * @since 2022/6/27 09:33
 */
@RestController
@Slf4j
public class HelloController {
    
    @Encrypt
    @GetMapping("hello")
    public JsonResult<?> hello() {
        Dict dict = new Dict();
        dict.set("name", "测试中文");
        return new JsonResult<>(0, "hello world", dict);
    }
    
    @Encrypt
    @GetMapping("get")
    public JsonResult<?> get(String a, Integer b) {
        Dict dict = new Dict();
        dict.set("name", "测试中文");
        dict.set("a", a);
        dict.set("b", b);
        return new JsonResult<>(0, "hello world", dict);
    }
    
    @Encrypt
    @PostMapping("post")
    public JsonResult<?> post(String a, Integer b) {
        Dict dict = new Dict();
        dict.set("name", "测试中文");
        dict.set("a", a);
        dict.set("b", b);
        return new JsonResult<>(0, "hello world", dict);
    }
    
    @GetMapping("world")
    public JsonResult<?> world() {
        Dict dict = new Dict();
        dict.set("name", "测试中文");
        return new JsonResult<>(0, "hello world", dict);
    }
}
