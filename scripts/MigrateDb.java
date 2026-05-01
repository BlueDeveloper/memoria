import java.sql.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class MigrateDb {
    static final String WALLET = "C:/BLUE/Project/blue/OracleCloud/Wallet_BlueAutoDB";
    static final String URL = "jdbc:oracle:thin:@BlueAutoDB_high?TNS_ADMIN=" + WALLET;
    static final String USER = "MEMORIA";
    static final String PASS = "Brp2026Cal!#";

    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        System.out.println("=== DB Migration ===\n");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Connected!\n");
            Statement stmt = conn.createStatement();

            // 1. Drop old objects (ignore errors)
            String[] drops = {
                "DROP TABLE USER_ITEM", "DROP TABLE SHOP_ITEM",
                "DROP TABLE TICKET_TRANSACTION", "DROP TABLE TICKET_BALANCE",
                "DROP TABLE INVITATION", "DROP TABLE EVENT_COMMENT",
                "DROP TABLE EVENT", "DROP TABLE CALENDAR_MEMBER", "DROP TABLE CALENDAR",
                "DROP TABLE DIARY_MEMBER", "DROP TABLE DIARY", "DROP TABLE MEMBER",
                "DROP INDEX IDX_CALENDAR_MEMBER_CAL", "DROP INDEX IDX_CALENDAR_MEMBER_MEM",
                "DROP INDEX IDX_EVENT_CALENDAR", "DROP INDEX IDX_EVENT_CREATOR",
                "DROP INDEX IDX_EVENT_START_DT", "DROP INDEX IDX_EVENT_COMMENT_EVENT",
                "DROP INDEX IDX_EVENT_COMMENT_MEMBER", "DROP INDEX IDX_INVITATION_CALENDAR",
                "DROP INDEX IDX_INVITATION_INVITEE",
                "DROP INDEX IDX_DIARY_MEMBER_DIARY", "DROP INDEX IDX_DIARY_MEMBER_MEM",
                "DROP INDEX IDX_EVENT_DIARY", "DROP INDEX IDX_INVITATION_DIARY",
                "DROP INDEX IDX_TICKET_BALANCE_MEMBER", "DROP INDEX IDX_TICKET_TXN_MEMBER",
                "DROP INDEX IDX_SHOP_ITEM_CATEGORY", "DROP INDEX IDX_USER_ITEM_MEMBER",
                "DROP INDEX IDX_USER_ITEM_ITEM",
                "DROP SEQUENCE SEQ_MEMBER", "DROP SEQUENCE SEQ_CALENDAR",
                "DROP SEQUENCE SEQ_CALENDAR_MEMBER", "DROP SEQUENCE SEQ_EVENT",
                "DROP SEQUENCE SEQ_EVENT_COMMENT", "DROP SEQUENCE SEQ_INVITATION",
                "DROP SEQUENCE SEQ_DIARY", "DROP SEQUENCE SEQ_DIARY_MEMBER",
                "DROP SEQUENCE SEQ_TICKET_BALANCE", "DROP SEQUENCE SEQ_TICKET_TRANSACTION",
                "DROP SEQUENCE SEQ_SHOP_ITEM", "DROP SEQUENCE SEQ_USER_ITEM"
            };

            System.out.println("[1/2] Dropping old objects...");
            for (String sql : drops) {
                try { stmt.execute(sql); System.out.println("  DROP: " + sql.substring(5)); }
                catch (SQLException e) { /* ignore */ }
            }

            // 2. Run new DDL
            System.out.println("\n[2/2] Creating new objects...");
            String ddl = Files.readString(Path.of("docs/sql/create.sql"), StandardCharsets.UTF_8);
            ddl = ddl.replaceAll("/\\*[\\s\\S]*?\\*/", "");
            String[] stmts = ddl.split(";");
            int ok = 0, skip = 0;
            for (String sql : stmts) {
                sql = sql.trim();
                if (sql.isEmpty()) continue;
                try {
                    stmt.execute(sql);
                    ok++;
                    String line = sql.split("\n")[0].trim();
                    if (line.length() > 60) line = line.substring(0, 60) + "...";
                    System.out.println("  OK: " + line);
                } catch (SQLException e) {
                    if (e.getErrorCode() == 955 || e.getErrorCode() == 1408 || e.getErrorCode() == 2264) {
                        skip++;
                    } else {
                        System.out.println("  ERR [" + e.getErrorCode() + "]: " + sql.split("\n")[0].trim());
                    }
                }
            }
            System.out.println("\n=== Done: " + ok + " ok, " + skip + " skip ===");
        }
    }
}
