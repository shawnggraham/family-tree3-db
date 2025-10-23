// FamilyTreeRepository.java
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyTreeRepository {
    private final Database db;

    public FamilyTreeRepository(Database db) {
        this.db = db;
    }

    public void insertPerson(Person p) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO Person
            (id, givenName, familyName, middleNames, sex, birthDate, deathDate, birthPlace, notes)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getId().toString());
            ps.setString(2, p.getGivenName());
            ps.setString(3, p.getFamilyName());
            ps.setString(4, p.getMiddleNames());
            ps.setString(5, p.getSex() == null ? "UNKNOWN" : p.getSex().name());
            ps.setString(6, toDate(p.getBirthDate()));
            ps.setString(7, toDate(p.getDeathDate()));
            ps.setString(8, p.getBirthPlace());
            ps.setString(9, p.getNotes());
            ps.executeUpdate();
        }
    }

    public UUID insertParentChild(UUID parentId, UUID childId, boolean adopted) throws SQLException {
        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO ParentChildLink (id, parent, child, isAdoptive, notes)
            VALUES (?,?,?,?,NULL)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, parentId.toString());
            ps.setString(3, childId.toString());
            ps.setInt(4, adopted ? 1 : 0);
            ps.executeUpdate();
        }
        return id;
    }

    public void insertUnion(UnionRecord u) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO UnionRecord
            (id, type, partnerA, partnerB, startDate, endDate, location, notes)
            VALUES (?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getId().toString());
            ps.setString(2, u.getType() == null ? "MARRIAGE" : u.getType().name());
            ps.setString(3, toId(u.getPartnerA()));
            ps.setString(4, toId(u.getPartnerB()));
            ps.setString(5, toDate(u.getStartDate()));
            ps.setString(6, toDate(u.getEndDate()));
            ps.setString(7, u.getLocation());
            ps.setString(8, u.getNotes());
            ps.executeUpdate();
        }
    }

    public List<Person> loadAllPersons() throws SQLException {
        String sql = "SELECT id, givenName, familyName, middleNames, sex, birthDate, deathDate, birthPlace, notes FROM Person";
        List<Person> res = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Person p = new Person();
                p.setId(UUID.fromString(rs.getString("id")));
                p.setGivenName(rs.getString("givenName"));
                p.setFamilyName(rs.getString("familyName"));
                p.setMiddleNames(rs.getString("middleNames"));
                String sex = rs.getString("sex");
                try { p.setSex(sex == null ? Sex.UNKNOWN : Sex.valueOf(sex)); } catch (Exception e) { p.setSex(Sex.UNKNOWN); }
                p.setBirthDate(parseDate(rs.getString("birthDate")));
                p.setDeathDate(parseDate(rs.getString("deathDate")));
                p.setBirthPlace(rs.getString("birthPlace"));
                p.setNotes(rs.getString("notes"));
                res.add(p);
            }
        }
        return res;
    }

    public List<ParentChildLink> loadAllParentChildLinks() throws SQLException {
        String sql = "SELECT parent, child, isAdoptive FROM ParentChildLink";
        List<ParentChildLink> res = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UUID parent = UUID.fromString(rs.getString("parent"));
                UUID child = UUID.fromString(rs.getString("child"));
                boolean adopted = rs.getInt("isAdoptive") == 1;
                res.add(new ParentChildLink(parent, child, adopted));
            }
        }
        return res;
    }

    public List<UnionRecord> loadAllUnions() throws SQLException {
        String sql = "SELECT id, type, partnerA, partnerB, startDate, endDate, location, notes FROM UnionRecord";
        List<UnionRecord> res = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UnionRecord u = new UnionRecord();
                u.setId(UUID.fromString(rs.getString("id")));
                try { u.setType(UnionType.valueOf(rs.getString("type"))); } catch (Exception ignored) {}
                String a = rs.getString("partnerA");
                String b = rs.getString("partnerB");
                u.setPartnerA(a == null ? null : UUID.fromString(a));
                u.setPartnerB(b == null ? null : UUID.fromString(b));
                u.setStartDate(parseDate(rs.getString("startDate")));
                u.setEndDate(parseDate(rs.getString("endDate")));
                u.setLocation(rs.getString("location"));
                u.setNotes(rs.getString("notes"));
                res.add(u);
            }
        }
        return res;
    }

    private static String toDate(LocalDate d) { return d == null ? null : d.toString(); }
    private static LocalDate parseDate(String s) { return (s == null || s.isBlank()) ? null : LocalDate.parse(s); }
    private static String toId(UUID id) { return id == null ? null : id.toString(); }
}