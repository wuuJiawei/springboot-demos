package xyz.easyboot;

import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.easyboot.dubbo.ITestApi;
import xyz.easyboot.dubbo.TestVo;

import javax.annotation.PostConstruct;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@EnableAutoConfiguration
public class DubboConsumerApplication {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference(url = "dubbo://127.0.0.1:10086")
    private ITestApi api;

    @PostConstruct
    public void runner() {
        TestVo vo = api.print("你好，Dubbo");
        logger.info(vo.getId() + " " + vo.getName());
    }

    @RequestMapping(value = "/say-hello", method = GET)
    public TestVo sayHello(@RequestParam String name) {
        return api.print("你好，Dubbo");
    }

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApplication.class, args);
    }

}
