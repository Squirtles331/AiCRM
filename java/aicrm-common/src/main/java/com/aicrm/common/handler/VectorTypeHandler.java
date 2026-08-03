package com.aicrm.common.handler;

import com.pgvector.PGvector;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL pgvector 类型处理器
 * <p>
 * Java 侧以 float[] 承载向量，写入时转为 PGvector，读取时转回 float[]。
 * 用法：@TableField(typeHandler = VectorTypeHandler.class)
 * 前置：数据库需启用 pgvector 扩展（CREATE EXTENSION IF NOT EXISTS vector;）
 */
public class VectorTypeHandler extends BaseTypeHandler<float[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setObject(i, new PGvector(parameter));
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toArray(rs.getObject(columnName));
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toArray(rs.getObject(columnIndex));
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toArray(cs.getObject(columnIndex));
    }

    private float[] toArray(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof float[] floats) {
            return floats;
        }
        if (obj instanceof PGvector vector) {
            return vector.toArray();
        }
        if (obj instanceof String sql) {
            try {
                return new PGvector(sql).toArray();
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }
}
