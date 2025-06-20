package com.softserve.ldm.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {
    public static final String DOTENV_FILENAME = "secretKeys.env";
    public static final String VALID_TABLE_NAME_REGEX = "^(?!_)[a-z]+(?:_[a-z]+){0,10}(?<!_)$";
    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String TABLE = "TABLE";
    public static final String SELECT_FROM_WITH_LIMIT_AND_OFFSET = "SELECT * FROM %s LIMIT %d OFFSET %d;";
    public static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM %s;";
    public static final int SQL_ROW_LIMIT = 10_000;
}
