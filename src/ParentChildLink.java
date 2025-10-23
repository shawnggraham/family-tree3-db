import java.util.UUID;

public class ParentChildLink {
    private UUID parentId;
    private UUID childId;
    private boolean adopted;

    public ParentChildLink(UUID parentId, UUID childId, boolean adopted) {
        this.parentId = parentId;
        this.childId = childId;
        this.adopted = adopted;
    }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public UUID getChildId() { return childId; }
    public void setChildId(UUID childId) { this.childId = childId; }

    public boolean isAdopted() { return adopted; }
    public void setAdopted(boolean adopted) { this.adopted = adopted; }
}