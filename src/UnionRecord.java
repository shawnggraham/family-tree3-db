import java.time.LocalDate;
import java.util.UUID;

public class UnionRecord {
    private UUID id;
    private UnionType type = UnionType.MARRIAGE;
    private UUID partnerA;
    private UUID partnerB;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String notes;

    public UnionRecord() {
        this.id = UUID.randomUUID();
    }

    public UnionRecord(UnionType type, UUID partnerA, UUID partnerB, LocalDate startDate) {
        this();
        this.type = type == null ? UnionType.MARRIAGE : type;
        this.partnerA = partnerA;
        this.partnerB = partnerB;
        this.startDate = startDate;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UnionType getType() { return type; }
    public void setType(UnionType type) { this.type = type; }

    public UUID getPartnerA() { return partnerA; }
    public void setPartnerA(UUID partnerA) { this.partnerA = partnerA; }

    public UUID getPartnerB() { return partnerB; }
    public void setPartnerB(UUID partnerB) { this.partnerB = partnerB; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() {
        return endDate == null || endDate.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return type + ": " + partnerA + " <> " + partnerB + " @ " + startDate + (endDate == null ? "" : " - " + endDate);
    }
}