import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PersonDialog extends JDialog {
    private JTextField givenNameField;
    private JTextField familyNameField;
    private JComboBox<Sex> sexCombo;
    private JTextField birthDateField; // yyyy-MM-dd
    private JTextField birthPlaceField;
    private JTextArea notesArea;

    private Person createdPerson;

    public PersonDialog(Frame owner) {
        super(owner, "Add Person", true);
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        givenNameField = new JTextField(20);
        familyNameField = new JTextField(20);
        sexCombo = new JComboBox<>(Sex.values());
        birthDateField = new JTextField(10);
        birthPlaceField = new JTextField(20);
        notesArea = new JTextArea(4, 20);

        addRow(form, gbc, row++, "Given name:", givenNameField);
        addRow(form, gbc, row++, "Family name:", familyNameField);
        addRow(form, gbc, row++, "Sex:", sexCombo);
        addRow(form, gbc, row++, "Birth date (yyyy-MM-dd):", birthDateField);
        addRow(form, gbc, row++, "Birth place:", birthPlaceField);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        form.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(new JScrollPane(notesArea), gbc);
        row++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        buttons.add(ok);
        buttons.add(cancel);

        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> dispose());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(comp, gbc);
    }

    private void onOk() {
        String given = givenNameField.getText().trim();
        String family = familyNameField.getText().trim();
        if (given.isEmpty() || family.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Given and family names are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Sex sex = (Sex) sexCombo.getSelectedItem();
        Person p = new Person(given, family, sex);

        String birthDateText = birthDateField.getText().trim();
        if (!birthDateText.isEmpty()) {
            try {
                p.setBirthDate(LocalDate.parse(birthDateText));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Birth date must be in yyyy-MM-dd format.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String birthPlace = birthPlaceField.getText().trim();
        if (!birthPlace.isEmpty()) p.setBirthPlace(birthPlace);

        String notes = notesArea.getText().trim();
        if (!notes.isEmpty()) p.setNotes(notes);

        this.createdPerson = p;
        dispose();
    }

    public Person getCreatedPerson() {
        return createdPerson;
    }
}
