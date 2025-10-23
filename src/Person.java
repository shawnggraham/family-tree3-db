import java.time.LocalDate;
import java.util.UUID;

public class Person {
    private UUID id;
    private String givenName;
    private String familyName;
    private String middleNames;
    private Sex sex = Sex.UNKNOWN;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String notes;

    public Person() {
        this.id = UUID.randomUUID();
    }

    public Person(String givenName, String familyName, Sex sex) {
        this();
        this.givenName = givenName;
        this.familyName = familyName;
        this.sex = sex == null ? Sex.UNKNOWN : sex;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getMiddleNames() { return middleNames; }
    public void setMiddleNames(String middleNames) { this.middleNames = middleNames; }

    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getDeathDate() { return deathDate; }
    public void setDeathDate(LocalDate deathDate) { this.deathDate = deathDate; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFullName() {
        String mid = (middleNames == null || middleNames.isBlank()) ? "" : " " + middleNames;
        return (givenName == null ? "" : givenName) + mid + (familyName == null ? "" : " " + familyName);
    }

    public boolean isDeceased() {
        return deathDate != null;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}