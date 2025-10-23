import java.sql.*;

public class Database implements AutoCloseable {
    private final String url;
    private Connection conn;

    public Database(String filePath) {
        this.url = "jdbc:sqlite:" + filePath;
    }

    public void connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");  // <-- required for driver registration
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }

            conn = DriverManager.getConnection(url);
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
    }


    public Connection getConnection() {
        return conn;
    }

    public void runMigrations() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS Person (
                  id           TEXT PRIMARY KEY,
                  givenName    TEXT,
                  familyName   TEXT,
                  middleNames  TEXT,
                  sex          TEXT NOT NULL DEFAULT 'UNKNOWN',
                  birthDate    TEXT,
                  deathDate    TEXT,
                  birthPlace   TEXT,
                  notes        TEXT,
                  CHECK (sex IN ('MALE','FEMALE','UNKNOWN','OTHER'))
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS UnionRecord (
                  id         TEXT PRIMARY KEY,
                  type       TEXT NOT NULL,
                  partnerA   TEXT,
                  partnerB   TEXT,
                  startDate  TEXT,
                  endDate    TEXT,
                  location   TEXT,
                  notes      TEXT,
                  FOREIGN KEY (partnerA) REFERENCES Person(id) ON DELETE SET NULL ON UPDATE CASCADE,
                  FOREIGN KEY (partnerB) REFERENCES Person(id) ON DELETE SET NULL ON UPDATE CASCADE
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS ParentChildLink (
                  id          TEXT PRIMARY KEY,
                  parent      TEXT NOT NULL,
                  child       TEXT NOT NULL,
                  isAdoptive  INTEGER NOT NULL DEFAULT 0,
                  notes       TEXT,
                  FOREIGN KEY (parent) REFERENCES Person(id) ON DELETE CASCADE ON UPDATE CASCADE,
                  FOREIGN KEY (child)  REFERENCES Person(id) ON DELETE CASCADE ON UPDATE CASCADE,
                  CHECK (isAdoptive IN (0,1)),
                  CHECK (parent <> child)
                )
            """);
            st.execute("CREATE INDEX IF NOT EXISTS idx_person_familyName ON Person(familyName)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pcl_parent ON ParentChildLink(parent)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pcl_child  ON ParentChildLink(child)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_union_partnerA ON UnionRecord(partnerA)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_union_partnerB ON UnionRecord(partnerB)");
        }
    }

    @Override
    public void close() {
        if (conn != null) {
            try { conn.close(); } catch (Exception ignored) {}
        }
    }
}
