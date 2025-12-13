package org.yearup.data.mysql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class MySqlDaoBase
{
    protected DataSource ds;

    public MySqlDaoBase(DataSource ds)
    {
        this.ds = ds;
    }

    protected Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }
}
