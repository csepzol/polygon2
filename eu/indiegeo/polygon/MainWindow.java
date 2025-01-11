/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import com.jogamp.common.util.awt.AWTEDTExecutor;
import com.jogamp.nativewindow.awt.AWTPrintLifecycle;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import datechooser.beans.DateChooserCombo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONException;


/**
 *
 * @author lenovo
 */
public class MainWindow extends javax.swing.JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {

    
/**
 * Sajat valtozok
 */
    CaveProject cp;
    static String title = "Polygon 2";
    static CaveRenderer cr = new CaveRenderer();
    static CaveOverview co = new CaveOverview();
    ButtonGroup mapButtonGroup = new ButtonGroup();
    ButtonGroup viewSettingButtonGroup = new ButtonGroup();
    boolean button1;
    boolean button3;
    int movePrevX, movePrevY;
    private boolean isNewSurvey;
    ButtonGroup viewGroup = new ButtonGroup();
    ButtonGroup lineWidthGroup = new ButtonGroup();
    ButtonGroup pointTextGroup = new ButtonGroup();
    Set<List<DefaultEdge>> cycles ;
    static SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
    int dpi = 1;
    CaveLoop cl;
    String[] surveyerJobs = {"Mérés","Rajzolás", "Műszer leolvasás", "Iránymérés", "Hosszmérés", "Jegyzőkönyv vezetés", "Figuráns", "Fúrás"};
    String[] lengthInst = {"Mérőszallag","Infra távolságmérő"};
    String[] compassInst = {"Függôkompasz", "Bányászkompasz", "Sunto","Iránytű", "Teodolit", "Lapp tájoló\n", "Rambo kés"};
    String[] inclinoInst = {"Fokív 0,5 fok beosztású", "Fokív 1 fok beosztású", "Fokív 2 fok beosztású", "Sounto"};
    Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    
/**
 * Sajat init
 *  Listeners
 */

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        this.setSize(r.width, r.height);
//        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
//        this.setUndecorated(true);
        caveMap.addMouseListener(this);
        caveMap.addMouseMotionListener(this);
        caveMap.addMouseWheelListener(this);
        caveOverview.addMouseListener(new OverviewMouseListener());
        caveOverview.addMouseMotionListener(new OverviewMouseListener());
        caveOverview.addMouseWheelListener(new OverviewMouseListener());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        surveyDatePicker.setDateFormat(dateFormat);
        dataTable.getTableHeader().setReorderingAllowed(false);
	dataTable.setAutoCreateRowSorter(true);
        coordinatesTable.getTableHeader().setReorderingAllowed(false);
        meridianTable.getTableHeader().setReorderingAllowed(false);
        viewGroup.add(basePlanRadioButton);
        viewGroup.add(sectionPlanRadioButton);
        viewGroup.add(modellRadioButton);
        modellRadioButton.setSelected(true);
        lineWidthGroup.add(lineWidth1ButtonMenu);
        lineWidthGroup.add(lineWidth2ButtonMenu);
        lineWidthGroup.add(lineWidth3ButtonMenu);
        lineWidth1ButtonMenu.setSelected(true);
        pointTextGroup.add(nonePointTextButtonMenu);
        pointTextGroup.add(depthPointTextButtonMenu);
        pointTextGroup.add(eovDepthPointTextButtonMenu);
        pointTextGroup.add(idPointTextButtonMenu);
        nonePointTextButtonMenu.setSelected(true);
        this.entranceViewCheckBoxMenu.setSelected(false);
        loopsInfoCheckBoxMenu.setSelected(false);
        this.tabbedPane.setEnabledAt(4, false);
        this.tabbedPane.setEnabledAt(5, false);
        dataTable.addKeyListener(new DataTableKeyListener());
        surveyPanel.addKeyListener(new SurveyPanelKeyListener());
        ListSelectionModel dataTableSelectionModel = dataTable.getSelectionModel();
        dataTableSelectionModel.addListSelectionListener(new CustomListSelectionHandler());
        UIManager.put("Table.focusCellHighlightBorder", new BorderUIResource(BorderFactory.createLineBorder(Color.red)));
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        dataTable.getColumnModel().getColumn(5).setPreferredWidth(500);
        this.initSurveyerTable();
        this.setPanelEnabled(surveyPanel, false);
        this.setPanelEnabled(dataPanel, false);
        dataTable.setEnabled(false);
        surveyerTable.setEnabled(false);
        coordinatesTable.setEnabled(false);
        
        this.setTitle(title);
    }

    public static JComboBox<String> getCompassComboBox() {
        return compassComboBox;
    }

    public void setCompassComboBox(JComboBox<String> compassComboBox) {
        this.compassComboBox = compassComboBox;
    }

    public static JTextField getCompassCorrectionField() {
        return compassCorrectionField;
    }

    public void setCompassCorrectionField(JTextField compassCorrectionField) {
        this.compassCorrectionField = compassCorrectionField;
    }

    public static JComboBox<String> getInclinoComboBox() {
        return inclinoComboBox;
    }

    public void setInclinoComboBox(JComboBox<String> inclinoComboBox) {
        this.inclinoComboBox = inclinoComboBox;
    }

    public static JTextField getInclinoCorrectionField() {
        return inclinoCorrectionField;
    }

    public void setInclinoCorrectionField(JTextField inclinoCorrectionField) {
        this.inclinoCorrectionField = inclinoCorrectionField;
    }

    public static JComboBox<String> getLengthComboBox() {
        return lengthComboBox;
    }

    public void setLengthComboBox(JComboBox<String> lengthComboBox) {
        this.lengthComboBox = lengthComboBox;
    }

    public static JTextField getLengthCorrectionField() {
        return lengthCorrectionField;
    }

    public void setLengthCorrectionField(JTextField lengthCorrectionField) {
        this.lengthCorrectionField = lengthCorrectionField;
    }
    
    private GLCapabilities createGLCapabilites() {
        
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setHardwareAccelerated(true);

        // try to enable 2x anti aliasing - should be supported on most hardware
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);
        
        return capabilities;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        cavesTree = new javax.swing.JTree();
        tabbedPane = new javax.swing.JTabbedPane();
        mapPanel = new javax.swing.JPanel();
        caveMap = new com.jogamp.opengl.awt.GLJPanel(createGLCapabilites());
        dataPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        coordinatesButton = new javax.swing.JButton();
        meridianButton = new javax.swing.JButton();
        saveTableDataButton = new javax.swing.JButton();
        newLineButton = new javax.swing.JButton();
        newColumnButton = new javax.swing.JButton();
        removeLineButton = new javax.swing.JButton();
        deleteColumn = new javax.swing.JButton();
        selectedLength = new javax.swing.JLabel();
        renameColumn = new javax.swing.JButton();
        surveyPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        projectNameField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        projectPlaceField = new javax.swing.JTextField();
        storeProjectDataButton = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        projectStartDate = new javax.swing.JLabel();
        projectEndDate = new javax.swing.JLabel();
        projectPointCount = new javax.swing.JLabel();
        projectVertical = new javax.swing.JLabel();
        projectAltitude = new javax.swing.JLabel();
        projectDepth = new javax.swing.JLabel();
        projectFullLength = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        projectCodeField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        surveyNameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        surveyDatePicker = new datechooser.beans.DateChooserCombo();
        jLabel5 = new javax.swing.JLabel();
        declinationField = new javax.swing.JTextField();
        getDeclinationButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        fixPointCheckbox = new javax.swing.JCheckBox();
        fixedPointIdField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        xField = new javax.swing.JTextField();
        yField = new javax.swing.JTextField();
        zField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        fixPointCommentField = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        surveyPointCount = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        surveyFullLength = new javax.swing.JLabel();
        saveSurveyDataButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        meridianInfoField = new javax.swing.JTextField();
        meridianInfoButton = new javax.swing.JButton();
        surveyerPanel = new javax.swing.JScrollPane();
        surveyerTable = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        surveyTeamField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lengthComboBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        compassComboBox = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        inclinoComboBox = new javax.swing.JComboBox<>();
        lengthCorrectionField = new javax.swing.JTextField();
        compassCorrectionField = new javax.swing.JTextField();
        inclinoCorrectionField = new javax.swing.JTextField();
        coordinatesPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        coordinatesTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        meridianPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        meridianTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        loopsPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        loopsNumber = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        loopSelectorComboBox = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        loopInfo = new javax.swing.JTextPane();
        calculateLoopErrorButton = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel6 = new javax.swing.JPanel();
        newCaveButton = new javax.swing.JButton();
        newSurveyButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        xRotField = new javax.swing.JTextField();
        yRotField = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        caveOverview = new com.jogamp.opengl.awt.GLJPanel(createGLCapabilites());
        openCaveButton = new javax.swing.JButton();
        saveCaveButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        messageHeader = new javax.swing.JLabel();
        actualPointName = new javax.swing.JTextField();
        actualPointData = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newCaveMenu = new javax.swing.JMenuItem();
        openCaveMenu = new javax.swing.JMenuItem();
        saveCaveMenu = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        printMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        dxfExportMenu = new javax.swing.JMenuItem();
        excelExportMenu = new javax.swing.JMenuItem();
        therionExportMenu = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        svxImportMenu = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        basePlanRadioButton = new javax.swing.JRadioButtonMenuItem();
        sectionPlanRadioButton = new javax.swing.JRadioButtonMenuItem();
        modellRadioButton = new javax.swing.JRadioButtonMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        lineWidth1ButtonMenu = new javax.swing.JRadioButtonMenuItem();
        lineWidth2ButtonMenu = new javax.swing.JRadioButtonMenuItem();
        lineWidth3ButtonMenu = new javax.swing.JRadioButtonMenuItem();
        jMenu8 = new javax.swing.JMenu();
        nonePointTextButtonMenu = new javax.swing.JRadioButtonMenuItem();
        idPointTextButtonMenu = new javax.swing.JRadioButtonMenuItem();
        depthPointTextButtonMenu = new javax.swing.JRadioButtonMenuItem();
        eovDepthPointTextButtonMenu = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        entranceViewCheckBoxMenu = new javax.swing.JCheckBoxMenuItem();
        jMenu9 = new javax.swing.JMenu();
        findLoopsMenuItem = new javax.swing.JMenuItem();
        loopsInfoCheckBoxMenu = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cavesTree.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        cavesTree.setSelectionModel(selectionModel);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Barlangok");
        cavesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        cavesTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cavesTreeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(cavesTree);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );

        tabbedPane.setToolTipText("");
        tabbedPane.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout caveMapLayout = new javax.swing.GroupLayout(caveMap);
        caveMap.setLayout(caveMapLayout);
        caveMapLayout.setHorizontalGroup(
            caveMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );
        caveMapLayout.setVerticalGroup(
            caveMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 862, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(caveMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(caveMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        caveMap.addGLEventListener( cr );

        tabbedPane.addTab("Térkép", mapPanel);

        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        dataTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "From", "To", "Length", "Azimuth", "Vertical", "Note"
            }
        ));
        dataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataTable.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(dataTable);
        ExcelAdapter myAd = new ExcelAdapter(dataTable);

        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Hossz(m):");

        coordinatesButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        coordinatesButton.setText("EOV/WGS84");
        coordinatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coordinatesButtonActionPerformed(evt);
            }
        });

        meridianButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        meridianButton.setText("Meridian konvergencia");
        meridianButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meridianButtonActionPerformed(evt);
            }
        });

        saveTableDataButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        saveTableDataButton.setText("Tárol");
        saveTableDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTableDataButtonActionPerformed(evt);
            }
        });

        newLineButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        newLineButton.setText("Új Sor");
        newLineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newLineButtonActionPerformed(evt);
            }
        });

        newColumnButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        newColumnButton.setText("Új oszlop");
        newColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newColumnButtonActionPerformed(evt);
            }
        });

        removeLineButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        removeLineButton.setText("Sor törlés");
        removeLineButton.setToolTipText("Kijelölt sorok törlése");
        removeLineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLineButtonActionPerformed(evt);
            }
        });

        deleteColumn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        deleteColumn.setText("Oszlop törlés");
        deleteColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteColumnActionPerformed(evt);
            }
        });

        selectedLength.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        selectedLength.setText("-");

        renameColumn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        renameColumn.setText("Oszlop átnevezés");
        renameColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameColumnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedLength, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(coordinatesButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meridianButton)
                        .addGap(18, 18, 18)
                        .addComponent(newColumnButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newLineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveTableDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(renameColumn)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(deleteColumn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeLineButton)))
                        .addGap(25, 25, 25)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coordinatesButton)
                    .addComponent(meridianButton)
                    .addComponent(saveTableDataButton)
                    .addComponent(newLineButton)
                    .addComponent(newColumnButton)
                    .addComponent(selectedLength))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeLineButton)
                    .addComponent(deleteColumn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(renameColumn)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Mérési adatok", dataPanel);

        surveyPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Barlang adatai", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Barlang neve ");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setText("Terület");

        storeProjectDataButton.setText("Tárol");
        storeProjectDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storeProjectDataButtonActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel32.setText("Információk");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel33.setText("Első felmérés dátuma:");

        projectStartDate.setText("-");

        projectEndDate.setText("-");

        projectPointCount.setText("-");

        projectVertical.setText("-");

        projectAltitude.setText("-");

        projectDepth.setText("-");

        projectFullLength.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        projectFullLength.setText("-");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel40.setText("Összhossz");

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel39.setText("Mélység:");

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel38.setText("Magasság:");

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel37.setText("Vertikális kiterjedés:");

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel36.setText("Összes pontok száma:");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel35.setText("Utolsó felmérés dátuma:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Kataszteri szám");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel21))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(projectPlaceField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(projectNameField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 297, Short.MAX_VALUE)
                        .addComponent(storeProjectDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(projectStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectEndDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectPointCount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectVertical, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectAltitude, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectDepth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(projectFullLength, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel32)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(projectCodeField)
                                .addGap(22, 22, 22)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(projectNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(storeProjectDataButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(projectPlaceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(projectCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(projectStartDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(projectEndDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(projectPointCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(projectVertical))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(projectAltitude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(projectDepth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(projectFullLength))
                .addGap(0, 40, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Felmérés adatai", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Felmérés neve");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Felmérés időpontja");

        surveyDatePicker.setLocale(new java.util.Locale("hu", "", ""));
        surveyDatePicker.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Deklináció");

        declinationField.setText("0");

        getDeclinationButton.setText("Deklináció meghatározása");
        getDeclinationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDeclinationButtonActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Fixpont");

        fixPointCheckbox.setSelected(true);
        fixPointCheckbox.setText("Van fixpont");
        fixPointCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixPointCheckboxActionPerformed(evt);
            }
        });

        fixedPointIdField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        fixedPointIdField.setText("0");
        fixedPointIdField.setToolTipText("");
        fixedPointIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedPointIdFieldActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Fixpont azonosítója");

        xField.setText("0");

        yField.setText("0");

        zField.setText("0");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("EOV X");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("EOV Y");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("EOV Z");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel34.setText("Fixpont megjegyzés: ");

        fixPointCommentField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixPointCommentFieldActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel29.setText("Információk");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setText("Pontok száma:");

        surveyPointCount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        surveyPointCount.setText("-");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setText("Összhossz:");

        surveyFullLength.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        surveyFullLength.setText("-");

        saveSurveyDataButton.setText("Tárol");
        saveSurveyDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSurveyDataButtonActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Meridián konvergencia");

        meridianInfoField.setEditable(false);

        meridianInfoButton.setText("Meridián konv. meghat.");
        meridianInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meridianInfoButtonActionPerformed(evt);
            }
        });

        surveyerTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        surveyerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Felmérő személy", "Feladata"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        surveyerPanel.setViewportView(surveyerTable);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Felmérő csapat: ");

        surveyTeamField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        surveyTeamField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surveyTeamFieldActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Hosszmérés:");

        lengthComboBox.setEditable(true);
        lengthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mérőszallag", "Infra távolságmérő" }));
        lengthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lengthComboBoxActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Iránymérés:");

        compassComboBox.setEditable(true);
        compassComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Függôkompasz", "Bányászkompasz", "Sunto", "Iránytu", "Teodolit", "Lapp tájoló", "Rambo kés" }));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Lejtmérés:");

        inclinoComboBox.setEditable(true);
        inclinoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fokív 0,5 fok beosztású", "Fokív 1 fok beosztású", "Fokív 2 fok beosztású", "Sounto" }));

        lengthCorrectionField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lengthCorrectionField.setText("0");

        compassCorrectionField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        compassCorrectionField.setText("0");

        inclinoCorrectionField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        inclinoCorrectionField.setText("0");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(surveyNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 317, Short.MAX_VALUE)
                                .addComponent(saveSurveyDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(declinationField)
                                    .addComponent(surveyDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getDeclinationButton))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 630, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel34)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(fixPointCommentField))
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16)
                                        .addComponent(jLabel6))
                                    .addGap(16, 16, 16)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(fixedPointIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fixPointCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                            .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(zField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(meridianInfoField, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(meridianInfoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(surveyFullLength, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(surveyPointCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(surveyTeamField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(surveyerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(18, 18, 18)
                                        .addComponent(inclinoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                            .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addGap(13, 13, 13)))
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(compassComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lengthComboBox, 0, 120, Short.MAX_VALUE))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lengthCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(inclinoCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(compassCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(surveyNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveSurveyDataButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(surveyDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(declinationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(getDeclinationButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(meridianInfoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(meridianInfoButton))
                .addGap(23, 23, 23)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fixPointCheckbox)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(fixedPointIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(fixPointCommentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(surveyPointCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(surveyFullLength))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(surveyTeamField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(surveyerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lengthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lengthCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(compassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compassCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(inclinoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inclinoCorrectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout surveyPanelLayout = new javax.swing.GroupLayout(surveyPanel);
        surveyPanel.setLayout(surveyPanelLayout);
        surveyPanelLayout.setHorizontalGroup(
            surveyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(surveyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        surveyPanelLayout.setVerticalGroup(
            surveyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 558, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Felmérés adatai", surveyPanel);

        coordinatesPanel.setToolTipText("Eov és WGS84 koordináták");

        coordinatesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Pont azonosító", "x", "y", "z", "eov x", "eovy", "eov z", "Latitude", "Longitude", "Altitude"
            }
        ));
        jScrollPane4.setViewportView(coordinatesTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout coordinatesPanelLayout = new javax.swing.GroupLayout(coordinatesPanel);
        coordinatesPanel.setLayout(coordinatesPanelLayout);
        coordinatesPanelLayout.setHorizontalGroup(
            coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coordinatesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        coordinatesPanelLayout.setVerticalGroup(
            coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coordinatesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedPane.addTab("Koordináták", coordinatesPanel);

        meridianTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "From", "To", "Eredeti irányszög", "Módosított irányszög"
            }
        ));
        jScrollPane5.setViewportView(meridianTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout meridianPanelLayout = new javax.swing.GroupLayout(meridianPanel);
        meridianPanel.setLayout(meridianPanelLayout);
        meridianPanelLayout.setHorizontalGroup(
            meridianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(meridianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(meridianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(meridianPanelLayout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 605, Short.MAX_VALUE)))
                .addContainerGap())
        );
        meridianPanelLayout.setVerticalGroup(
            meridianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(meridianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedPane.addTab("Meridián konvergencia", meridianPanel);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Hurkok száma");

        loopsNumber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        loopsNumber.setText("-");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setText("Hurok információk");

        loopSelectorComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                loopSelectorComboBoxItemStateChanged(evt);
            }
        });
        loopSelectorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopSelectorComboBoxActionPerformed(evt);
            }
        });

        loopInfo.setEditable(false);
        loopInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        loopInfo.setToolTipText("");
        jScrollPane2.setViewportView(loopInfo);

        calculateLoopErrorButton.setText("Hurok hiba zárása");
        calculateLoopErrorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateLoopErrorButtonActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Hurok kirajzolása");
        jToggleButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loopsPanelLayout = new javax.swing.GroupLayout(loopsPanel);
        loopsPanel.setLayout(loopsPanelLayout);
        loopsPanelLayout.setHorizontalGroup(
            loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loopsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loopsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(calculateLoopErrorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, loopsPanelLayout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(loopsNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, loopsPanelLayout.createSequentialGroup()
                            .addComponent(jLabel25)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(loopSelectorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(167, Short.MAX_VALUE))
        );
        loopsPanelLayout.setVerticalGroup(
            loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loopsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(loopsNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(loopSelectorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(loopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(loopsPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jToggleButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calculateLoopErrorButton)))
                .addContainerGap(556, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Hurok információk", loopsPanel);

        newCaveButton.setText("Új barlang");
        newCaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCaveButtonActionPerformed(evt);
            }
        });

        newSurveyButton.setText("Új felmérés");
        newSurveyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSurveyButtonActionPerformed(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Forgatás"));

        xRotField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xRotField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xRotFieldActionPerformed(evt);
            }
        });

        yRotField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yRotField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yRotFieldActionPerformed(evt);
            }
        });

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("X tengely");

        jLabel27.setText("Y tengely");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(xRotField)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yRotField))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xRotField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yRotField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        caveOverview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout caveOverviewLayout = new javax.swing.GroupLayout(caveOverview);
        caveOverview.setLayout(caveOverviewLayout);
        caveOverviewLayout.setHorizontalGroup(
            caveOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 156, Short.MAX_VALUE)
        );
        caveOverviewLayout.setVerticalGroup(
            caveOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 155, Short.MAX_VALUE)
        );

        openCaveButton.setText("Megnyitás");
        openCaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCaveButtonActionPerformed(evt);
            }
        });

        saveCaveButton.setText("Mentés");
        saveCaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCaveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(caveOverview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newCaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(openCaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(newSurveyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveCaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(caveOverview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCaveButton)
                    .addComponent(newSurveyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openCaveButton)
                    .addComponent(saveCaveButton))
                .addContainerGap())
        );

        jPanel7.getAccessibleContext().setAccessibleName("Forgatások");
        caveOverview.addGLEventListener(co);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Állapotsor"));

        messageHeader.setText("Aktuális pont:");

        actualPointName.setEditable(false);
        actualPointName.setText("-");

        actualPointData.setEditable(false);
        actualPointData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualPointDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(messageHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actualPointName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actualPointData)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(messageHeader)
                            .addComponent(actualPointData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actualPointName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMenu1.setText("Fájl");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        newCaveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newCaveMenu.setText("Új barlang");
        newCaveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCaveMenuActionPerformed(evt);
            }
        });
        jMenu1.add(newCaveMenu);

        openCaveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openCaveMenu.setText("Megnyitás");
        openCaveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCaveMenuActionPerformed(evt);
            }
        });
        jMenu1.add(openCaveMenu);

        saveCaveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveCaveMenu.setText("Mentés");
        saveCaveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCaveMenuActionPerformed(evt);
            }
        });
        jMenu1.add(saveCaveMenu);
        jMenu1.add(jSeparator1);

        printMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printMenu.setText("Nyomtatás");
        printMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMenuActionPerformed(evt);
            }
        });
        jMenu1.add(printMenu);

        jMenu3.setText("Exportálás");

        dxfExportMenu.setText("DXF(jdxf)");
        dxfExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dxfExportMenuActionPerformed(evt);
            }
        });
        jMenu3.add(dxfExportMenu);

        excelExportMenu.setText("Excel");
        excelExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelExportMenuActionPerformed(evt);
            }
        });
        jMenu3.add(excelExportMenu);

        therionExportMenu.setText("Therion");
        therionExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                therionExportMenuActionPerformed(evt);
            }
        });
        jMenu3.add(therionExportMenu);

        jMenu1.add(jMenu3);

        jMenu10.setText("Importálás");

        svxImportMenu.setText("SVX import felmérésként");
        svxImportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                svxImportMenuActionPerformed(evt);
            }
        });
        jMenu10.add(svxImportMenu);

        jMenu1.add(jMenu10);
        jMenu1.add(jSeparator2);

        exitMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        exitMenu.setText("Kilépés");
        exitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Térkép");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMenu4.setText("Nézet");

        basePlanRadioButton.setSelected(true);
        basePlanRadioButton.setText("Alaprajz");
        basePlanRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                basePlanRadioButtonActionPerformed(evt);
            }
        });
        jMenu4.add(basePlanRadioButton);

        sectionPlanRadioButton.setSelected(true);
        sectionPlanRadioButton.setText("Hosszmetszet");
        sectionPlanRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sectionPlanRadioButtonActionPerformed(evt);
            }
        });
        jMenu4.add(sectionPlanRadioButton);

        modellRadioButton.setSelected(true);
        modellRadioButton.setText("Térmodell");
        modellRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modellRadioButtonActionPerformed(evt);
            }
        });
        jMenu4.add(modellRadioButton);

        jMenu2.add(jMenu4);

        jMenu5.setText("Színek");

        jMenuItem4.setText("Háttérszíne");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenu6.setText("Poligon színe");

        jMenuItem5.setText("Egyszínű");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem5);

        jMenuItem6.setText("Mélység szerint");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem6);

        jMenuItem1.setText("Felmérés szerinti");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem1);

        jMenu5.add(jMenu6);

        jMenu2.add(jMenu5);

        jMenu7.setText("Vonal vastagság");

        lineWidth1ButtonMenu.setSelected(true);
        lineWidth1ButtonMenu.setText("1");
        lineWidth1ButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWidth1ButtonMenuActionPerformed(evt);
            }
        });
        jMenu7.add(lineWidth1ButtonMenu);

        lineWidth2ButtonMenu.setSelected(true);
        lineWidth2ButtonMenu.setText("2");
        lineWidth2ButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWidth2ButtonMenuActionPerformed(evt);
            }
        });
        jMenu7.add(lineWidth2ButtonMenu);

        lineWidth3ButtonMenu.setSelected(true);
        lineWidth3ButtonMenu.setText("3");
        lineWidth3ButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWidth3ButtonMenuActionPerformed(evt);
            }
        });
        jMenu7.add(lineWidth3ButtonMenu);

        jMenu2.add(jMenu7);

        jMenu8.setText("Pont felirat");

        nonePointTextButtonMenu.setSelected(true);
        nonePointTextButtonMenu.setText("Nincs");
        nonePointTextButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nonePointTextButtonMenuActionPerformed(evt);
            }
        });
        jMenu8.add(nonePointTextButtonMenu);

        idPointTextButtonMenu.setSelected(true);
        idPointTextButtonMenu.setText("Azonosító");
        idPointTextButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idPointTextButtonMenuActionPerformed(evt);
            }
        });
        jMenu8.add(idPointTextButtonMenu);

        depthPointTextButtonMenu.setSelected(true);
        depthPointTextButtonMenu.setText("Relatív mélység");
        depthPointTextButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depthPointTextButtonMenuActionPerformed(evt);
            }
        });
        jMenu8.add(depthPointTextButtonMenu);

        eovDepthPointTextButtonMenu.setSelected(true);
        eovDepthPointTextButtonMenu.setText("Tengerszint feletti magasság");
        eovDepthPointTextButtonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eovDepthPointTextButtonMenuActionPerformed(evt);
            }
        });
        jMenu8.add(eovDepthPointTextButtonMenu);

        jMenu2.add(jMenu8);
        jMenu2.add(jSeparator3);

        entranceViewCheckBoxMenu.setSelected(true);
        entranceViewCheckBoxMenu.setText("Bejárat kiemelése");
        entranceViewCheckBoxMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entranceViewCheckBoxMenuActionPerformed(evt);
            }
        });
        jMenu2.add(entranceViewCheckBoxMenu);

        jMenuBar1.add(jMenu2);

        jMenu9.setText("Hurkok");

        findLoopsMenuItem.setText("Hurkok keresése");
        findLoopsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findLoopsMenuItemActionPerformed(evt);
            }
        });
        jMenu9.add(findLoopsMenuItem);

        loopsInfoCheckBoxMenu.setSelected(true);
        loopsInfoCheckBoxMenu.setText("Hurok információk");
        loopsInfoCheckBoxMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopsInfoCheckBoxMenuActionPerformed(evt);
            }
        });
        jMenu9.add(loopsInfoCheckBoxMenu);

        jMenuBar1.add(jMenu9);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(tabbedPane)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newCaveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCaveMenuActionPerformed
        // TODO add your handling code here:
        cp= new CaveProject();
        tabbedPane.setSelectedIndex(2);
        projectNameField.requestFocus();
        Utils.clearCavesTree(cavesTree);
        Utils.clearSurveyPanelData();
        Utils.clearProjectPanel();
        Utils.removeAllRowFromTable(dataTable);
        Utils.removeAllRowFromTable(coordinatesTable);
        Utils.removeAllRowFromTable(meridianTable);
        Utils.removeNewColumnsFromDataTable(dataTable);
        cr.setCp(cp);
        cr.setOverview(co);
        caveMap.repaint();
        this.setPanelEnabled(jPanel8, true);
    }//GEN-LAST:event_newCaveMenuActionPerformed

    private void openCaveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCaveMenuActionPerformed
        // TODO add your handling code here:
        this.setPanelEnabled(jPanel8, true);
        JFileChooser fileChooser = new JFileChooser();
        File file = new File("properties.txt"); 
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();
            if (line.length() > 1){
                fileChooser.setCurrentDirectory(new File(line));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileNameExtensionFilter filterPolygon = new FileNameExtensionFilter("Polygon Cave File", "cave");
        FileNameExtensionFilter filterPolygon2 = new FileNameExtensionFilter("Polygon2 Cave File", "cave2");
        fileChooser.addChoosableFileFilter(filterPolygon);
        fileChooser.setFileFilter(filterPolygon2);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            PrintWriter writer;
            try {
                writer = new PrintWriter("properties.txt", "UTF-8");
                writer.println(selectedFile.getParent());
            writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //ap.setWorkingDirectory();
            if (selectedFile.getAbsolutePath().endsWith("cave2")){
                ReadCave2File cave2File = new ReadCave2File(selectedFile);
                cp = cave2File.cp;
            } else {
                ReadCaveFile caveFile = new ReadCaveFile(selectedFile);
                cp = caveFile.cp;
            }
            Utils.clearCavesTree(cavesTree);
            Utils.addCavesToTree(cavesTree, cp);
            Utils.calculateFixedCoordinates(cp);
            Utils.fillProjectPanelWithData(cp);
            cr.setCp(cp);
            cr.setOverview(co);
            cr.fittToScreen = true;
            setTitle(cp.getProjectName());
            caveMap.repaint();
        }
    }//GEN-LAST:event_openCaveMenuActionPerformed

    private void newColumnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newColumnButtonActionPerformed
        // TODO add your handling code here:
        String columnName = JOptionPane.showInputDialog(this, "Mi legyen az új oszlop neve?", "Új oszlop", JOptionPane.QUESTION_MESSAGE);
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.addColumn(columnName);
    }//GEN-LAST:event_newColumnButtonActionPerformed

    private void coordinatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coordinatesButtonActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
        if ( node.isLeaf() && cp != null && !node.isRoot()) {
                this.tabbedPane.setEnabledAt(4, true);
//                tabbedPane.setSelectedIndex(4);
//					surveyNameField.requestFocus();
                Utils.fillCoordinatesTable(coordinatesTable, cp.findSurveyByName(node.toString()));
        }
    }//GEN-LAST:event_coordinatesButtonActionPerformed

    private void newLineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newLineButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.addRow(new Object[]{});
        dataTable.requestFocus();
        dataTable.changeSelection(dataTable.getRowCount()-1, 0, false, false);
    }//GEN-LAST:event_newLineButtonActionPerformed

    private void meridianButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meridianButtonActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
        if ( node.isLeaf() && cp != null && !node.isRoot()) {
                this.tabbedPane.setEnabledAt(4, true);
                tabbedPane.setSelectedIndex(4);
//					surveyNameField.requestFocus();
                Utils.fillMeridianTable(meridianTable, cp.findSurveyByName(node.toString()));
                Utils.calculateFixedCoordinates(cp);
                caveMap.repaint();
        }
    }//GEN-LAST:event_meridianButtonActionPerformed

    private void newSurveyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSurveyButtonActionPerformed
        // TODO add your handling code here:
        if (cp != null ) {
            isNewSurvey=true;
            Utils.clearSurveyPanelData();
            this.setPanelEnabled(jPanel9, true);
            this.setPanelEnabled(dataPanel, true);
            dataTable.setEnabled(true);
            surveyerTable.setEnabled(true);
            Utils.removeAllRowFromTable(dataTable);
            tabbedPane.setSelectedIndex(2);
            surveyNameField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(null, "Hozz létre egy új barlangot először!");
        }
    }//GEN-LAST:event_newSurveyButtonActionPerformed

    private void newCaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCaveButtonActionPerformed
        // TODO add your handling code here:
        newCaveMenu.doClick();
    }//GEN-LAST:event_newCaveButtonActionPerformed

    private void openCaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCaveButtonActionPerformed
        // TODO add your handling code here:
        openCaveMenu.doClick();
    }//GEN-LAST:event_openCaveButtonActionPerformed

    private void saveTableDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTableDataButtonActionPerformed
        // TODO add your handling code here:
        TreePath selectionPath = cavesTree.getSelectionPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
        CaveSurvey actual = cp.findSurveyByName(node.toString());
        dataTable.requestFocus();
        dataTable.clearSelection();
        if (dataTable.isEditing()) {
            dataTable.getCellEditor().stopCellEditing();
        }
        ArrayList<CaveEdge> newData = new ArrayList<CaveEdge>();
        String[] header = new String[dataTable.getColumnCount()];
        for (int i=0; i<header.length; i++) {
            header[i]=dataTable.getColumnName(i);
        }
        actual.setSurveyHeader(header);
        for (int i=0; i<dataTable.getRowCount(); i++) {
                CaveEdge ce = new CaveEdge(new CavePoint((String)dataTable.getValueAt(i, dataTable.getColumn("From").getModelIndex())),
                                new CavePoint((String)dataTable.getValueAt(i, dataTable.getColumn("To").getModelIndex())),
                                Double.parseDouble(dataTable.getValueAt(i,  dataTable.getColumn("Length").getModelIndex() ).toString()),
                                Double.parseDouble(dataTable.getValueAt(i, dataTable.getColumn("Azimuth").getModelIndex()).toString()),
                                Double.parseDouble(dataTable.getValueAt(i, dataTable.getColumn("Vertical").getModelIndex()).toString()));
                for(int j=5; j<dataTable.getColumnCount(); j++) {
                        ce.addNotes((String)dataTable.getValueAt(i, j));
                }
                newData.add(ce);
        }
        actual.setSurveyData(newData);
        Utils.calculateFixedCoordinates(cp);
        Utils.fillTableWithData(dataTable, actual);
        if (selectionPath != null) {
            cavesTree.setSelectionPath(selectionPath);
        }
    }//GEN-LAST:event_saveTableDataButtonActionPerformed

    private void saveCaveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCaveMenuActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Mentés");
        File file = new File("properties.txt"); 
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();
            if (line.length() > 1){
                fileChooser.setCurrentDirectory(new File(line));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileNameExtensionFilter filterPolygon2 = new FileNameExtensionFilter("Polygon2 Cave File", "cave2");
        FileNameExtensionFilter filterPolygon = new FileNameExtensionFilter("Polygon Cave File", "cave");
        FileNameExtensionFilter filterTherion = new FileNameExtensionFilter("Therion Cave File", "th");
//        fileChooser.addChoosableFileFilter(filterTherion);
        fileChooser.setFileFilter(filterPolygon2);
        
        int userSelection = fileChooser.showSaveDialog(this);

        if (JFileChooser.APPROVE_OPTION == userSelection) {
            String[] ext = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions();
            System.out.println(ext[0]);
            File fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath()+ "." +ext[0]);
            if (fileChooser.getSelectedFile().getAbsolutePath().contains("." +ext[0])){
                fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath());
            }
            saveSurveyDataButton.doClick();
            saveTableDataButton.doClick();
            System.out.println("Save as file: " + fileToSave.getAbsoluteFile());
            SaveCave2File saveCave2File = new SaveCave2File(fileToSave, cp);
        }
    }//GEN-LAST:event_saveCaveMenuActionPerformed

    private void exitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuActionPerformed
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_exitMenuActionPerformed

    private void saveCaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCaveButtonActionPerformed
        // TODO add your handling code here:
        saveCaveMenu.doClick();
    }//GEN-LAST:event_saveCaveButtonActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        Color initialcolor=Color.BLACK;
        Color color=JColorChooser.showDialog(this,"Select a color",initialcolor);
        System.out.println(color.toString());
        cr.bckgrdRGB[0] = (float) color.getRed() / 255;
        cr.bckgrdRGB[1] = (float) color.getGreen() / 255;
        cr.bckgrdRGB[2] = (float) color.getBlue() / 255;
        System.out.println(cr.bckgrdRGB[0]);
        System.out.println(cr.bckgrdRGB[1]);
        System.out.println(cr.bckgrdRGB[2]);
        caveMap.repaint();
        
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void basePlanRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_basePlanRadioButtonActionPerformed
        // TODO add your handling code here:
        cr.rotable= true;
        cr.xRot=0;
        cr.yRot=-90;
        caveMap.repaint();
        cr.rotable=false;

    }//GEN-LAST:event_basePlanRadioButtonActionPerformed

    private void sectionPlanRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sectionPlanRadioButtonActionPerformed
        // TODO add your handling code here:
        cr.rotable=true;
        cr.xRot=0;
        cr.yRot=0;
        caveMap.repaint();
        cr.rotable=false;
    }//GEN-LAST:event_sectionPlanRadioButtonActionPerformed

    private void modellRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modellRadioButtonActionPerformed
        // TODO add your handling code here:
        cr.xRot=0;
        cr.yRot=-90;
        cr.rotable=true;
        caveMap.repaint();
    }//GEN-LAST:event_modellRadioButtonActionPerformed

    private void lineWidth1ButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWidth1ButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.lineWidth=1;
        caveMap.repaint();
    }//GEN-LAST:event_lineWidth1ButtonMenuActionPerformed

    private void lineWidth2ButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWidth2ButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.lineWidth=2;
        caveMap.repaint();
    }//GEN-LAST:event_lineWidth2ButtonMenuActionPerformed

    private void lineWidth3ButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWidth3ButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.lineWidth=3;
        caveMap.repaint();
    }//GEN-LAST:event_lineWidth3ButtonMenuActionPerformed

    private void nonePointTextButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonePointTextButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.showText=0;
        caveMap.repaint();
    }//GEN-LAST:event_nonePointTextButtonMenuActionPerformed

    private void depthPointTextButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depthPointTextButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.showText=2;
        caveMap.repaint();
    }//GEN-LAST:event_depthPointTextButtonMenuActionPerformed

    private void eovDepthPointTextButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eovDepthPointTextButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.showText=3;
        caveMap.repaint();
    }//GEN-LAST:event_eovDepthPointTextButtonMenuActionPerformed

    private void idPointTextButtonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idPointTextButtonMenuActionPerformed
        // TODO add your handling code here:
        cr.showText=1;
        caveMap.repaint();
    }//GEN-LAST:event_idPointTextButtonMenuActionPerformed

    private void entranceViewCheckBoxMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entranceViewCheckBoxMenuActionPerformed
        // TODO add your handling code here:
        cr.entranceView = this.entranceViewCheckBoxMenu.isSelected();
        caveMap.repaint();
    }//GEN-LAST:event_entranceViewCheckBoxMenuActionPerformed

    private void findLoopsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findLoopsMenuItemActionPerformed
        // TODO add your handling code here:
        System.out.println("Hurkok");
        cycles = Utils.getLoopsInCave(cp);
        tabbedPane.setSelectedIndex(5);
        loopsNumber.setText(Integer.toString(cycles.size()));
        
        Vector comboBoxItems=new Vector();
        comboBoxItems.add("");
        for (int i=1; i<=cycles.size(); i++) {
            comboBoxItems.add(i+". hurok");
        }
        
        DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        loopSelectorComboBox.setModel(model);
        this.tabbedPane.setEnabledAt(5, true);
               
    }//GEN-LAST:event_findLoopsMenuItemActionPerformed

    private void loopSelectorComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_loopSelectorComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_loopSelectorComboBoxItemStateChanged

    private void loopSelectorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopSelectorComboBoxActionPerformed
        // TODO add your handling code here:
        ArrayList<CaveEdge> loopEdges= new ArrayList<>();
        if (cycles != null) {
            Object[] cyclesArray = cycles.toArray();
            if (loopSelectorComboBox.getSelectedIndex() >= 1) {
                int selectedIndex =loopSelectorComboBox.getSelectedIndex()-1;
                LinkedList<DefaultEdge> actualCycle = (LinkedList<DefaultEdge>) cyclesArray[selectedIndex];
                StringBuilder str = new StringBuilder();
                str.append(cyclesArray[selectedIndex].toString());
                str.append("\n");
                for (int i=0; i<actualCycle.size(); i++){
                    DefaultEdge actualGraphEdge = actualCycle.get(i);
                    CaveEdge actualCaveEdge = cp.findCaveEdge(graph.getEdgeSource(actualGraphEdge), graph.getEdgeTarget(actualGraphEdge));
                    loopEdges.add(actualCaveEdge);
                }
                CavePoint start = null, end = null;
//                for (CaveEdge edge : loopEdges) {
//                    for (CaveEdge edge2 : loopEdges) {
//                        if 
//                    }
//                    System.out.println(edge.from.getName()+ " " + Arrays.toString(edge.from.getCoordinates()));
//                    System.out.println(edge.to.getName()+ " " + Arrays.toString(edge.to.getCoordinates()));
//                    System.out.println("-----------------------------------");
//                }
                cl = new CaveLoop(loopEdges);
                double loopLength=cl.loopLength;
                double loopProjectedLength=cl.loopProjectedLength;
                cr.loopEdges = cl.getLoopEdges();
                start=cl.getStart();
                end=cl.getEnd();
                str.append("Hurok hossza: " + String.format("%.2f", loopLength) + " méter\n");
                str.append("Hurok alaprajzi hossza: " + String.format("%.2f", loopProjectedLength) + " méter\n");
                try{
                    double xError = cl.getxError();
                    double yError = cl.getyError();
                    double zError = cl.getzError();
                    double error = cl.getError();
                    str.append("X hiba: " + String.format("%.4f", Math.abs(xError)) + " méter\n");
                    str.append("Y hiba: " + String.format("%.4f", Math.abs(yError)) + " méter\n");
                    str.append("Z hiba: " + String.format("%.4f", Math.abs(zError)) + " méter\n");
                    str.append("Térbeli hiba: " + String.format("%.4f", error) +" méter\n");
                    str.append("Hiba százalék: "+ String.format("%.4f", error/loopLength*100)+"\n");
                    if ( (error/loopLength*100) > 0.5 ) {
                        str.append("Érdemes felmérni újra az adott hurkot\n");
                    } else {
                        str.append("Minden rendben.\n");
                    }
                } catch (Exception e) {
                    System.out.println("Nincs meg a kezdő és végpont");
                }
                loopInfo.setText(str.toString());
            } else {
                loopInfo.setText("");
            }
            this.jToggleButton1.setSelected(false);
        }
    }//GEN-LAST:event_loopSelectorComboBoxActionPerformed

    private void loopsInfoCheckBoxMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopsInfoCheckBoxMenuActionPerformed
        // TODO add your handling code here:
        if(this.loopsInfoCheckBoxMenu.isSelected()){
            this.tabbedPane.setEnabledAt(6, true);
        } else {
            this.tabbedPane.setEnabledAt(6, false);
        }
        
    }//GEN-LAST:event_loopsInfoCheckBoxMenuActionPerformed

    private void dxfExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dxfExportMenuActionPerformed
        // TODO add your handling code here:
        if (cp != null) {
            JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Mentés");
        fileChooser.setCurrentDirectory(new File("C:\\munka\\kutvez\\csodabogyos\\"));
        FileNameExtensionFilter filterDXF = new FileNameExtensionFilter("DXF File", "dxf");
        fileChooser.setFileFilter(filterDXF);
        
        int userSelection = fileChooser.showSaveDialog(this);

        if (JFileChooser.APPROVE_OPTION == userSelection) {
            String[] ext = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions();
            System.out.println(ext[0]);
            File fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath()+ "." +ext[0]);
            if (fileChooser.getSelectedFile().getAbsolutePath().contains("." +ext[0])){
                fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath());
            }
            System.out.println("Save as file: " + fileToSave.getAbsoluteFile());
            DxfExport dxfOut = new DxfExport();
            dxfOut.generateOutput(cp);
            dxfOut.saveToFile(fileToSave);
        }
        }
    }//GEN-LAST:event_dxfExportMenuActionPerformed

    private void printMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printMenuActionPerformed
        // TODO add your handling code here:
        Graphics graphics = caveMap.getGraphics();
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PageFormat pf = job.pageDialog(aset);
        
        job.setPrintable(new CavePrint());
        boolean ok = job.printDialog(aset);
        if (ok) {
            try {
                System.out.println("print");
                job.print(aset);
            } catch (PrinterException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }

//        double scaleGLMatXY = 72.0/5;
//        int numSamples = 0; // leave multisampling as-is
//        final AWTPrintLifecycle.Context ctx = AWTPrintLifecycle.Context.setupPrint(caveMap.getParent(), scaleGLMatXY, scaleGLMatXY, numSamples,-1,-1);
////        AWTPrintLifecycle.Context ctx = caveMap.setupPrint(1, 1, 0, -1, -1);
//        if (ok) {
//            try {
//                 AWTEDTExecutor.singleton.invoke(true, new Runnable() {
//                      public void run() {
//                          try {
//                              job.print();
//                          } catch (PrinterException ex) {
//                              ex.printStackTrace();
//                          }
//                     } });
//              } finally {
//                 ctx.releasePrint();
//              }
//        }
    }//GEN-LAST:event_printMenuActionPerformed

    private void xRotFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xRotFieldActionPerformed
        // TODO add your handling code here:
        cr.xRot=Float.valueOf(xRotField.getText());
        caveMap.repaint();
    }//GEN-LAST:event_xRotFieldActionPerformed

    private void yRotFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yRotFieldActionPerformed
        // TODO add your handling code here:
        cr.yRot=Float.valueOf(yRotField.getText());
        caveMap.repaint();
    }//GEN-LAST:event_yRotFieldActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        cr.surveyColors.clear();
        int surveysNumber = this.cp.caveSurveys.size();
        System.out.println(surveysNumber);
        for (int i=0; i<surveysNumber; i++){
            Color c = Color.getHSBColor((float) (i*1/(surveysNumber*1.0)), 1, 1);
            cr.surveyColors.add(c);
        }
        cr.coloringMode = 3;
        caveMap.repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        Color initialcolor=Color.BLACK;
        Color color=JColorChooser.showDialog(this,"Select a color",initialcolor);
        cr.coloringMode=2;
        cr.caveRGB[0] = color.getRed() / 255;
        cr.caveRGB[1] = color.getGreen() / 255;
        cr.caveRGB[2] = color.getBlue() / 255;
        caveMap.repaint();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        cr.coloringMode=1;
        caveMap.repaint();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void removeLineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLineButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        if (dataTable.getSelectedRowCount() > 0) {
            int[] rows = dataTable.getSelectedRows();
            for (int i:rows){
                model.removeRow(i);
            }
        }
    
    }//GEN-LAST:event_removeLineButtonActionPerformed

    private void deleteColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteColumnActionPerformed
        // TODO add your handling code here:
        JPanel panel = new JPanel();
        panel.add(new JLabel("Melyik oszlopot töröljük:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        DefaultTableModel tableModel = (DefaultTableModel) dataTable.getModel();
        int columnCount = tableModel.getColumnCount();
        model.addElement("");
        for (int i=6; i<columnCount; i++){
            model.addElement(tableModel.getColumnName(i));
        }
        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Oszlop törlése", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (result) {
            case JOptionPane.OK_OPTION:
                removeColumn(comboBox.getSelectedIndex()+5);
                break;
        }
        
    }//GEN-LAST:event_deleteColumnActionPerformed

    private void excelExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelExportMenuActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Mentés");
        File file = new File("properties.txt"); 
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();
            if (line.length() > 1){
                fileChooser.setCurrentDirectory(new File(line));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileNameExtensionFilter filterXLS = new FileNameExtensionFilter("Excel file", "xls");
        
        fileChooser.setFileFilter(filterXLS);
        
        int userSelection = fileChooser.showSaveDialog(this);

        if (JFileChooser.APPROVE_OPTION == userSelection) {
            String[] ext = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions();
            System.out.println(ext[0]);
            File fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath()+ "." +ext[0]);
            if (fileChooser.getSelectedFile().getAbsolutePath().contains("." +ext[0])){
                fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath());
            }
            System.out.println("Save as file: " + fileToSave.getAbsoluteFile());
            ExcelExport ee = new ExcelExport();
            ee.cp=cp;
            ee.export(fileToSave);
        }
    }//GEN-LAST:event_excelExportMenuActionPerformed

    private void calculateLoopErrorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateLoopErrorButtonActionPerformed
        // TODO add your handling code here:
        cl.calculateFixedLoop();
        cl.calculateFixedLoopInEov();
    }//GEN-LAST:event_calculateLoopErrorButtonActionPerformed

    private void therionExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_therionExportMenuActionPerformed
        // TODO add your handling code here:
        if (cp != null){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Mentés");
            File file = new File("properties.txt"); 
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                br.close();
                if (line.length() > 1){
                    fileChooser.setCurrentDirectory(new File(line));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileNameExtensionFilter filterXLS = new FileNameExtensionFilter("Therion file", "th");

            fileChooser.setFileFilter(filterXLS);

            int userSelection = fileChooser.showSaveDialog(this);

            if (JFileChooser.APPROVE_OPTION == userSelection) {
                String[] ext = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions();
                System.out.println(ext[0]);
                File fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath()+ "." +ext[0]);
                if (fileChooser.getSelectedFile().getAbsolutePath().contains("." +ext[0])){
                    fileToSave = new File(fileChooser.getSelectedFile().getAbsolutePath());
                }
                System.out.println("Save as file: " + fileToSave.getAbsoluteFile());
                TherionExport te = new TherionExport();
                te.cp=cp;
                te.export(fileToSave);
            }
        }
    }//GEN-LAST:event_therionExportMenuActionPerformed

    private void svxImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_svxImportMenuActionPerformed
        // TODO add your handling code here:
        if ( cp != null ){
            JFileChooser fileChooser = new JFileChooser();
            File file = new File("properties.txt"); 
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                br.close();
                if (line.length() > 1){
                    fileChooser.setCurrentDirectory(new File(line));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            //fileChooser.setCurrentDirectory(new File("C:\\munka\\kutvez\\csodabogyos\\"));
            FileNameExtensionFilter filterSVX = new FileNameExtensionFilter("SVX file", "svx");
            fileChooser.setFileFilter(filterSVX);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                PrintWriter writer;
                try {
                    writer = new PrintWriter("properties.txt", "UTF-8");
                    writer.println(selectedFile.getParent());
                writer.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

                ReadSvxFile surveyFile = new ReadSvxFile(selectedFile);
                cp.addCaveSurvey(surveyFile.cs);
                Utils.clearCavesTree(cavesTree);
                Utils.addCavesToTree(cavesTree, cp);
                Utils.calculateFixedCoordinates(cp);
                Utils.fillProjectPanelWithData(cp);
                cr.setCp(cp);
                cr.setOverview(co);
                caveMap.repaint();
                caveOverview.repaint();
                saveSurveyDataButton.doClick();
                saveTableDataButton.doClick();
            }
        } else {
                JOptionPane.showMessageDialog(null, "Nyiss meg vagy hozz létre új barlangot az importáláshoz!", "InfoBox ", JOptionPane.INFORMATION_MESSAGE);
            }
    }//GEN-LAST:event_svxImportMenuActionPerformed

    private void actualPointDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualPointDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_actualPointDataActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        cr.showLoop = jToggleButton1.isSelected();
        tabbedPane.setSelectedIndex(0);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void cavesTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cavesTreeMouseClicked
        // TODO add your handling code here:
        this.setPanelEnabled(jPanel9, true);
        this.setPanelEnabled(dataPanel, true);
        dataTable.setEnabled(true);
        surveyerTable.setEnabled(true);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
        if ( node.isLeaf() && cp != null && !node.isRoot()&& (node.getLevel() > 1) ) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (tabbedPane.getSelectedIndex()==0) {
                tabbedPane.setSelectedIndex(2);
            }
            //tabbedPane.setSelectedIndex(2);
            surveyNameField.requestFocus();
            int index = parent.getIndex(node);
            String indexName = node.toString();
            System.out.println(node.toString());
            if (cp.findSurveyByName(node.toString()).getSurveyHeader() != null) {
                Utils.fillTableWithData(dataTable, cp.findSurveyByName(node.toString()));
                this.coordinatesButton.doClick();
                if(tabbedPane.getSelectedIndex()==4) {
                    this.meridianButton.doClick();
                }
            }
            Utils.fillSurveyPanelWithData(cp.findSurveyByName(node.toString()));
        }
    }//GEN-LAST:event_cavesTreeMouseClicked

    private void renameColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameColumnActionPerformed
        // TODO add your handling code here:
        JPanel panel = new JPanel();
        panel.add(new JLabel("Melyik oszlopot töröljük:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        DefaultTableModel tableModel = (DefaultTableModel) dataTable.getModel();
        int columnCount = tableModel.getColumnCount();
        model.addElement("");
        System.out.println("---------------------");
        for (int i=6; i<columnCount; i++){
            System.out.println(tableModel.getColumnName(i));
            model.addElement(tableModel.getColumnName(i));
        }
        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);
        JTextField textField = new JTextField(20);
        panel.add(textField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Oszlop átnevezése", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (result) {
            case JOptionPane.OK_OPTION:
                if (comboBox.getSelectedIndex()>0){
                    renameColumn(comboBox.getSelectedIndex()+5,textField.getText(), tableModel);
                }
                break;
        }
    }//GEN-LAST:event_renameColumnActionPerformed

    private void surveyTeamFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_surveyTeamFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_surveyTeamFieldActionPerformed

    private void meridianInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meridianInfoButtonActionPerformed
        // TODO add your handling code here:
        meridianButton.doClick();
        tabbedPane.setSelectedIndex(2);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
        if ( node.isLeaf() && cp != null && !node.isRoot()) {
            //                Utils.fillMeridianTable(meridianTable, cp.findSurveyByName(node.toString()));
            //                Utils.calculateFixedCoordinates(cp);
            //                caveMap.repaint();
            CaveSurvey cs = cp.findSurveyByName(node.toString());
            double meridianInfo = 0;
            for (CaveEdge edge : cs.getSurveyData()) {
                meridianInfo+=edge.getMeridianConvergence();
            }
            meridianInfo = meridianInfo / cs.getSurveyData().size();
            meridianInfoField.setText(String.format("%.2f", meridianInfo));
        }
    }//GEN-LAST:event_meridianInfoButtonActionPerformed

    private void saveSurveyDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSurveyDataButtonActionPerformed
        // TODO add your handling code here:
        TreePath selectionPath = cavesTree.getSelectionPath();
        if (isNewSurvey == true) {
            Utils.addSurveyToCaveProject(cp);
            Utils.clearCavesTree(cavesTree);
            Utils.addCavesToTree(cavesTree, cp);
            isNewSurvey=false;
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) cavesTree.getLastSelectedPathComponent();
            if (node != null){
                CaveSurvey actual = cp.findSurveyByName(node.toString());
                Utils.saveSurveyPanelData(actual);
                Utils.fillSurveyPanelWithData(actual);
                Utils.clearCavesTree(cavesTree);
                Utils.addCavesToTree(cavesTree, cp);
            }
        }
        if (selectionPath != null) {
            cavesTree.setSelectionPath(selectionPath);
        }
    }//GEN-LAST:event_saveSurveyDataButtonActionPerformed

    private void fixPointCommentFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixPointCommentFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fixPointCommentFieldActionPerformed

    private void fixedPointIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedPointIdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fixedPointIdFieldActionPerformed

    private void fixPointCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixPointCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fixPointCheckboxActionPerformed

    private void getDeclinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDeclinationButtonActionPerformed
        // TODO add your handling code here:
        System.out.println("Get declination");
        progressBar.setIndeterminate(true);
        //        progressBar.setVisible(true);
        MagneticDeclination md;
        if (fixPointCheckbox.isSelected()){
            try {
                EovWgsConverter ewc = new EovWgsConverter(xField.getText(), yField.getText());
                ewc.convert();
                md = new MagneticDeclination(surveyDatePicker.getText(), ewc.getLat(), ewc.getLon());
                declinationField.setText(Double.toString(md.getDeclination()));
            } catch (JSONException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FixPoint fixPoint = null;
            for (CaveSurvey survey : cp.caveSurveys){
                if ( survey.getFixPoint() != null){
                    fixPoint = survey.getFixPoint();
                }
            }
            if (fixPoint == null) {
                infoBox("Nincs fixpont a felmérésben!", "Hiba");
            } else {
                try {
                    EovWgsConverter ewc = new EovWgsConverter((int)fixPoint.coordinates[0], (int)fixPoint.coordinates[1]);
                    ewc.convert();
                    md = new MagneticDeclination(surveyDatePicker.getText(), ewc.getLat(), ewc.getLon());
                    declinationField.setText(Double.toString(md.getDeclination()));
                } catch (JSONException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        progressBar.setIndeterminate(false);
    }//GEN-LAST:event_getDeclinationButtonActionPerformed

    private void storeProjectDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeProjectDataButtonActionPerformed
        // TODO add your handling code here:
        cavesTree.clearSelection();
        Utils.saveProjectPanelData(cp);
        Utils.fillProjectPanelWithData(cp);
        Utils.clearCavesTree(cavesTree);
        Utils.addCavesToTree(cavesTree, cp);
    }//GEN-LAST:event_storeProjectDataButtonActionPerformed

    private void lengthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lengthComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lengthComboBoxActionPerformed

    public static void main(String[] args) {
        // TODO code application logic here
        try {
            javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });  
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextField actualPointData;
    private static javax.swing.JTextField actualPointName;
    private static javax.swing.JRadioButtonMenuItem basePlanRadioButton;
    private javax.swing.JButton calculateLoopErrorButton;
    private static com.jogamp.opengl.awt.GLJPanel caveMap;
    private static com.jogamp.opengl.awt.GLJPanel caveOverview;
    private javax.swing.JTree cavesTree;
    private static javax.swing.JComboBox<String> compassComboBox;
    private static javax.swing.JTextField compassCorrectionField;
    private javax.swing.JButton coordinatesButton;
    private javax.swing.JPanel coordinatesPanel;
    private javax.swing.JTable coordinatesTable;
    private javax.swing.JPanel dataPanel;
    private static javax.swing.JTable dataTable;
    private static javax.swing.JTextField declinationField;
    private javax.swing.JButton deleteColumn;
    private javax.swing.JRadioButtonMenuItem depthPointTextButtonMenu;
    private javax.swing.JMenuItem dxfExportMenu;
    private javax.swing.JCheckBoxMenuItem entranceViewCheckBoxMenu;
    private javax.swing.JRadioButtonMenuItem eovDepthPointTextButtonMenu;
    private javax.swing.JMenuItem excelExportMenu;
    private javax.swing.JMenuItem exitMenu;
    private javax.swing.JMenuItem findLoopsMenuItem;
    private static javax.swing.JCheckBox fixPointCheckbox;
    private static javax.swing.JTextField fixPointCommentField;
    private static javax.swing.JTextField fixedPointIdField;
    private javax.swing.JButton getDeclinationButton;
    private javax.swing.JRadioButtonMenuItem idPointTextButtonMenu;
    private static javax.swing.JComboBox<String> inclinoComboBox;
    private static javax.swing.JTextField inclinoCorrectionField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToggleButton jToggleButton1;
    private static javax.swing.JComboBox<String> lengthComboBox;
    private static javax.swing.JTextField lengthCorrectionField;
    private javax.swing.JRadioButtonMenuItem lineWidth1ButtonMenu;
    private javax.swing.JRadioButtonMenuItem lineWidth2ButtonMenu;
    private javax.swing.JRadioButtonMenuItem lineWidth3ButtonMenu;
    private javax.swing.JTextPane loopInfo;
    private javax.swing.JComboBox<String> loopSelectorComboBox;
    private javax.swing.JCheckBoxMenuItem loopsInfoCheckBoxMenu;
    private javax.swing.JLabel loopsNumber;
    private javax.swing.JPanel loopsPanel;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JButton meridianButton;
    private javax.swing.JButton meridianInfoButton;
    private javax.swing.JTextField meridianInfoField;
    private javax.swing.JPanel meridianPanel;
    private javax.swing.JTable meridianTable;
    private javax.swing.JLabel messageHeader;
    private javax.swing.JRadioButtonMenuItem modellRadioButton;
    private javax.swing.JButton newCaveButton;
    private javax.swing.JMenuItem newCaveMenu;
    private static javax.swing.JButton newColumnButton;
    private static javax.swing.JButton newLineButton;
    private javax.swing.JButton newSurveyButton;
    private javax.swing.JRadioButtonMenuItem nonePointTextButtonMenu;
    private javax.swing.JButton openCaveButton;
    private javax.swing.JMenuItem openCaveMenu;
    private javax.swing.JMenuItem printMenu;
    private javax.swing.JProgressBar progressBar;
    private static javax.swing.JLabel projectAltitude;
    private static javax.swing.JTextField projectCodeField;
    private static javax.swing.JLabel projectDepth;
    private static javax.swing.JLabel projectEndDate;
    private static javax.swing.JLabel projectFullLength;
    private static javax.swing.JTextField projectNameField;
    private static javax.swing.JTextField projectPlaceField;
    private static javax.swing.JLabel projectPointCount;
    private static javax.swing.JLabel projectStartDate;
    private static javax.swing.JLabel projectVertical;
    private javax.swing.JButton removeLineButton;
    private javax.swing.JButton renameColumn;
    private javax.swing.JButton saveCaveButton;
    private javax.swing.JMenuItem saveCaveMenu;
    private javax.swing.JButton saveSurveyDataButton;
    private static javax.swing.JButton saveTableDataButton;
    private javax.swing.JRadioButtonMenuItem sectionPlanRadioButton;
    private static javax.swing.JLabel selectedLength;
    private javax.swing.JButton storeProjectDataButton;
    private static datechooser.beans.DateChooserCombo surveyDatePicker;
    private static javax.swing.JLabel surveyFullLength;
    private static javax.swing.JTextField surveyNameField;
    private static javax.swing.JPanel surveyPanel;
    private static javax.swing.JLabel surveyPointCount;
    private static javax.swing.JTextField surveyTeamField;
    private javax.swing.JScrollPane surveyerPanel;
    private static javax.swing.JTable surveyerTable;
    private javax.swing.JMenuItem svxImportMenu;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JMenuItem therionExportMenu;
    private static javax.swing.JTextField xField;
    private static javax.swing.JTextField xRotField;
    private static javax.swing.JTextField yField;
    private static javax.swing.JTextField yRotField;
    private static javax.swing.JTextField zField;
    // End of variables declaration//GEN-END:variables


    public static JPanel getSurveyPanel() {
        return surveyPanel;
    }

    

    public static DateChooserCombo getSurveyDatePicker() {
        return surveyDatePicker;
    }

    public JTree getCavesTree() {
        return cavesTree;
    }

    public JTable getCoordinatesTable() {
        return coordinatesTable;
    }

    public static JTable getDataTable() {
        return dataTable;
    }

    public static JTextField getDeclinationField() {
        return declinationField;
    }

    public static JCheckBox getFixPointCheckbox() {
        return fixPointCheckbox;
    }

    public static JTextField getFixedPointIdField() {
        return fixedPointIdField;
    }

    public static JTextField getProjectNameField() {
        return projectNameField;
    }

    public static JTextField getProjectPlaceField() {
        return projectPlaceField;
    }

    public static GLJPanel getCaveMap() {
        return caveMap;
    }

    public static JTextField getSurveyNameField() {
        return surveyNameField;
    }

    public static JTextField getxField() {
        return xField;
    }

    public static JTextField getyField() {
        return yField;
    }

    public static JTextField getzField() {
        return zField;
    }

    public static JTextField getActualPointName() {
        return actualPointName;
    }

    public static JTextField getActualPointData() {
        return actualPointData;
    }
    
    

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && me.isShiftDown()){
            if (cr.showSelectedPoint == false ){
                cr.showSelectedPoint= true;
                cr.findSelectedPoint= true;
                cr.selectedPointX = me.getX();
                cr.selectedPointY = me.getY();
                caveMap.repaint();
            } else {
                cr.showSelectedPoint = false;
                cr.findSelectedPoint= false;
                actualPointName.setText("-");
                actualPointData.setText("");
                caveMap.repaint();
            }
        }
        if (me.getButton()==MouseEvent.BUTTON2){
            cr.xRot=0;
            cr.yRot=-90;
            cr.yTrans=0;
            cr.zTrans=0;
            cr.fittToScreen=true;
            caveMap.repaint();
        } 
    }

    @Override
    public void mousePressed(MouseEvent me) {
        button1 = me.getButton()==MouseEvent.BUTTON1;
        button3 = me.getButton() == MouseEvent.BUTTON3;
        caveMap.requestFocusInWindow();
        movePrevX = me.getX();
        movePrevY = me.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if(button1 && modellRadioButton.isSelected()) {
            cr.xRot += 360*(me.getX()-movePrevX)/me.getComponent().getWidth();
            movePrevX = me.getX();
            cr.yRot += 360*(me.getY()-movePrevY)/me.getComponent().getHeight();
            movePrevY = me.getY();
            ellenorzes();
            caveMap.repaint();
        }
        if(button1 && sectionPlanRadioButton.isSelected()) {
            cr.xRot += 360*(me.getX()-movePrevX)/me.getComponent().getWidth();
            movePrevX = me.getX();
//            cr.yRot += 360*(me.getY()-movePrevY)/me.getComponent().getHeight();
//            movePrevY = me.getY();
            ellenorzes();
            caveMap.repaint();
        }
        if(button1 && basePlanRadioButton.isSelected()) {
            cr.xRot += 360*(me.getX()-movePrevX)/me.getComponent().getWidth();
            movePrevX = me.getX();
//            cr.yRot += 360*(me.getY()-movePrevY)/me.getComponent().getHeight();
//            movePrevY = me.getY();
            ellenorzes();
            caveMap.repaint();
        }
        if (button3) {
            cr.yTrans += (me.getX()-movePrevX)/10;
            movePrevX = me.getX();
            cr.zTrans -= (me.getY()-movePrevY)/10;
            movePrevY = me.getY();
            caveMap.repaint();
        }
    }

    public static JRadioButtonMenuItem getBasePlanRadioButton() {
        return basePlanRadioButton;
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if ( me.isShiftDown() ){
            cr.mouse = me;           
        }
        caveMap.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        cr.scale += mwe.getWheelRotation();
        caveMap.repaint();
    }
    
    public void ellenorzes(){
        if(cr.xRot > 356.0f)
            cr.xRot = 0.0f;

        if(cr.xRot < 0.0f)
          cr.xRot = 355.0f;

        if(cr.yRot > 180.0f)
          cr.yRot = -180.0f;

        if(cr.yRot < -180.0f)
          cr.yRot = 180.0f;
    }
    
    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public SimpleGraph<String, DefaultEdge> getGraph() {
        return graph;
    }

    public static void setGraph(SimpleGraph<String, DefaultEdge> graph) {
        MainWindow.graph = graph;
    }

    public static JTextField getxRotField() {
        return xRotField;
    }

    public static JTextField getyRotField() {
        return yRotField;
    }

    public static JButton getNewColumnButton() {
        return newColumnButton;
    }

    public static JButton getNewLineButton() {
        return newLineButton;
    }

    public static JButton getSaveTableDataButton() {
        return saveTableDataButton;
    }

    public static GLJPanel getCaveOverview() {
        return caveOverview;
    }

    public void removeColumn(int column) {
        // for each row, remove the column
        DefaultTableModel tableModel = (DefaultTableModel) dataTable.getModel();
        Vector rows = tableModel.getDataVector();
        for (Object row : rows) {
            ((Vector) row).remove(column);
        }

        // remove the header
        dataTable.removeColumn(dataTable.getColumnModel().getColumn(column));
        //columnIdentifiers.remove(column);

        // notify
        //tableModel.fireTableStructureChanged();
    }
    
    public void renameColumn(int column, String name, DefaultTableModel tableModel) {
        System.out.println(dataTable.getColumnName(column));
        TableColumnModel tcm = dataTable.getColumnModel();
        TableColumn tc = tcm.getColumn(column);
        tc.setHeaderValue(name);
        ArrayList<String> columnNames = new ArrayList<String>();
        Enumeration<TableColumn> columns = tcm.getColumns();
        while (columns.hasMoreElements()) {
            columnNames.add(columns.nextElement().getHeaderValue().toString());
        }
        String[] columnArray = columnNames.toArray(new String[columnNames.size()]);
        System.out.println(Arrays.toString(columnArray));
        tableModel.setColumnIdentifiers(columnArray);
//        dataTable.getColumnModel().getColumn(column).setHeaderValue(name);
        dataTable.getTableHeader().resizeAndRepaint();
    }

    public static JLabel getSelectedLength() {
        return selectedLength;
    }

    public JLabel getjLabel17() {
        return jLabel17;
    }

    public static JLabel getSurveyFullLength() {
        return surveyFullLength;
    }

    public static JLabel getSurveyPointCount() {
        return surveyPointCount;
    }

    public static JLabel getProjectAltitude() {
        return projectAltitude;
    }

    public static JLabel getProjectDepth() {
        return projectDepth;
    }

    public static JLabel getProjectEndDate() {
        return projectEndDate;
    }

    public static JLabel getProjectPointCount() {
        return projectPointCount;
    }

    public static JLabel getProjectStartDate() {
        return projectStartDate;
    }

    public static JLabel getProjectVertical() {
        return projectVertical;
    }

    public static JLabel getProjectFullLength() {
        return projectFullLength;
    }
    
    public static CaveOverview getCo() {
        return co;
    }
    
    public static CaveRenderer getCr() {
        return cr;
    }

    public static JTextField getFixPointCommentField() {
        return fixPointCommentField;
    }

    public static void setFixPointCommentField(JTextField fixPointCommentField) {
        MainWindow.fixPointCommentField = fixPointCommentField;
    }
    
    public void initSurveyerTable(){
        TableColumn jobsColumn = surveyerTable.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();
        comboBox.setEditable(true);
        for(String item:this.surveyerJobs){
            comboBox.addItem(item);
        }
        jobsColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }
    
    
    public static JTable getSurveyerTable() {
        return surveyerTable;
    }
    
    public static JTextField getSurveyTeamField() {
        return surveyTeamField;
    }

    public static void setSurveyerTable(JTable surveyerTable) {
        MainWindow.surveyerTable = surveyerTable;
    }

    public static JTextField getProjectCodeField() {
        return projectCodeField;
    }

    public static void setProjectCodeField(JTextField projectCodeField) {
        MainWindow.projectCodeField = projectCodeField;
    }
    
    public void setPanelEnabled(JPanel panel, Boolean isEnabled) {
    panel.setEnabled(isEnabled);

    Component[] components = panel.getComponents();

    for (Component component : components) {
        if (component instanceof JPanel) {
            setPanelEnabled((JPanel) component, isEnabled);
        }
        component.setEnabled(isEnabled);
    }
}

    private void setScrollPaneEnabled(JScrollPane panel, boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }
    
    public static void setCaveTitle(String title){
        MainWindow.title = title; 
    }
    
}
