package xyz.easyboot.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/6/8
 */
@Controller
public class IndexController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/")
    public String index(HttpServletRequest request){
        request.setAttribute("port", port);
        return "index";
    }

}
