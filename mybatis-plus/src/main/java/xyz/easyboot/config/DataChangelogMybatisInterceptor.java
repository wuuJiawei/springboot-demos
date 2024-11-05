package xyz.easyboot.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * @author wuu
 */
@Slf4j
@Component
public class DataChangelogMybatisInterceptor extends DataChangeRecorderInnerInterceptor {

    private static String[] TABLE_NAMES = {};

    public void setTableName(String... tableName) {
        TABLE_NAMES = tableName;
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        super.beforePrepare(sh, connection, transactionTimeout);
    }

    @Override
    protected void dealOperationResult(OperationResult operationResult) {
        logger.info("operationResult: {}", operationResult);
        List<DataChangedRecord> changedRecords = JSONUtil.toList(operationResult.getChangedData(), DataChangedRecord.class);
        log.info("changedRecords: {}", JSONUtil.toJsonPrettyStr(changedRecords));
    }

    @Override
    public OperationResult processUpdate(Update updateStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        String tableName = updateStmt.getTable().getName();
        if (!Arrays.asList(TABLE_NAMES).contains(tableName)) {
            return null;
        }
        Expression where = updateStmt.getWhere();
        PlainSelect selectBody = new PlainSelect();
        Table table = updateStmt.getTable();
        String operation = SqlCommandType.UPDATE.name().toLowerCase();
        selectBody.setFromItem(table);

        // 获取主键字段
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
            primaryKeys = new ArrayList<>();
            while (primaryKeysResultSet.next()) {
                String columnName = primaryKeysResultSet.getString("COLUMN_NAME");
                primaryKeys.add(columnName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 仅支持单主键
        String pkColumn = primaryKeys.getFirst();
        String pkValue = boundSql.getParameterObject().toString();
        log.info("pkColumn: {}, pkValue: {}", pkColumn, pkValue);

        List<Column> updateColumns = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateColumns.addAll(updateSet.getColumns());
        }
        log.info("updateColumns: {}", updateColumns);

        Columns2SelectItemsResult selectItems = buildColumns2SelectItems(tableName, updateColumns);
        log.info("selectItems: {}", selectItems);

        selectBody.setSelectItems(selectItems.getSelectItems());
        selectBody.setWhere(where);
        SelectItem<PlainSelect> plainSelectSelectItem = new SelectItem<>(selectBody);
        log.info("plainSelectSelectItem: {}", plainSelectSelectItem);

        BoundSql boundSql4Select = new BoundSql(mappedStatement.getConfiguration(), plainSelectSelectItem.toString(),
                prepareParameterMapping4Select(boundSql.getParameterMappings(), updateStmt),
                boundSql.getParameterObject());
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        Map<String, Object> additionalParameters = mpBoundSql.additionalParameters();
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            for (Map.Entry<String, Object> ety : additionalParameters.entrySet()) {
                boundSql4Select.setAdditionalParameter(ety.getKey(), ety.getValue());
            }
        }

        Map<String, Object> updatedColumnDatas = getUpdatedColumnDatas(tableName, boundSql, updateStmt);
        log.info("updatedColumnDatas: {}", updatedColumnDatas);

        OriginalDataObj originalData = buildOriginalObjectData(updatedColumnDatas, selectBody, selectItems.getPk(), mappedStatement, boundSql4Select, connection);
        log.info("originalData: {}", originalData);

        OperationResult result = new OperationResult();
        result.setOperation(operation);
        result.setTableName(tableName);
        result.setRecordStatus(true);
        List<DataChangedRecord> dataChangedRecords = compareAndGetUpdatedColumnDataList(pkColumn, pkValue, originalData, updatedColumnDatas);
        log.info("dataChangedRecords: {}", dataChangedRecords);

        String changedDataText = buildDataStr(dataChangedRecords);
        result.setChangedData(changedDataText);
        return result;
    }

    @Override
    public OperationResult processDelete(Delete deleteStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        String tableName = deleteStmt.getTable().getName();
        if (!Arrays.asList(TABLE_NAMES).contains(tableName)) {
            return null;
        }
        return super.processDelete(deleteStmt, mappedStatement, boundSql, connection);
    }

    @Override
    public OperationResult processInsert(Insert insertStmt, BoundSql boundSql) {
        String tableName = insertStmt.getTable().getName();
        if (!Arrays.asList(TABLE_NAMES).contains(tableName)) {
            return null;
        }

        // 获取主键字段
        List<String> primaryKeys = new ArrayList<>();

        String pkColumn = null;
        String pkValue = null;

        String operation = SqlCommandType.INSERT.name().toLowerCase();
        OperationResult result = new OperationResult();
        result.setOperation(operation);
        result.setTableName(tableName);
        result.setRecordStatus(true);
        Map<String, Object> updatedColumnDatas = getUpdatedColumnDatas(tableName, boundSql, insertStmt);
        result.buildDataStr(compareAndGetUpdatedColumnDataList(pkColumn, pkValue, null, null));
        return result;
    }

    public String buildDataStr(List<DataChangedRecord> records) {
        return JSONUtil.toJsonStr(records);
    }

    private List<DataChangedRecord> compareAndGetUpdatedColumnDataList(String pkColumn, String pkValue, OriginalDataObj originalDataObj, Map<String, Object> columnNameValMap) {
        if (originalDataObj == null || originalDataObj.isEmpty()) {
            DataChangedRecord oneRecord = new DataChangedRecord();
            List<DataColumnChangeResult> updateColumns = new ArrayList<>(columnNameValMap.size());
            for (Map.Entry<String, Object> ety : columnNameValMap.entrySet()) {
                String columnName = ety.getKey();
                updateColumns.add(DataColumnChangeResult.constrcutByUpdateVal(columnName, ety.getValue()));
            }
            oneRecord.setUpdatedColumns(updateColumns);
            oneRecord.setPkColumnName(pkColumn);
            oneRecord.setPkColumnVal(pkValue);
            return Collections.singletonList(oneRecord);
        }
        List<DataChangedRecord> originalDataList = originalDataObj.getOriginalDataObj();
        List<DataChangedRecord> updateDataList = new ArrayList<>(originalDataList.size());
        for (DataChangedRecord originalData : originalDataList) {
            if (originalData.hasUpdate(columnNameValMap, Set.of(), Set.of())) {
                updateDataList.add(originalData);
            }
        }
        return updateDataList;
    }

    /**
     * 准备单个的变更前数据
     *
     * @param updatedColumnDatas
     * @param resultSet
     * @param pk
     * @return
     * @throws SQLException
     */
    private DataChangedRecord prepareOriginalDataObj(Map<String, Object> updatedColumnDatas, ResultSet resultSet, Column pk) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<DataColumnChangeResult> originalColumnDatas = new LinkedList<>();
        DataColumnChangeResult pkval = null;
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toUpperCase();
            DataColumnChangeResult col;
            Object updateVal = updatedColumnDatas.get(columnName);
            if (updateVal != null && updateVal.getClass().getCanonicalName().startsWith("java.")) {
                col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i, updateVal.getClass()));
            } else {
                col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i));
            }
            if (pk != null && columnName.equalsIgnoreCase(pk.getColumnName())) {
                pkval = col;
            } else {
                originalColumnDatas.add(col);
            }
        }
        DataChangedRecord changedRecord = new DataChangedRecord();
        changedRecord.setOriginalColumnDatas(originalColumnDatas);
        if (pkval != null) {
            changedRecord.setPkColumnName(pkval.getColumnName());
            changedRecord.setPkColumnVal(pkval.getOriginalValue());
        }
        return changedRecord;
    }

    private Object buildOriginalObjectData(Select selectStmt, Column pk, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        // 查询原始数据
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            // 从ResultSet中获取查询结果的对象
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<Map<String, Object>> results = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            // 这里 results 包含了所有行的数据，每行是一个 Map
            return results;
        } catch (Exception e) {
            return null;
        }
    }

    private OriginalDataObj buildOriginalObjectData(Map<String, Object> updatedColumnDatas, Select selectStmt, Column pk, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            List<DataChangedRecord> originalObjectDatas = new LinkedList<>();
            int count = 0;

            while (resultSet.next()) {
                ++count;
                originalObjectDatas.add(prepareOriginalDataObj(updatedColumnDatas, resultSet, pk));
            }
            OriginalDataObj result = new OriginalDataObj();
            result.setOriginalDataObj(originalObjectDatas);
            resultSet.close();
            return result;
        } catch (Exception e) {
            if (e instanceof DataUpdateLimitationException) {
                throw (DataUpdateLimitationException) e;
            }
            logger.error("try to get record tobe updated for selectStmt={}", selectStmt, e);
            return new OriginalDataObj();
        }
    }

    private List<ParameterMapping> prepareParameterMapping4Select(List<ParameterMapping> originalMappingList, Update updateStmt) {
        List<Expression> updateValueExpressions = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateValueExpressions.addAll(updateSet.getValues());
        }
        int removeParamCount = 0;
        for (Expression expression : updateValueExpressions) {
            if (expression instanceof JdbcParameter) {
                ++removeParamCount;
            }
        }
        return originalMappingList.subList(removeParamCount, originalMappingList.size());
    }

    private Columns2SelectItemsResult buildColumns2SelectItems(String tableName, List<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return Columns2SelectItemsResult.build(Collections.singletonList(new SelectItem<>(new AllColumns())), 0);
        }
        List<SelectItem<?>> selectItems = new ArrayList<>(columns.size());
        for (Column column : columns) {
            selectItems.add(new SelectItem<>(column));
        }
        TableInfo tableInfo = getTableInfoByTableName(tableName);
        if (tableInfo == null) {
            return Columns2SelectItemsResult.build(selectItems, 0);
        }
        Column pk = new Column(tableInfo.getKeyColumn());
        selectItems.add(new SelectItem<>(pk));
        Columns2SelectItemsResult result = Columns2SelectItemsResult.build(selectItems, 1);
        result.setPk(pk);
        return result;
    }

    private TableInfo getTableInfoByTableName(String tableName) {
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                return tableInfo;
            }
        }
        return null;
    }


}
