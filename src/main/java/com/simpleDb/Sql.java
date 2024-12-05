package com.simpleDb;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Sql {
    private final SimpleDb simpleDb;
    private final StringBuilder sqlFormat;

    public Sql(SimpleDb simpleDb) {
        this.simpleDb = simpleDb;
        this.sqlFormat = new StringBuilder();
    }

    public Sql append(String sqlBit, Object... params) {
        this.sqlFormat.append(" " + sqlBit);
        return this;
    }

    public long insert() {
        return 1;
    }

    public int update() {
        return 3;
    }

    public int delete() {
        return 2;
    }

    public List<Map<String, Object>> selectRows() {
        return new ArrayList<>(){{
            add(
                    Map.of(
                            "id", 1L,
                            "createdDate", LocalDateTime.now(),
                            "modifiedDate", LocalDateTime.now(),
                            "title", "제목1",
                            "body", "내용1",
                            "isBlind", false
                    )
            );
            add(
                    Map.of(
                            "id", 2L,
                            "createdDate", LocalDateTime.now(),
                            "modifiedDate", LocalDateTime.now(),
                            "title", "제목2",
                            "body", "내용2",
                            "isBlind", false
                    )
            );
            add(
                    Map.of(
                            "id", 3L,
                            "createdDate", LocalDateTime.now(),
                            "modifiedDate", LocalDateTime.now(),
                            "title", "제목3",
                            "body", "내용3",
                            "isBlind", false
                    )
            );
        }};
    }

    public Map<String, Object> selectRow() {
        return Map.of(
                "id", 1L,
                "createdDate", LocalDateTime.now(),
                "modifiedDate", LocalDateTime.now(),
                "title", "제목1",
                "body", "내용1",
                "isBlind", false
        );
    }

    public LocalDateTime selectDatetime() {
        return LocalDateTime.now();
    }

    public long selectLong() {
        return 1L;
    }


    public String selectString() {
        return "제목1";
    }

    // 위임
    public boolean selectBoolean() {
        return simpleDb.selectBooean(sqlFormat.toString().trim());
    }
}
