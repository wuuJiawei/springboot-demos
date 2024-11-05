package xyz.easyboot.generator.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class XmlConfig {

    /** 文件路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

}
