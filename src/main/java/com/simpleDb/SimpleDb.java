package com.simpleDb;

import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleDb {
    private final String host;
    private final String username;
    private final String password;
    private final String dbName;
    private Connection connection;
    // 데이터베이스 연결 초기화
    private void connect() {
        if (connection != null) {
            return; // 이미 연결되어 있으면 아무 작업도 하지 않음
        }
        String url = String.format("jdbc:mysql://%s/%s?useSSL=false", host, dbName);
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    // 자원 해제
    public void close() {
        if (connection == null) {
            return; // 연결이 없는 경우 아무 작업도 하지 않음
        }
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection: " + e.getMessage(), e);
        }
    }
    public Sql genSql() {
        return new Sql(this);
    }
    // PreparedStatement 파라미터 바인딩 분리
    private void bindParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }

    // SQL 실행 메서드
    private <T> T _run(String sql, Class cls, Object... params) {
        connect();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bindParameters(preparedStatement, params);
            if (sql.startsWith("SELECT")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();

                if (cls == String.class) {
                    return (T) resultSet.getString(1);
                } else if (cls == Map.class) {
                    Map<String, Object> row = new LinkedHashMap<>();

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value;

                        switch (metaData.getColumnType(i)) {
                            case Types.BIGINT:
                                value = resultSet.getLong(columnName);
                                break;
                            case Types.TIMESTAMP:
                                Timestamp timestamp = resultSet.getTimestamp(columnName);
                                value = (timestamp != null) ? timestamp.toLocalDateTime() : null;
                                break;
                            case Types.BOOLEAN:
                                value = resultSet.getBoolean(columnName);
                                break;
                            default:
                                value = resultSet.getObject(columnName);
                                break;
                        }

                        row.put(columnName, value);
                    }
                    return (T) row;

                } else if (cls == LocalDateTime.class) {
                    return (T) resultSet.getTimestamp(1).toLocalDateTime();
                } else if (cls == Long.class) {
                    return (T) (Long) resultSet.getLong(1);
                } else if (cls == Boolean.class) {
                    return (T) (Boolean) resultSet.getBoolean(1);
                }
            }

            return (T) (Integer) preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL: " + sql + ". Error: " + e.getMessage(), e);
        }
    }

    public int run(String sql, Object... params) {
        return _run(sql, Integer.class, params);
    }

    public boolean selectBoolean(String sql) {
        return _run(sql, Boolean.class);
    }

    public String selectString(String sql) {
        return _run(sql, String.class);
    }

    public long selectLong(String sql) {
        return _run(sql, Long.class);
    }

    public LocalDateTime selectDatetime(String sql) {
        return _run(sql, LocalDateTime.class);
    }

    public Map<String, Object> selectRow(String sql) {
        return _run(sql, Map.class);
    }
}