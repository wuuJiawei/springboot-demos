package xyz.easyboot.generator;

import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DbReader {

    private final DataSource dataSource;

    public DbReader() {
        String url = "jdbc:mysql://localhost:3306/bbh_htb?useUnicode=true&characterEncoding=UTF-8&useSSL=false&databaseTerm=SCHEMA&allowMultiQueries=true";
        String user = "root";
        String password = "123456";
        this.dataSource = new SimpleDataSource(url, user, password);
    }

    public DbReader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Table getTable(String tableName) {
        return MetaUtil.getTableMeta(dataSource, tableName);
    }

    public List<Column> getTableColumns(String tableName) {
        return new ArrayList<>(getTable(tableName).getColumns());
    }

}
