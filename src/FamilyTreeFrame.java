import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class FamilyTreeFrame extends JFrame {
    private final FamilyTree tree;
    private final FamilyTreeRepository repo;

    private final DefaultListModel<Person> personListModel = new DefaultListModel<>();
    private final JList<Person> personList = new JList<>(personListModel);
    private final JTextArea detailsArea = new JTextArea();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_DATE;

    public FamilyTreeFrame(FamilyTree tree, FamilyTreeRepository repo) {
        super("Family Tree");
        this.tree = Objects.requireNonNull(tree, "tree");
        this.repo = Objects.requireNonNull(repo, "repo");
        initUi();
        loadPeople();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top button bar ---
        JPanel buttonPanel = new JPanel();

        JButton addPersonBtn = new JButton("Add Person");
        JButton linkPeopleBtn = new JButton("Link People");
        JButton showChildrenBtn = new JButton("Show Children");
        JButton showGrandChildrenBtn = new JButton("Show Grandchildren");
        JButton refreshBtn = new JButton("Refresh");

        addPersonBtn.addActionListener(e -> addPerson());
        linkPeopleBtn.addActionListener(e -> linkPeople());
        showChildrenBtn.addActionListener(e -> showChildren());
        showGrandChildrenBtn.addActionListener(e -> showGrandChildren());
        refreshBtn.addActionListener(e -> loadPeople());

        buttonPanel.add(addPersonBtn);
        buttonPanel.add(linkPeopleBtn);
        buttonPanel.add(showChildrenBtn);
        buttonPanel.add(showGrandChildrenBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.NORTH);

        // --- Center: list and details split pane ---
        personList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPersonDetails(personList.getSelectedValue());
            }
        });

        JScrollPane listScroll = new JScrollPane(personList);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsArea.setEditable(false);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailsScroll);
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);
    }

    private void loadPeople() {
        personListModel.clear();
        try {
            List<Person> people = repo.loadAllPersons();
            for (Person p : people) {
                personListModel.addElement(p);
            }
            detailsArea.setText("Loaded " + people.size() + " people from the database.");
        } catch (Exception ex) {
            detailsArea.setText("Error loading people: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showPersonDetails(Person p) {
        if (p == null) {
            detailsArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Full Name: ").append(p.getFullName()).append("\n");
        sb.append("Sex: ").append(p.getSex()).append("\n");
        if (p.getBirthDate() != null) sb.append("Born: ").append(DATE_FMT.format(p.getBirthDate())).append("\n");
        if (p.getDeathDate() != null) sb.append("Died: ").append(DATE_FMT.format(p.getDeathDate())).append("\n");
        if (p.getBirthPlace() != null) sb.append("Birth Place: ").append(p.getBirthPlace()).append("\n");
        if (p.getNotes() != null) sb.append("Notes: ").append(p.getNotes()).append("\n");
        sb.append("\nChildren:\n");

        List<Person> children = tree.getChildrenOf(p.getId());
        if (children.isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Person c : children) {
                sb.append("  • ").append(c.getFullName()).append("\n");
            }
        }

        detailsArea.setText(sb.toString());
    }

    private void showChildren() {
        Person selected = personList.getSelectedValue();
        if (selected == null) {
            detailsArea.setText("No person selected.");
            return;
        }
        List<Person> children = tree.getChildrenOf(selected.getId());
        if (children.isEmpty()) {
            detailsArea.setText(selected.getFullName() + " has no children recorded.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Children of ").append(selected.getFullName()).append(":\n\n");
            for (Person c : children) {
                sb.append("• ").append(c.getFullName()).append("\n");
            }
            detailsArea.setText(sb.toString());
        }
    }

    private void showGrandChildren() {
        Person selected = personList.getSelectedValue();
        if (selected == null) {
            detailsArea.setText("No person selected.");
            return;
        }

        List<Person> grandkids = tree.getGrandChildrenOf(selected.getId());
        if (grandkids.isEmpty()) {
            detailsArea.setText(selected.getFullName() + " has no grandchildren recorded.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Grandchildren of ").append(selected.getFullName()).append(":\n\n");
            for (Person g : grandkids) {
                sb.append("• ").append(g.getFullName()).append("\n");
            }
            detailsArea.setText(sb.toString());
        }
    }

    // --- Add Person Dialog ---
    private void addPerson() {
        JTextField givenField = new JTextField();
        JTextField familyField = new JTextField();
        JComboBox<Sex> sexBox = new JComboBox<>(Sex.values());
        JTextField birthPlaceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Given Name:"));
        panel.add(givenField);
        panel.add(new JLabel("Family Name:"));
        panel.add(familyField);
        panel.add(new JLabel("Sex:"));
        panel.add(sexBox);
        panel.add(new JLabel("Birth Place:"));
        panel.add(birthPlaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Person",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Person p = new Person(
                        givenField.getText(),
                        familyField.getText(),
                        (Sex) sexBox.getSelectedItem()
                );
                p.setBirthPlace(birthPlaceField.getText());
                repo.insertPerson(p);
                tree.addPerson(p);
                personListModel.addElement(p);
                detailsArea.setText("Added person: " + p.getFullName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding person: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Link People Dialog ---
    private void linkPeople() {
        if (personListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No people in list to link.");
            return;
        }

        // Build array manually to avoid DefaultListModel toArray() error
        Person[] all = new Person[personListModel.size()];
        for (int i = 0; i < personListModel.size(); i++) {
            all[i] = personListModel.getElementAt(i);
        }

        Person parent = (Person) JOptionPane.showInputDialog(this,
                "Select Parent:", "Link People",
                JOptionPane.PLAIN_MESSAGE, null, all, all[0]);
        if (parent == null) return;

        Person child = (Person) JOptionPane.showInputDialog(this,
                "Select Child:", "Link People",
                JOptionPane.PLAIN_MESSAGE, null, all, all[0]);
        if (child == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Make " + parent.getFullName() + " the parent of " + child.getFullName() + "?",
                "Confirm Link", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try {
            repo.insertParentChild(parent.getId(), child.getId(), false);
            tree.linkParentChild(parent.getId(), child.getId(), false);
            detailsArea.setText("Linked parent " + parent.getFullName() +
                    " → child " + child.getFullName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error linking people: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
