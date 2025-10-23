import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // --- Open database connection and keep it alive during GUI lifetime ---
            Database db = new Database("familytree3.db");
            try {
                db.connect();
                db.runMigrations();

                FamilyTree tree = new FamilyTree();
                FamilyTreeRepository repo = new FamilyTreeRepository(db);

                // Load data from the database into memory
                for (Person p : repo.loadAllPersons()) {
                    tree.addPerson(p);
                }
                for (ParentChildLink link : repo.loadAllParentChildLinks()) {
                    tree.linkParentChild(link.getParentId(), link.getChildId(), link.isAdopted());
                }
                for (UnionRecord u : repo.loadAllUnions()) {
                    tree.addUnion(u);
                }

                // --- Launch main GUI window ---
                FamilyTreeFrame frame = new FamilyTreeFrame(tree, repo);

                // Close DB cleanly when window exits
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        db.close();
                    }
                });

                frame.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
                db.close();
            }
        });
    }
}
