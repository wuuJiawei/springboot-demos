package xyz.easyboot.generator;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.easyboot.generator.bean.EnumParamDefinition;
import xyz.easyboot.generator.bean.EnumResultDefinition;

import java.util.Collections;
import java.util.List;

public class AiEnumBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AiEnumBuilder.class);

    public static String buildMessage(String json) {
        String template = ResourceUtil.readStr("generator/enum-json-response-example.json", CharsetUtil.CHARSET_UTF_8);
        String prompt = "根据表名和字段注释生成固定结构的json，要求严格返回要求的结构，不要过度发挥。" +
                "要求的结构示例如下: {}，其中comment是枚举对应字段的注释，如果原来的注释很混乱，要求进行注释的整理，比如统一标点符号、统一文本结构等。" +
                "其中className是枚举对应字段生成的枚举的类名，为了避免重复，要会加上表名作为前缀。" +
                "其中items是枚举项，item的name是枚举项的key，item的comment是枚举项的注释，item的code是枚举项的值。要足够贴切，能够见名知意。" +
                "主要要先根据注释判断这个字段是否枚举，如果不是枚举，则isEnum计为false，items直接计为空数组。" +
                "注意不要用markdown，更不要用代码快包裹，直接提供文本。" +
                "这是要求生成的表名和对应的字段: {}";
        return StrUtil.format(prompt, template, json);
    }

    public static List<EnumResultDefinition> generate(List<EnumParamDefinition> enumParamDefinitions) {
        try {
            String result = callWithMessage(JSONUtil.toJsonStr(enumParamDefinitions));
            return JSONUtil.toList(result, EnumResultDefinition.class);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    public static String callWithMessage2(String json) throws ApiException, NoApiKeyException, InputRequiredException {
        return ResourceUtil.readStr("generator/enum-json-response-example.json", CharsetUtil.CHARSET_UTF_8);
    }

    public static String callWithMessage(String json) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();

        String content = buildMessage(json);
        logger.info("content: {}", content);

        Message msg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();

        GenerationParam param = GenerationParam.builder()
                // https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-coder-turbo")
                .messages(Collections.singletonList(msg))
                .resultFormat(GenerationParam.ResultFormat.TEXT)
                .topP(0.8)
                .temperature(0.7f)
                .enableSearch(false)
                .apiKey("sk-fa4b847d20384176baf299a42bf688d7")
                .build();

        logger.info("AI识别中...");
        GenerationResult result = gen.call(param);
        logger.info("result: {}", result);
        return cleanOutputText(result.getOutput().getText());
    }

    private static String cleanOutputText(String text) {
        return text.replace("```json", "").replace("```", "");
    }

}
