import java.util.*;
import java.util.stream.Collectors;

public class FamilyTree {
    private final Map<UUID, Person> persons = new HashMap<>();
    private final Map<UUID, UnionRecord> unions = new HashMap<>();
    private final List<ParentChildLink> parentChildLinks = new ArrayList<>();

    // People
    public Person addPerson(Person person) {
        Objects.requireNonNull(person, "person");
        persons.put(person.getId(), person);
        return person;
    }

    public Optional<Person> findPerson(UUID id) {
        return Optional.ofNullable(persons.get(id));
    }

    public Collection<Person> listPeople() {
        return Collections.unmodifiableCollection(persons.values());
    }

    // Unions
    public UnionRecord addUnion(UnionRecord union) {
        Objects.requireNonNull(union, "union");
        unions.put(union.getId(), union);
        return union;
    }

    public Optional<UnionRecord> findUnion(UUID id) {
        return Optional.ofNullable(unions.get(id));
    }

    public Collection<UnionRecord> listUnions() {
        return Collections.unmodifiableCollection(unions.values());
    }

    // Parent-child
    public ParentChildLink linkParentChild(UUID parentId, UUID childId, boolean adopted) {
        if (!persons.containsKey(parentId)) throw new IllegalArgumentException("Unknown parentId: " + parentId);
        if (!persons.containsKey(childId)) throw new IllegalArgumentException("Unknown childId: " + childId);
        ParentChildLink link = new ParentChildLink(parentId, childId, adopted);
        parentChildLinks.add(link);
        return link;
    }

    public List<Person> getParentsOf(UUID childId) {
        return parentChildLinks.stream()
                .filter(l -> l.getChildId().equals(childId))
                .map(ParentChildLink::getParentId)
                .map(persons::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Person> getChildrenOf(UUID parentId) {
        return parentChildLinks.stream()
                .filter(l -> l.getParentId().equals(parentId))
                .map(ParentChildLink::getChildId)
                .map(persons::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Convenience queries
    public List<Person> getSiblingsOf(UUID personId) {
        Set<UUID> parentIds = parentChildLinks.stream()
                .filter(l -> l.getChildId().equals(personId))
                .map(ParentChildLink::getParentId)
                .collect(Collectors.toSet());

        Set<UUID> siblingIds = parentChildLinks.stream()
                .filter(l -> parentIds.contains(l.getParentId()))
                .map(ParentChildLink::getChildId)
                .filter(id -> !id.equals(personId))
                .collect(Collectors.toSet());

        return siblingIds.stream()
                .map(persons::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Grandchildren
    public List<Person> getGrandChildrenOf(UUID grandParentId) {
        List<Person> grandchildren = new ArrayList<>();
        List<Person> children = getChildrenOf(grandParentId);
        for (Person child : children) {
            grandchildren.addAll(getChildrenOf(child.getId()));
        }
        return grandchildren;
    }
}
