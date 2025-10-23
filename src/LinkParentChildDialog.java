import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LinkParentChildDialog extends JDialog {
    private final JComboBox<Person> parentCombo;
    private final JComboBox<Person> childCombo;
    private final JCheckBox adoptedCheck;

    private Person selectedParent;
    private Person selectedChild;
    private boolean adopted;

    public LinkParentChildDialog(Frame owner, List<Person> people) {
        super(owner, "Link Parent ↔ Child", true);
        parentCombo = new JComboBox<>(people.toArray(new Person[0]));
        childCombo = new JComboBox<>(people.toArray(new Person[0]));
        adoptedCheck = new JCheckBox("Adopted");

        parentCombo.setRenderer(new PersonRenderer());
        childCombo.setRenderer(new PersonRenderer());

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
        addRow(form, gbc, row++, "Parent:", parentCombo);
        addRow(form, gbc, row++, "Child:", childCombo);

        gbc.gridx = 1; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(adoptedCheck, gbc);

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
        Person parent = (Person) parentCombo.getSelectedItem();
        Person child = (Person) childCombo.getSelectedItem();
        if (parent == null || child == null) {
            JOptionPane.showMessageDialog(this, "Select both parent and child.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (parent.getId().equals(child.getId())) {
            JOptionPane.showMessageDialog(this, "Parent and child cannot be the same person.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedParent = parent;
        selectedChild = child;
        adopted = adoptedCheck.isSelected();
        dispose();
    }

    public Person getSelectedParent() {
        return selectedParent;
    }

    public Person getSelectedChild() {
        return selectedChild;
    }

    public boolean isAdopted() {
        return adopted;
    }

    private static class PersonRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Person p) {
                setText(p.getFullName());
            }
            return this;
        }
    }
}
