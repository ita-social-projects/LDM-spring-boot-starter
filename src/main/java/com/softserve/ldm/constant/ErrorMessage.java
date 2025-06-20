package com.softserve.ldm.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessage {
    public static final String LOG_FILES_NOT_FOUND = "No log files found";
    public static final String LOG_FILE_NOT_FOUND = "No file found with name: %s";
    public static final String CANNOT_READ_LOG_FILE = "Error reading log file: %s";
    public static final String BAD_SECRET_KEY = "The given secret key is incorrect";
    public static final String CANNOT_DELETE_DOTENV = "Failed to delete .env file";
    public static final String FUNCTIONALITY_NOT_AVAILABLE = "Functionality is not available";
    public static final String INVALID_TABLE_NAME = "Table name must contain only lowercase letters "
            + "and single underscores between words. Cannot start or end with an underscore.";
    public static final String SQL_METADATA_EXCEPTION_MESSAGE = "Error occurred while retrieving database data for: ";
    public static final String MAX_PAGE_SIZE_EXCEPTION = "Page size must be less than or equal to 100";
    public static final String NEGATIVE_VALUE_EXCEPTION = "%s must be a positive number";
    public static final String INVALID_VALUE_EXCEPTION = "Invalid value for %s: must be an integer";
    public static final String NEGATIVE_LIMIT = "Limit cannot be negative";
    public static final String EXCEED_LIMIT = "Out of max rows limit. Max limit is " + AppConstant.SQL_ROW_LIMIT;
    public static final String NEGATIVE_OFFSET = "Offset cannot be negative";
    public static final String GENERATION_EXCEL_FILE_ERROR = "Error generating Excel file";
    public static final String EMPTY_TABLE = "Table '%s' doesn't contain any row";
}
