package xyz;

import cn.hutool.json.JSONUtil;
import io.javalin.Javalin;
import xyz.app.MobileApp;
import xyz.card.CardPackage;
import xyz.telefen.MailSender;
import xyz.telefen.TeleFen;

/**
 * @author wujiawei
 * @see
 * @since 2022/3/30 23:02
 */
public class Main {
    
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        app.get("/", ctx -> ctx.result("v1.0.0"));
        app.get("/sign", new MobileApp.Sign());
        app.get("/request", new MobileApp.Request());
        app.get("/card-package/sign", new CardPackage.Sign());
        app.get("/card-package/sign-and-request", new CardPackage.SignAndRequest());
        app.get("/telefen-sign", ctx -> {
            // 电信翼积分签名
            String body = ctx.queryParam("body");
            String token = ctx.queryParam("token");
            String sign = TeleFen.sign(body, token);
            ctx.json(sign);
        });
        app.get("/telefen-request", ctx -> {
            // 电信翼积分发起请求
            String body = ctx.queryParam("body");
            String header = ctx.queryParam("header");
            String url = ctx.queryParam("url");
            String token = ctx.queryParam("token");
            String result = TeleFen.request(url, token, JSONUtil.parseObj(header), JSONUtil.parseObj(body));
            ctx.json(result);
        });
        app.get("/telefen-mail", context -> {
            String str = context.queryParam("param");
            MailSender.Param param = JSONUtil.toBean(str, MailSender.Param.class);
            new MailSender().send(param);
            context.json("success");
        });
    }

}
