package setTheoryHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.vp.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SetTheoryVisualizer extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 247);
    private static final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private static final Color SECONDARY_COLOR = new Color(142, 142, 147);
    private static final Color ACCENT_COLOR = new Color(255, 149, 0);

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private SetManagerWrapper setManager;
    private Canvas3D canvas3D;
    private BranchGroup scene;
    private TransformGroup viewTransformGroup;
    private double zoom = 1.0;
    private Point lastMousePosition;
    private boolean isDragging = false;
    private List<SetObject3D> setObjects = new ArrayList<>();
    private ScheduledExecutorService animationExecutor;

    public SetTheoryVisualizer() {
        setManager = new SetManagerWrapper();
        initUI();
        init3DScene();
        setupAnimations();
    }

    private void initUI() {
        setTitle("Set Theory Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel with card layout for different views
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create navigation bar
        JPanel navBar = createNavBar();

        // Create 3D view panel
        JPanel view3DPanel = create3DViewPanel();

        // Create menu view panel
        JPanel menuPanel = createMenuPanel();

        // Add panels to card layout
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(view3DPanel, "3dview");

        // Add navigation bar and main panel to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navBar, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Show menu by default
        cardLayout.show(mainPanel, "menu");
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(22, 22, 24));
        navBar.setPreferredSize(new Dimension(getWidth(), 60));
        navBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // App logo/title
        JLabel titleLabel = new JLabel("Set Theory");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton menuButton = createIconButton("☰", 24, Color.WHITE);
        menuButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JButton view3DButton = createIconButton("✷", 24, Color.WHITE);
        view3DButton.addActionListener(e -> cardLayout.show(mainPanel, "3dview"));

        buttonPanel.add(menuButton);
        buttonPanel.add(view3DButton);

        navBar.add(titleLabel, BorderLayout.WEST);
        navBar.add(buttonPanel, BorderLayout.EAST);

        return navBar;
    }

    private JButton createIconButton(String icon, int size, Color color) {
        JButton button = new JButton(icon);
        button.setFont(new Font("SF Pro Display", Font.PLAIN, size));
        button.setForeground(color);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(PRIMARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(color);
            }
        });

        return button;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(40, 60, 60, 60));

        // Header
        JLabel headerLabel = new JLabel("Set Theory Operations");
        headerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 36));
        headerLabel.setForeground(new Color(28, 28, 30));
        headerLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Grid of operation cards
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 30, 30));
        gridPanel.setOpaque(false);

        // Create operation cards
        OperationCard universalCard = new OperationCard("Universal Set", "U", "View and edit the universal set");
        universalCard.addActionListener(e -> showUniversalSet());

        OperationCard membershipCard = new OperationCard("Membership", "∈", "Verify element membership");
        membershipCard.addActionListener(e -> verifyMembership());

        OperationCard subsetsCard = new OperationCard("Subsets", "⊆", "Classify and analyze subsets");
        subsetsCard.addActionListener(e -> classifySubsets());

        OperationCard powerSetCard = new OperationCard("Power Set", "P(U)", "Explore the power set");
        powerSetCard.addActionListener(e -> showPowerSet());

        OperationCard operationsCard = new OperationCard("Operations", "∪∩Δ", "Perform set operations");
        operationsCard.addActionListener(e -> performSetOperations());

        OperationCard lawsCard = new OperationCard("Set Laws", "≡", "Demonstrate set theory laws");
        lawsCard.addActionListener(e -> demonstrateSetLaws());

        gridPanel.add(universalCard);
        gridPanel.add(membershipCard);
        gridPanel.add(subsetsCard);
        gridPanel.add(powerSetCard);
        gridPanel.add(operationsCard);
        gridPanel.add(lawsCard);

        // Add components to menu panel
        menuPanel.add(headerLabel, BorderLayout.NORTH);
        menuPanel.add(gridPanel, BorderLayout.CENTER);

        return menuPanel;
    }

    private JPanel create3DViewPanel() {
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBackground(BACKGROUND_COLOR);

        // Create 3D canvas
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas3D = new Canvas3D(config);
        canvas3D.setSize(800, 600);
        canvas3D.setPreferredSize(new Dimension(800, 600));

        // Add mouse listeners for interaction
        add3DInteractionListeners();

        // Add canvas to panel
        viewPanel.add(canvas3D, BorderLayout.CENTER);

        // Add control panel
        viewPanel.add(create3DControlPanel(), BorderLayout.SOUTH);

        return viewPanel;
    }

    private JPanel create3DControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(22, 22, 24));
        controlPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Zoom controls
        JButton zoomInButton = createIconButton("+", 20, Color.WHITE);
        zoomInButton.addActionListener(e -> zoom(1.1));

        JButton zoomOutButton = createIconButton("-", 20, Color.WHITE);
        zoomOutButton.addActionListener(e -> zoom(0.9));

        // Reset view button
        JButton resetButton = createIconButton("↻", 20, Color.WHITE);
        resetButton.addActionListener(e -> resetView());

        // Animation toggle
        JToggleButton animateButton = new JToggleButton("Animate");
        styleButton(animateButton);
        animateButton.addActionListener(e -> toggleAnimation(animateButton.isSelected()));

        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(resetButton);
        controlPanel.add(Box.createHorizontalStrut(40));
        controlPanel.add(animateButton);

        return controlPanel;
    }

    private void styleButton(AbstractButton button) {
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 44, 46));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void add3DInteractionListeners() {
        canvas3D.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint();
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });

        canvas3D.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && viewTransformGroup != null) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;

                    Transform3D rotation = new Transform3D();
                    viewTransformGroup.getTransform(rotation);

                    // Create rotation transforms
                    Transform3D rotX = new Transform3D();
                    rotX.rotX(dy * 0.01);

                    Transform3D rotY = new Transform3D();
                    rotY.rotY(dx * 0.01);

                    // Combine rotations
                    rotation.mul(rotX);
                    rotation.mul(rotY);

                    viewTransformGroup.setTransform(rotation);
                    lastMousePosition = e.getPoint();
                }
            }
        });

        canvas3D.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            zoom(notches > 0 ? 0.9 : 1.1);
        });
    }

    private void init3DScene() {
        // Create the universe
        SimpleUniverse universe = new SimpleUniverse(canvas3D);

        // Configure viewing platform
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();
        viewingPlatform.setNominalViewingTransform();

        // Get the view transform group for rotation control
        viewTransformGroup = viewingPlatform.getViewPlatformTransform();

        // Create the scene graph root
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_DETACH);

        // Add lighting
        addLighting(scene);

        // Add a background
        addBackground(scene);

        // Add initial set objects
        createInitialSetObjects();

        // Compile and attach the scene
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private void addLighting(BranchGroup scene) {
        // Set up the lighting
        Color3f ambientColor = new Color3f(0.2f, 0.2f, 0.2f);
        AmbientLight ambientLight = new AmbientLight(ambientColor);
        ambientLight.setInfluencingBounds(new BoundingSphere(100.0));
        scene.addChild(ambientLight);
        
        Color3f directionalColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f lightDirection = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight directionalLight = new DirectionalLight(directionalColor, lightDirection);
        directionalLight.setInfluencingBounds(new BoundingSphere(100.0));
        scene.addChild(directionalLight);
    }

    private void addBackground(BranchGroup scene) {
        // Create a gradient background
        Background background = new Background();
        background.setApplicationBounds(new BoundingSphere(100.0));
        
        Color3f[] colors = {
            new Color3f(0.05f, 0.05f, 0.1f),
            new Color3f(0.1f, 0.1f, 0.2f)
        };
        background.setColor(0, colors[0]);
        background.setColor(1, colors[1]);
        
        scene.addChild(background);
    }

    private void createInitialSetObjects() {
        // Clear existing objects
        setObjects.clear();
        if (scene != null) {
            scene.detach();
        }
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_DETACH);

        // Add lighting and background again
        addLighting(scene);
        addBackground(scene);

        // Create a universal set sphere
        SetObject3D universalSetObj = new SetObject3D("Universal Set", new Color3f(0.1f, 0.3f, 0.8f), 2.0f);
        universalSetObj.setPosition(0, 0, 0);
        scene.addChild(universalSetObj.getTransformGroup());
        setObjects.add(universalSetObj);

        // Create some subset spheres
        SetObject3D subsetA = new SetObject3D("Subset A", new Color3f(0.8f, 0.2f, 0.2f), 1.2f);
        subsetA.setPosition(-1, 0.5, 0);
        scene.addChild(subsetA.getTransformGroup());
        setObjects.add(subsetA);

        SetObject3D subsetB = new SetObject3D("Subset B", new Color3f(0.2f, 0.8f, 0.2f), 1.2f);
        subsetB.setPosition(1, 0.5, 0);
        scene.addChild(subsetB.getTransformGroup());
        setObjects.add(subsetB);

        // Create an intersection object
        SetObject3D intersection = new SetObject3D("A ∩ B", new Color3f(0.8f, 0.8f, 0.2f), 0.8f);
        intersection.setPosition(0, 0.5, 0);
        scene.addChild(intersection.getTransformGroup());
        setObjects.add(intersection);

        // Compile the scene
        scene.compile();

        // Add to universe if canvas exists
        if (canvas3D != null) {
            SimpleUniverse universe = (SimpleUniverse) canvas3D.getParent().getParent();
            universe.addBranchGraph(scene);
        }
    }

    private void setupAnimations() {
        animationExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    private void toggleAnimation(boolean enabled) {
        if (enabled) {
            animationExecutor.scheduleAtFixedRate(this::animateSets, 0, 50, TimeUnit.MILLISECONDS);
        } else {
            animationExecutor.shutdownNow();
            animationExecutor = Executors.newSingleThreadScheduledExecutor();
        }
    }

    private void animateSets() {
        for (SetObject3D setObj : setObjects) {
            // Random small movements
            double dx = (Math.random() - 0.5) * 0.05;
            double dy = (Math.random() - 0.5) * 0.05;
            double dz = (Math.random() - 0.5) * 0.05;

            setObj.translate(dx, dy, dz);

            // Small rotations
            setObj.rotate(0.01, 0.01, 0.01);
        }
    }

    private void zoom(double factor) {
        if (viewTransformGroup != null) {
            zoom *= factor;
            Transform3D transform = new Transform3D();
            viewTransformGroup.getTransform(transform);

            Vector3d translation = new Vector3d();
            transform.get(translation);

            transform.setScale(new Vector3d(zoom, zoom, zoom));
            transform.setTranslation(translation);

            viewTransformGroup.setTransform(transform);
        }
    }

    private void resetView() {
        if (viewTransformGroup != null) {
            zoom = 1.0;
            Transform3D transform = new Transform3D();
            transform.setIdentity();
            viewTransformGroup.setTransform(transform);
        }
    }

    // Set operation methods that integrate with the original SetManager
    private void showUniversalSet() {
        // Create a dialog to show the universal set
        JDialog dialog = createOperationDialog("Universal Set");
        JTextArea textArea = new JTextArea(setManager.getUniversalSet().toString());
        textArea.setEditable(false);
        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);

        // Visualize in 3D
        visualizeUniversalSet();
    }

    private void verifyMembership() {
        JDialog dialog = createOperationDialog("Membership Verification");
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField elementField = new JTextField();
        JComboBox<String> setCombo = new JComboBox<>(new String[] { "Universal Set", "Subset A", "Subset B" });

        inputPanel.add(new JLabel("Element:"));
        inputPanel.add(elementField);
        inputPanel.add(new JLabel("Set:"));
        inputPanel.add(setCombo);

        // Result area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Verify button
        JButton verifyButton = new JButton("Verify");
        styleButton(verifyButton);
        verifyButton.addActionListener(e -> {
            String element = elementField.getText();
            String selectedSet = (String) setCombo.getSelectedItem();
            resultArea.setText("Element '" + element + "' " +
                    (selectedSet.equals("Universal Set") ? "∈" : "∉") + " " + selectedSet);
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(verifyButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void classifySubsets() {
        JDialog dialog = createOperationDialog("Subset Classification");
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        // Get subset classification from SetManager
        Map<String, String> classification = setManager.classifyAllSubsets();
        StringBuilder sb = new StringBuilder();
        classification.forEach((name, type) -> sb.append(name).append(": ").append(type).append("\n"));
        textArea.setText(sb.toString());

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);

        // Highlight subsets in 3D view
        highlightSubsets();
    }

    private void showPowerSet() {
        JDialog dialog = createOperationDialog("Power Set");
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        // Get power set from SetManager
        Set<Set<Object>> powerSet = setManager.getPowerSet();
        StringBuilder sb = new StringBuilder("P(U) = {\n");
        for (Set<Object> subset : powerSet) {
            sb.append("  ").append(subset).append(",\n");
        }
        if (!powerSet.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length()); // Remove last comma
        }
        sb.append("\n}");
        textArea.setText(sb.toString());

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);

        // Visualize power set in 3D
        visualizePowerSet();
    }

    private void performSetOperations() {
        JDialog dialog = createOperationDialog("Set Operations");
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Operation selection
        JPanel operationPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        ButtonGroup opGroup = new ButtonGroup();

        JRadioButton unionButton = new JRadioButton("Union (A ∪ B)");
        JRadioButton intersectionButton = new JRadioButton("Intersection (A ∩ B)");
        JRadioButton differenceButton = new JRadioButton("Difference (A - B)");
        JRadioButton symDiffButton = new JRadioButton("Symmetric Difference (A Δ B)");

        opGroup.add(unionButton);
        opGroup.add(intersectionButton);
        opGroup.add(differenceButton);
        opGroup.add(symDiffButton);

        operationPanel.add(unionButton);
        operationPanel.add(intersectionButton);
        operationPanel.add(differenceButton);
        operationPanel.add(symDiffButton);

        // Set selection
        JPanel setPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JCheckBox setACheck = new JCheckBox("Subset A");
        JCheckBox setBCheck = new JCheckBox("Subset B");
        setPanel.add(setACheck);
        setPanel.add(setBCheck);

        // Result area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Execute button
        JButton executeButton = new JButton("Execute");
        styleButton(executeButton);
        executeButton.addActionListener(e -> {
            String operation = "";
            Set<Object> result = new HashSet<>();

            List<Set<Object>> selectedSets = new ArrayList<>();
            if (setACheck.isSelected())
                selectedSets.add(setManager.getSubsets().get(0));
            if (setBCheck.isSelected())
                selectedSets.add(setManager.getSubsets().get(1));

            if (unionButton.isSelected()) {
                operation = "Union";
                result = setManager.performUnion(selectedSets);
            } else if (intersectionButton.isSelected()) {
                operation = "Intersection";
                result = setManager.performIntersection(selectedSets);
            } else if (differenceButton.isSelected()) {
                operation = "Difference";
                result = setManager.performDifference(selectedSets);
            } else if (symDiffButton.isSelected() && selectedSets.size() == 2) {
                operation = "Symmetric Difference";
                result = setManager.performSymmetricDifference(selectedSets.get(0), selectedSets.get(1));
            }

            resultArea.setText("Result of " + operation + ":\n" + result);

            // Visualize the operation in 3D
            visualizeSetOperation(operation);
        });

        panel.add(operationPanel, BorderLayout.NORTH);
        panel.add(setPanel, BorderLayout.CENTER);
        panel.add(executeButton, BorderLayout.SOUTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.EAST);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void demonstrateSetLaws() {
        JDialog dialog = createOperationDialog("Set Theory Laws");
        JTabbedPane tabbedPane = new JTabbedPane();

        List<Set<Object>> subsets = setManager.getSubsets();
        if (subsets.size() < 3) {
            JOptionPane.showMessageDialog(this, "Need at least 3 subsets to demonstrate all laws", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Set<Object> A = subsets.get(0);
        Set<Object> B = subsets.get(1);
        Set<Object> C = subsets.get(2);

        // Commutative Law
        JPanel commutativePanel = new JPanel(new BorderLayout());
        commutativePanel.add(new JLabel("<html><h2>Commutative Law</h2><pre>" +
                setManager.demonstrateLaw("Commutative", A, B, C) + "</pre></html>"), BorderLayout.CENTER);
        tabbedPane.addTab("Commutative", commutativePanel);

        // Associative Law
        JPanel associativePanel = new JPanel(new BorderLayout());
        associativePanel.add(new JLabel("<html><h2>Associative Law</h2><pre>" +
                setManager.demonstrateLaw("Associative", A, B, C) + "</pre></html>"), BorderLayout.CENTER);
        tabbedPane.addTab("Associative", associativePanel);

        // Distributive Law
        JPanel distributivePanel = new JPanel(new BorderLayout());
        distributivePanel.add(new JLabel("<html><h2>Distributive Law</h2><pre>" +
                setManager.demonstrateLaw("Distributive", A, B, C) + "</pre></html>"), BorderLayout.CENTER);
        tabbedPane.addTab("Distributive", distributivePanel);

        // De Morgan's Law
        JPanel deMorganPanel = new JPanel(new BorderLayout());
        deMorganPanel.add(new JLabel("<html><h2>De Morgan's Laws</h2><pre>" +
                setManager.demonstrateLaw("De Morgan", A, B, C) + "</pre></html>"), BorderLayout.CENTER);
        tabbedPane.addTab("De Morgan", deMorganPanel);

        dialog.add(tabbedPane);
        dialog.setVisible(true);

        // Visualize laws in 3D
        visualizeSetLaws();
    }

    private JDialog createOperationDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        return dialog;
    }

    // 3D Visualization methods
    private void visualizeUniversalSet() {
        // Highlight the universal set in 3D
        for (SetObject3D setObj : setObjects) {
            if (setObj.getName().equals("Universal Set")) {
                setObj.highlight(true);
            } else {
                setObj.highlight(false);
            }
        }

        // Switch to 3D view
        cardLayout.show(mainPanel, "3dview");
    }

    private void highlightSubsets() {
        // Highlight all subsets
        for (SetObject3D setObj : setObjects) {
            if (!setObj.getName().equals("Universal Set")) {
                setObj.highlight(true);
            } else {
                setObj.highlight(false);
            }
        }

        // Switch to 3D view
        cardLayout.show(mainPanel, "3dview");
    }

    private void visualizePowerSet() {
        // Create multiple smaller spheres representing power set elements
        scene.detach();
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_DETACH);

        addLighting(scene);
        addBackground(scene);

        // Create a grid of spheres
        Set<Set<Object>> powerSet = setManager.getPowerSet();
        int count = powerSet.size();
        int cols = (int) Math.ceil(Math.sqrt(count));
        double spacing = 2.0;
        int i = 0;

        for (Set<Object> subset : powerSet) {
            int row = i / cols;
            int col = i % cols;

            SetObject3D powerSetObj = new SetObject3D("PowerSet-" + i,
                    new Color3f(0.5f, 0.8f, 1.0f), 0.5f);
            powerSetObj.setPosition((col - cols / 2.0) * spacing, (cols / 2.0 - row) * spacing, 0);
            scene.addChild(powerSetObj.getTransformGroup());
            setObjects.add(powerSetObj);
            i++;
        }

        scene.compile();
        SimpleUniverse universe = (SimpleUniverse) canvas3D.getParent().getParent();
        universe.addBranchGraph(scene);

        // Switch to 3D view
        cardLayout.show(mainPanel, "3dview");
    }

    private void visualizeSetOperation(String operation) {
        // Highlight the appropriate sets based on operation
        for (SetObject3D setObj : setObjects) {
            if (operation.equals("Union") &&
                    (setObj.getName().equals("Subset A") || setObj.getName().equals("Subset B"))) {
                setObj.highlight(true);
            } else if (operation.equals("Intersection") &&
                    setObj.getName().equals("A ∩ B")) {
                setObj.highlight(true);
            } else if (operation.equals("Difference") &&
                    setObj.getName().equals("Subset A")) {
                setObj.highlight(true);
            } else if (operation.equals("Symmetric Difference") &&
                    (setObj.getName().equals("Subset A") || setObj.getName().equals("Subset B"))) {
                setObj.highlight(true);
            } else {
                setObj.highlight(false);
            }
        }

        // Switch to 3D view
        cardLayout.show(mainPanel, "3dview");
    }

    private void visualizeSetLaws() {
        // Create a more complex visualization for set laws
        scene.detach();
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_DETACH);

        addLighting(scene);
        addBackground(scene);

        // Create sets A, B, C for law demonstration
        SetObject3D setA = new SetObject3D("Set A", new Color3f(0.8f, 0.2f, 0.2f), 1.0f);
        setA.setPosition(-2, 0, 0);
        scene.addChild(setA.getTransformGroup());

        SetObject3D setB = new SetObject3D("Set B", new Color3f(0.2f, 0.8f, 0.2f), 1.0f);
        setB.setPosition(0, 0, 0);
        scene.addChild(setB.getTransformGroup());

        SetObject3D setC = new SetObject3D("Set C", new Color3f(0.2f, 0.2f, 0.8f), 1.0f);
        setC.setPosition(2, 0, 0);
        scene.addChild(setC.getTransformGroup());

        // Create operation results
        SetObject3D unionAB = new SetObject3D("A ∪ B", new Color3f(0.8f, 0.5f, 0.2f), 1.2f);
        unionAB.setPosition(-1, -2, 0);
        scene.addChild(unionAB.getTransformGroup());

        SetObject3D interAB = new SetObject3D("A ∩ B", new Color3f(0.5f, 0.8f, 0.2f), 0.8f);
        interAB.setPosition(1, -2, 0);
        scene.addChild(interAB.getTransformGroup());

        scene.compile();
        SimpleUniverse universe = (SimpleUniverse) canvas3D.getParent().getParent();
        universe.addBranchGraph(scene);

        // Switch to 3D view
        cardLayout.show(mainPanel, "3dview");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and show the GUI
                SetTheoryVisualizer visualizer = new SetTheoryVisualizer();
                visualizer.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Custom 3D set object class
    private class SetObject3D {
        private String name;
        private TransformGroup transformGroup;
        private Sphere sphere;
        private Color3f color;
        private float radius;
        private Point3d position;

        public SetObject3D(String name, Color3f color, float radius) {
            this.name = name;
            this.color = color;
            this.radius = radius;
            this.position = new Point3d(0, 0, 0);

            // Create appearance
            Appearance appearance = new Appearance();
            Material material = new Material();
            material.setDiffuseColor(color);
            material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
            material.setShininess(64.0f);
            appearance.setMaterial(material);

            // Create sphere
            sphere = new Sphere(radius, Sphere.GENERATE_NORMALS, 50, appearance);

            // Create transform group
            transformGroup = new TransformGroup();
            transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transformGroup.addChild(sphere);
        }

        public void setPosition(double x, double y, double z) {
            position.set(x, y, z);
            updateTransform();
        }

        public void translate(double dx, double dy, double dz) {
            position.x += dx;
            position.y += dy;
            position.z += dz;
            updateTransform();
        }

        public void rotate(double rx, double ry, double rz) {
            Transform3D transform = new Transform3D();
            transformGroup.getTransform(transform);

            Transform3D rotation = new Transform3D();
            rotation.rotX(rx);
            transform.mul(rotation);

            rotation.rotY(ry);
            transform.mul(rotation);

            rotation.rotZ(rz);
            transform.mul(rotation);

            transformGroup.setTransform(transform);
        }

        private void updateTransform() {
            Transform3D transform = new Transform3D();
            transform.setTranslation(new Vector3d(position));
            transformGroup.setTransform(transform);
        }

        public void highlight(boolean highlight) {
            Appearance appearance = sphere.getAppearance();
            Material material = appearance.getMaterial();

            if (highlight) {
                material.setEmissiveColor(new Color3f(0.2f, 0.2f, 0.2f));
                material.setDiffuseColor(new Color3f(
                        Math.min(1.0f, color.x + 0.3f),
                        Math.min(1.0f, color.y + 0.3f),
                        Math.min(1.0f, color.z + 0.3f)));
            } else {
                material.setEmissiveColor(new Color3f(0.0f, 0.0f, 0.0f));
                material.setDiffuseColor(color);
            }
        }

        public String getName() {
            return name;
        }

        public TransformGroup getTransformGroup() {
            return transformGroup;
        }
    }

    // Custom operation card component
    private class OperationCard extends JButton {
        private String title;
        private String symbol;
        private String description;

        public OperationCard(String title, String symbol, String description) {
            this.title = title;
            this.symbol = symbol;
            this.description = description;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setBackground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(240, 240, 245));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                }
            });

            // Create components
            JLabel symbolLabel = new JLabel(symbol, SwingConstants.CENTER);
            symbolLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 48));
            symbolLabel.setForeground(PRIMARY_COLOR);

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
            titleLabel.setForeground(new Color(28, 28, 30));

            JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
            descLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            descLabel.setForeground(SECONDARY_COLOR);

            // Add to card
            add(symbolLabel, BorderLayout.NORTH);
            add(titleLabel, BorderLayout.CENTER);
            add(descLabel, BorderLayout.SOUTH);
        }
    }
}

class SetManagerWrapper {
    private SetManager setManager;

    public SetManagerWrapper() {
        this.setManager = new SetManager();
        initializeDemoSets();
    }

    private void initializeDemoSets() {
        // Create a universal set with some elements
        Set<Object> universal = new HashSet<>();
        universal.add("a");
        universal.add("b");
        universal.add("c");
        universal.add(1);
        universal.add(2);

        // Create some subsets
        Set<Object> subsetA = new HashSet<>();
        subsetA.add("a");
        subsetA.add("b");

        Set<Object> subsetB = new HashSet<>();
        subsetB.add("b");
        subsetB.add("c");
        subsetB.add(1);

        // Use reflection to set the private fields
        try {
            Field universalField = SetManager.class.getDeclaredField("universalSet");
            universalField.setAccessible(true);
            universalField.set(setManager, universal);

            Field subsetsField = SetManager.class.getDeclaredField("subsets");
            subsetsField.setAccessible(true);
            List<Set<Object>> subsets = new ArrayList<>();
            subsets.add(subsetA);
            subsets.add(subsetB);
            subsetsField.set(setManager, subsets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Object> getUniversalSet() {
        return setManager.getUniversalSet();
    }

    public List<Set<Object>> getSubsets() {
        try {
            Field subsetsField = SetManager.class.getDeclaredField("subsets");
            subsetsField.setAccessible(true);
            return (List<Set<Object>>) subsetsField.get(setManager);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Set<Object> performUnion(List<Set<Object>> sets) {
        Set<Object> result = new HashSet<>();
        sets.forEach(result::addAll);
        return result;
    }

    public Set<Object> performIntersection(List<Set<Object>> sets) {
        if (sets.size() < 2)
            return new HashSet<>();

        Set<Object> result = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            result.retainAll(sets.get(i));
        }
        return result;
    }

    public Set<Object> performDifference(List<Set<Object>> sets) {
        if (sets.size() < 2)
            return new HashSet<>();

        Set<Object> result = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            result.removeAll(sets.get(i));
        }
        return result;
    }

    public Set<Object> performSymmetricDifference(Set<Object> a, Set<Object> b) {
        Set<Object> union = new HashSet<>(a);
        union.addAll(b);

        Set<Object> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<Object> result = new HashSet<>(union);
        result.removeAll(intersection);

        return result;
    }

    public Set<Object> getComplement(Set<Object> set) {
        Set<Object> universal = getUniversalSet();
        Set<Object> result = new HashSet<>(universal);
        result.removeAll(set);
        return result;
    }

    public Set<Set<Object>> getPowerSet() {
        return getPowerSet(getUniversalSet());
    }

    private Set<Set<Object>> getPowerSet(Set<Object> originalSet) {
        Set<Set<Object>> powerSet = new HashSet<>();
        powerSet.add(new HashSet<>());

        for (Object element : originalSet) {
            Set<Set<Object>> newSubsets = new HashSet<>();
            for (Set<Object> subset : powerSet) {
                Set<Object> newSubset = new HashSet<>(subset);
                newSubset.add(element);
                newSubsets.add(newSubset);
            }
            powerSet.addAll(newSubsets);
        }
        return powerSet;
    }

    public Map<String, Boolean> verifyMembership(Object element) {
        Map<String, Boolean> membership = new LinkedHashMap<>();
        Set<Object> universal = getUniversalSet();
        List<Set<Object>> subsets = getSubsets();

        membership.put("Universal Set", universal.contains(element));

        char setName = 'A';
        for (Set<Object> subset : subsets) {
            membership.put("Subset " + setName, subset.contains(element));
            setName++;
        }

        return membership;
    }

    public Map<String, String> classifyAllSubsets() {
        Map<String, String> classification = new LinkedHashMap<>();
        Set<Object> universal = getUniversalSet();
        List<Set<Object>> subsets = getSubsets();

        char setName = 'A';
        for (Set<Object> subset : subsets) {
            if (universal.containsAll(subset)) {
                String type = subset.size() < universal.size() ? "⊂ U (Subconjunto propio)"
                        : "⊆ U (Subconjunto impropio)";
                classification.put("Subset " + setName, type);
            } else {
                classification.put("Subset " + setName, "⊈ U (No es subconjunto)");
            }
            setName++;
        }

        return classification;
    }

    public String demonstrateLaw(String lawName, Set<Object> a, Set<Object> b, Set<Object> c) {
        switch (lawName) {
            case "Commutative":
                Set<Object> unionAB = performUnion(Arrays.asList(a, b));
                Set<Object> unionBA = performUnion(Arrays.asList(b, a));
                boolean unionCommutative = unionAB.equals(unionBA);

                Set<Object> interAB = performIntersection(Arrays.asList(a, b));
                Set<Object> interBA = performIntersection(Arrays.asList(b, a));
                boolean interCommutative = interAB.equals(interBA);

                return "Ley Conmutativa:\n" +
                        "A ∪ B = B ∪ A: " + (unionCommutative ? "✅" : "❌") + "\n" +
                        "A ∩ B = B ∩ A: " + (interCommutative ? "✅" : "❌");

            case "Associative":
                Set<Object> unionAB_C = performUnion(Arrays.asList(performUnion(Arrays.asList(a, b)), c));
                Set<Object> unionA_BC = performUnion(Arrays.asList(a, performUnion(Arrays.asList(b, c))));
                boolean unionAssociative = unionAB_C.equals(unionA_BC);

                Set<Object> interAB_C = performIntersection(Arrays.asList(performIntersection(Arrays.asList(a, b)), c));
                Set<Object> interA_BC = performIntersection(Arrays.asList(a, performIntersection(Arrays.asList(b, c))));
                boolean interAssociative = interAB_C.equals(interA_BC);

                return "Ley Asociativa:\n" +
                        "(A ∪ B) ∪ C = A ∪ (B ∪ C): " + (unionAssociative ? "✅" : "❌") + "\n" +
                        "(A ∩ B) ∩ C = A ∩ (B ∩ C): " + (interAssociative ? "✅" : "❌");

            case "Distributive":
                Set<Object> left1 = performUnion(Arrays.asList(a, performIntersection(Arrays.asList(b, c))));
                Set<Object> right1 = performIntersection(Arrays.asList(
                        performUnion(Arrays.asList(a, b)),
                        performUnion(Arrays.asList(a, c))));
                boolean distributive1 = left1.equals(right1);

                Set<Object> left2 = performIntersection(Arrays.asList(a, performUnion(Arrays.asList(b, c))));
                Set<Object> right2 = performUnion(Arrays.asList(
                        performIntersection(Arrays.asList(a, b)),
                        performIntersection(Arrays.asList(a, c))));
                boolean distributive2 = left2.equals(right2);

                return "Ley Distributiva:\n" +
                        "A ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C): " + (distributive1 ? "✅" : "❌") + "\n" +
                        "A ∩ (B ∪ C) = (A ∩ B) ∪ (A ∩ C): " + (distributive2 ? "✅" : "❌");

            case "De Morgan":
                Set<Object> complementUnion = getComplement(performUnion(Arrays.asList(a, b)));
                Set<Object> intersectionComplements = performIntersection(Arrays.asList(
                        getComplement(a), getComplement(b)));
                boolean deMorgan1 = complementUnion.equals(intersectionComplements);

                Set<Object> complementIntersection = getComplement(performIntersection(Arrays.asList(a, b)));
                Set<Object> unionComplements = performUnion(Arrays.asList(
                        getComplement(a), getComplement(b)));
                boolean deMorgan2 = complementIntersection.equals(unionComplements);

                return "Leyes de De Morgan:\n" +
                        "(A ∪ B)' = A' ∩ B': " + (deMorgan1 ? "✅" : "❌") + "\n" +
                        "(A ∩ B)' = A' ∪ B': " + (deMorgan2 ? "✅" : "❌");

            default:
                return "Ley no reconocida";
        }
    }
}