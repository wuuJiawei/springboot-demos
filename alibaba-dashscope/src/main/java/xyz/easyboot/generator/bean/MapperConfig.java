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
public class MapperConfig {

    /** 包路径 */
    private String packagePath;

    /** 文件路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

    /** 额外要导入的包 */
    private List<String> extraImportList = new ArrayList<>();

}
