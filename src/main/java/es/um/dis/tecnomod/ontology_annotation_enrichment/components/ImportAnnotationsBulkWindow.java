package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.ontology_annotation_enrichment.AnnotationEnricher;

public class ImportAnnotationsBulkWindow extends JPanel
                             implements ActionListener, PropertyChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2875550578135610005L;
	private static final int LOG_ROWS = 20;
	private static final int LOG_COLUMNS = 20;
	private static final int MESSAGE_ROWS= 2;
	private static final int MESSAGE_COLUMNS = 20;
	private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private JTextArea textMessage;
    private JTextField externalOntologyTextField;
    private JButton loadExternalOntologyButton;
    private Task task;
    private OWLOntology ontology;
    private Optional<OWLOntology> externalOntology;
    

    class Task extends SwingWorker<Void, Void> {
    	private List<EventListener> listeners;
    	
    	public Task(List<EventListener> listeners) {
    		this.listeners = listeners;
    	}
    	public Task() {
    		this.listeners = new ArrayList<>();
    	}
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            AnnotationEnricher annotationEnricher = new AnnotationEnricher(ontology, externalOntology.orElse(null));
            for(EventListener listener : listeners) {
            	annotationEnricher.addListener(listener);
            }
			annotationEnricher.enrichOntology();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }

    public ImportAnnotationsBulkWindow(OWLOntology ontology) {
    	super();
        this.ontology = ontology;
        this.externalOntology = Optional.empty();
        
        //Create the UI.
        JLabel externalOntologyLabel = new JLabel("External ontology to use (optional): ");
        this.externalOntologyTextField = new JTextField();
        int externalOntologyTextFieldHeight = externalOntologyTextField.getMinimumSize().height;
        int externalOntologyTextFieldWidth = externalOntologyTextField.getMaximumSize().width;
        this.externalOntologyTextField.setMaximumSize(new Dimension(externalOntologyTextFieldWidth, externalOntologyTextFieldHeight));
        this.loadExternalOntologyButton = new JButton("Load");
        loadExternalOntologyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (!externalOntology.isPresent()) {
					try {
						loadExternalOntology();
					} catch (Exception exception) {
						JOptionPane.showMessageDialog(null, exception.getLocalizedMessage());
						removeExternalOntology();
					}
				} else {
					removeExternalOntology();
				}
		    	setCursor(null);
			}
        	
        });
        this.textMessage = new JTextArea("All the entities in the ontology are going to be enriched by extracting the annotation assertion axioms from their IRI. This task is performed entity by entity and could be time-consuming. Press start to continue.", MESSAGE_ROWS, MESSAGE_COLUMNS);
        this.textMessage.setFont(new Font("Serif", Font.BOLD, 15));
        this.textMessage.setLineWrap(true);
        this.textMessage.setWrapStyleWord(true);
        this.textMessage.setOpaque(false);
        this.textMessage.setEditable(false);
        
        this.startButton = new JButton("Start");
        this.startButton.setActionCommand("start");
        this.startButton.addActionListener(this);

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setStringPainted(true);

        this.taskOutput = new JTextArea(LOG_ROWS, LOG_COLUMNS);
        this.taskOutput.setMargin(new Insets(5,5,5,5));
        this.taskOutput.setEditable(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.startButton);
        buttonPanel.add(this.progressBar);
        
        JPanel extOntologyPanel = new JPanel();
        extOntologyPanel.setLayout(new BoxLayout (extOntologyPanel, BoxLayout.X_AXIS));
        extOntologyPanel.add(externalOntologyLabel);
        extOntologyPanel.add(this.externalOntologyTextField);
        extOntologyPanel.add(this.loadExternalOntologyButton);
        
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        add(extOntologyPanel);
        add(new JScrollPane(this.textMessage));
        add(buttonPanel);
        add(new JScrollPane(this.taskOutput));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }
    
    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    	if (evt instanceof ImportAnnotationProgressEvent) {
			ImportAnnotationProgressEvent progress = (ImportAnnotationProgressEvent)evt;
			if (progress.getPercentageComplete() != null){
				progressBar.setValue(progress.getPercentageComplete());
			}
			if (progress.getMessage() != null && !progress.getMessage().isEmpty()) {
				taskOutput.append(progress.getMessage());
				taskOutput.append("\n");
				taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
			}
		}
    }

    /**
     * Invoked when the user presses the start button.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task(Arrays.asList(this));
        task.execute();
    }
    
    private void removeExternalOntology() {
    	if (this.externalOntology.isPresent()) {
    		this.externalOntology.get().getOWLOntologyManager().getOntologies().clear();
    	}
    	this.externalOntology = Optional.empty();
    	this.externalOntologyTextField.setText("");
    	this.externalOntologyTextField.setEditable(true);
    	this.loadExternalOntologyButton.setText("Load");
    	this.textMessage.setText("All the entities in the ontology are going to be enriched by extracting the annotation assertion axioms from its IRI. Press start to continue.");
    }
    
    private void loadExternalOntology() throws OWLOntologyCreationException {
    	IRI externalOntologyIRI = IRI.create(externalOntologyTextField.getText());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.externalOntology = Optional.of(manager.loadOntology(externalOntologyIRI));
		this.externalOntologyTextField.setEditable(false);
		this.loadExternalOntologyButton.setText("Remove");
		this.textMessage.setText(String.format("All the entities in the ontology are going to be enriched by extracting the annotation assertion axioms from the ontology %s. Press start to continue.", externalOntologyIRI.toQuotedString()));
    }
}
