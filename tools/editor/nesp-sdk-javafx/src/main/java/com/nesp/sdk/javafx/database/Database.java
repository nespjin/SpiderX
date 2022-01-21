package com.nesp.sdk.javafx.database;

import com.nesp.sdk.java.util.Log;
import com.nesp.sdk.javafx.content.ContentValues;
import javafx.application.Platform;
import org.sqlite.SQLiteConfig;
import org.sqlite.mc.SQLiteMCSqlCipherConfig;
import org.sqlite.mc.SQLiteMCWxAES256Config;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/29 下午3:57
 * Description:
 **/
public abstract class Database {
    public static boolean sLogEnable = false;
    private static final String TAG = "Database";

    /**
     * Algorithms used in ON CONFLICT clause
     * http://www.sqlite.org/lang_conflict.html
     */
    /**
     * When a constraint violation occurs, an immediate ROLLBACK occurs,
     * thus ending the current transaction, and the command aborts with a
     * return code of SQLITE_CONSTRAINT. If no transaction is active
     * (other than the implied transaction that is created on every command)
     * then this algorithm works the same as ABORT.
     */
    public static final int CONFLICT_ROLLBACK = 1;

    /**
     * When a constraint violation occurs,no ROLLBACK is executed
     * so changes from prior commands within the same transaction
     * are preserved. This is the default behavior.
     */
    public static final int CONFLICT_ABORT = 2;

    /**
     * When a constraint violation occurs, the command aborts with a return
     * code SQLITE_CONSTRAINT. But any changes to the database that
     * the command made prior to encountering the constraint violation
     * are preserved and are not backed out.
     */
    public static final int CONFLICT_FAIL = 3;

    /**
     * When a constraint violation occurs, the one row that contains
     * the constraint violation is not inserted or changed.
     * But the command continues executing normally. Other rows before and
     * after the row that contained the constraint violation continue to be
     * inserted or updated normally. No error is returned.
     */
    public static final int CONFLICT_IGNORE = 4;

    /**
     * When a UNIQUE constraint violation occurs, the pre-existing rows that
     * are causing the constraint violation are removed prior to inserting
     * or updating the current row. Thus the insert or update always occurs.
     * The command continues executing normally. No error is returned.
     * If a NOT NULL constraint violation occurs, the NULL value is replaced
     * by the default value for that column. If the column has no default
     * value, then the ABORT algorithm is used. If a CHECK constraint
     * violation occurs then the IGNORE algorithm is used. When this conflict
     * resolution strategy deletes rows in order to satisfy a constraint,
     * it does not invoke delete triggers on those rows.
     * This behavior might change in a future release.
     */
    public static final int CONFLICT_REPLACE = 5;

    /**
     * use the following when no conflict action is specified.
     */
    public static final int CONFLICT_NONE = 0;
    private static final String[] CONFLICT_VALUES = new String[]
            {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};

    private final String mDatabasePath;
    private Connection mConnection;
    private boolean mIsConnectWhenExecSQL = false;

    private static final String META_TABLE_NAME = "meta";

    private final String mKey;

    public Database(final String path) {
        this(path, "");
    }

    public Database(final String path, final String key) {
        mDatabasePath = path;
        mKey = key;
    }

    public boolean connect() {
        try {
            final File file = new File(getDatabasePath());
            final boolean exists = file.exists();
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    System.out.println("Database file create failed");
                }
            }

            if (!exists) {
                if (!file.createNewFile()) {
                    System.out.println("Database file create failed");
                }
            }

            if (mKey == null || mKey.isEmpty()) {
                mConnection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
            } else {
                final SQLiteConfig defaultSqlCipherConfig = getDefaultSqlCipherConfig();
                final SQLiteConfig sqlCipherConfig = getSqlCipherConfig();

                mConnection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath(),
                        (sqlCipherConfig == null ? defaultSqlCipherConfig : sqlCipherConfig).toProperties());
            }
            final Thread databaseInitializeThread = new Thread(exists ? this::checkWhenInitialize : this::create);
            databaseInitializeThread.setDaemon(true);
            databaseInitializeThread.start();
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected final SQLiteConfig getDefaultSqlCipherConfig() {
        return SQLiteMCSqlCipherConfig.getV4Defaults().withKey(mKey);
    }

    protected SQLiteConfig getSqlCipherConfig() {
        return getDefaultSqlCipherConfig();
    }

    private void checkWhenInitialize() {
        final Optional<ResultSet> resultSet = execQuery("SELECT * FROM " + META_TABLE_NAME);
        int oldVersion = -1;
        if (resultSet.isPresent()) {
            try {
                while (resultSet.get().next()) {
                    if (resultSet.get().getString("key").equals("version")) {
                        oldVersion = Integer.parseInt(resultSet.get().getString("value"));
                        break;
                    }
                }
            } catch (SQLException | NumberFormatException ignored) {

            }

            if (getVersion() > oldVersion && oldVersion >= 0) {
                if (upgrade(oldVersion, getVersion())) {
                    execUpdate(" UPDATE " + META_TABLE_NAME +
                            " SET `value`=" + getVersion() + " WHERE `key`=\"version\"");
                }
            }
        } else {
            initializeDatabaseInfoTable();
        }
    }

    private void create() {
        initializeDatabaseInfoTable();

        onCreate();
    }

    private void initializeDatabaseInfoTable() {
        final String metaTableName = META_TABLE_NAME;
        // create database info table
        final String sqlCreateDatabaseInfoTable =
                "CREATE TABLE IF NOT EXISTS " + metaTableName + "(" +
                        "id INTEGER PRIMARY KEY, " +
                        "key TEXT NOT NULL DEFAULT \"\" UNIQUE," +
                        "value  TEXT NOT NULL DEFAULT \"\"" +
                        ")";
        execUpdate(sqlCreateDatabaseInfoTable);

        // insert database info to table
        final ContentValues contentValuesDatabaseInfo = new ContentValues();
        contentValuesDatabaseInfo.put("key", "version");
        contentValuesDatabaseInfo.put("value", String.valueOf(getVersion()));
        insert(metaTableName, contentValuesDatabaseInfo);
    }

    private boolean upgrade(int oldVersion, int newVersion) {

        return onUpgrade(oldVersion, newVersion);
    }

    /**
     * Call when Database need to upgrade from oldVersion to newVersion. Run on child thread.
     *
     * @param oldVersion the previous version of database.
     * @param newVersion the current version of database.
     * @return upgrade success or not. true - success
     */
    public abstract boolean onUpgrade(int oldVersion, int newVersion);

    /**
     * @return version of database. must be >= 0.
     */
    public abstract int getVersion();

    /**
     * This method will call when database created on child thread.
     */
    public abstract void onCreate();

    public boolean isConnected() {
        try {
            return mConnection != null && !mConnection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnect() {
        if (mConnection != null) {
            try {
                mConnection.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private synchronized void checkThreadAndConnection() {
        if (Platform.isFxApplicationThread()) {
            throw new RuntimeException("Not allow run sql in UI Thread");
        }
        if (!isConnected()) {
            throw new IllegalStateException("Database(" + getDatabasePath() + ") is not connected");
        }
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs) {
        return query(table, whereClause, whereArgs, -1);
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs, final int limit) {
        return query(table, whereClause, whereArgs, null, null, limit);
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs, final String[] columnNames) {
        return query(table, whereClause, whereArgs, columnNames, -1);
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs, final String[] columnNames,
                                     final int limit) {
        return query(table, whereClause, whereArgs, null, columnNames, limit);
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs, final int[] columnIndexes) {
        return query(table, whereClause, whereArgs, columnIndexes, null, -1);
    }

    public Optional<ResultSet> query(final String table, final String whereClause,
                                     final String[] whereArgs, final int[] columnIndexes,
                                     final int limit) {
        return query(table, whereClause, whereArgs, columnIndexes, null, limit);
    }

    private Optional<ResultSet> query(final String table, final String whereClause,
                                      final String[] whereArgs, final int[] columnIndexes,
                                      final String[] columnNames,
                                      final int limit) {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT");
        sql.append(" * ");
        sql.append(" FROM `");
        sql.append(table);
        sql.append("` WHERE ");
        sql.append(whereClause);
        if (limit > 0) {
            sql.append(" LIMIT ");
            sql.append(limit);
        }
        return execQuery(sql.toString(), whereArgs, columnIndexes, columnNames);
    }

    public int insert(final String table, final ContentValues contentValues) {
        return insert(table, "", contentValues);
    }

    public int insert(final String table, final ContentValues contentValues,
                      final int conflictAlgorithm) {
        return insert(table, "", contentValues, conflictAlgorithm);
    }

    public int insert(final String table, final String nullColumnHack,
                      final ContentValues initialValues) {
        return insert(table, nullColumnHack, initialValues, CONFLICT_NONE);
    }

    public int insert(final String table, final String nullColumnHack,
                      final ContentValues initialValues, final int conflictAlgorithm) {
        if (table == null || table.trim().isEmpty() ||
                initialValues == null || initialValues.isEmpty()) {
            return -1;
        }

        final List<String> args = new ArrayList<>();

        final StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(CONFLICT_VALUES[conflictAlgorithm]);
        sql.append(" INTO `");
        sql.append(table);
        sql.append("`");

        final StringBuilder sqlColumnNamesSb = new StringBuilder();
        final StringBuilder sqlColumnValueSb = new StringBuilder();
        initialValues.getValues().forEach((columnName, value) -> {
            if (!sqlColumnNamesSb.isEmpty()) {
                sqlColumnNamesSb.append(",");
            }
            sqlColumnNamesSb.append(columnName);

            if (!sqlColumnValueSb.isEmpty()) {
                sqlColumnValueSb.append(",");
            }
            sqlColumnValueSb.append("?");

            args.add(value == null ? nullColumnHack : String.valueOf(value));
        });
        sql.append(" (").append(sqlColumnNamesSb).append(") ");
        sql.append("VALUES");
        sql.append(" (").append(sqlColumnValueSb).append("); ");
        return execUpdateWithArgs(sql.toString(), args.toArray(new String[0]));
    }

    public int update(final String table, final ContentValues values,
                      final String whereClause, final String[] whereArgs) {
        return updateWithOnConflict(table, values, whereClause, whereArgs, CONFLICT_NONE);
    }

    public int updateWithOnConflict(final String table, final ContentValues values,
                                    final String whereClause, final String[] whereArgs,
                                    final int conflictAlgorithm) {
        if (table == null || table.isEmpty() || values == null || values.isEmpty()) {
            return -1;
        }
        final StringBuilder sql = new StringBuilder(120);
        sql.append(" UPDATE ");
        sql.append(CONFLICT_VALUES[conflictAlgorithm]);
        sql.append("`");
        sql.append(table);
        sql.append("`");
        sql.append(" SET ");

        final Set<Map.Entry<String, Object>> entrySet = values.valueSet();
        Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();

        while (entriesIter.hasNext()) {
            final Map.Entry<String, Object> entry = entriesIter.next();
            sql.append(entry.getKey());
            sql.append("=?");
            if (entriesIter.hasNext()) {
                sql.append(",");
            }
        }

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }

        final int size = entrySet.size();
        final String[] selectionArgs = new String[size + whereArgs.length];
        entriesIter = entrySet.iterator();
        for (int i = 0; i < size; i++) {
            selectionArgs[i] = String.valueOf(entriesIter.next().getValue());
        }

        // Bind where args
        System.arraycopy(whereArgs, 0, selectionArgs, size, whereArgs.length);

        return execUpdateWithArgs(sql.toString(), selectionArgs);
    }

    public int delete(final String table) {
        return delete(table, null, null);
    }

    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        final StringBuilder sql = new StringBuilder("DELETE FROM `" + table + "`");
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
            return execUpdateWithArgs(sql.toString(), whereArgs);
        } else {
            return execUpdate(sql.toString());
        }
    }

    public boolean execWithArgs(final String sql, final String[] selectionArgs) {
        checkThreadAndConnection();
        if (sLogEnable) {
            Log.i(TAG, "execWithArgs: SQL = " + sql + " selectionArgs = " +
                    Arrays.toString(selectionArgs));
        }
        try {
            final PreparedStatement statement = mConnection.prepareStatement(sql);
            for (int i = 0; i < selectionArgs.length; i++) {
                statement.setString(i + 1, selectionArgs[i]);
            }
            statement.closeOnCompletion();
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exec(final String sql) throws SQLException {
        checkThreadAndConnection();
        if (sLogEnable) Log.i(TAG, "exec: SQL = " + sql);
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.execute(sql);
    }

    public boolean exec(final String sql, final int autoGeneratedKeys) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.execute(sql, autoGeneratedKeys);
    }

    public boolean exec(final String sql, final int[] columnIndexes) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.execute(sql, columnIndexes);
    }

    public boolean exec(final String sql, final String[] columnNames) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.execute(sql, columnNames);
    }

    public int execUpdateWithArgs(final String sql, final String[] selectionArgs) {
        checkThreadAndConnection();
        if (sLogEnable) Log.i(TAG, "execUpdateWithArgs: SQL = " + sql + " selectionArgs = " +
                Arrays.toString(selectionArgs));
        try {
            final PreparedStatement statement = mConnection.prepareStatement(sql);
            for (int i = 0; i < selectionArgs.length; i++) {
                statement.setString(i + 1, selectionArgs[i]);
            }
            statement.closeOnCompletion();
            return statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int execUpdate(final String sql) {
        checkThreadAndConnection();
        if (sLogEnable) Log.i(TAG, "execUpdate: SQL = " + sql);
        try {
            final Statement statement = mConnection.createStatement();
            statement.closeOnCompletion();
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int execUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.executeUpdate(sql, autoGeneratedKeys);
    }

    public int execUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.executeUpdate(sql, columnIndexes);
    }

    public int execUpdate(final String sql, final String[] columnNames) throws SQLException {
        checkThreadAndConnection();
        final Statement statement = mConnection.createStatement();
        statement.closeOnCompletion();
        return statement.executeUpdate(sql, columnNames);
    }

    public Optional<ResultSet> execQuery(final String sql, final String[] args) {
        return execQuery(sql, args, null, null);
    }

    public Optional<ResultSet> execQuery(final String sql, final String[] args,
                                         final String[] columnNames) {
        return execQuery(sql, args, null, columnNames);
    }

    public Optional<ResultSet> execQuery(final String sql, final String[] args,
                                         final int[] columnIndexes) {
        return execQuery(sql, args, columnIndexes, null);
    }

    private Optional<ResultSet> execQuery(final String sql, final String[] args,
                                          final int[] columnIndexes,
                                          final String[] columnNames) {
        checkThreadAndConnection();
        if (sLogEnable) Log.i(TAG, "execQuery: SQL = " + sql + ",args = " + Arrays.toString(args));
        try {
            final PreparedStatement statement;
            if (columnIndexes != null && columnIndexes.length > 0) {
                statement = mConnection.prepareStatement(sql, columnIndexes);
            } else if (columnNames != null && columnNames.length > 0) {
                statement = mConnection.prepareStatement(sql, columnNames);
            } else {
                statement = mConnection.prepareStatement(sql);
            }

            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    statement.setString(i + 1, args[i]);
                }
            }
            statement.closeOnCompletion();
            return Optional.of(statement.executeQuery());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<ResultSet> execQuery(final String sql) {
        checkThreadAndConnection();
        if (sLogEnable) Log.i(TAG, "execQuery: SQL = " + sql);
        try {
            final Statement statement = mConnection.createStatement();
            statement.closeOnCompletion();
            return Optional.of(statement.executeQuery(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Statement beginTransaction() throws SQLException {
        checkThreadAndConnection();
        return mConnection.createStatement();
    }

    public Connection getConnection() {
        return mConnection;
    }

    public String getDatabasePath() {
        return mDatabasePath;
    }

    public Database setConnectWhenExecSQL(final boolean connectWhenExecSQL) {
        mIsConnectWhenExecSQL = connectWhenExecSQL;
        return this;
    }

    public boolean isConnectWhenExecSQL() {
        return mIsConnectWhenExecSQL;
    }

    private static void tryCloseStatement(final Statement statement) {
        if (statement == null) return;
        try {
            statement.closeOnCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
