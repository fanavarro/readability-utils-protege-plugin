package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.awt.Cursor;
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.ontology_annotation_enrichment.AnnotationEnricher;

public class ImportAnnotationsWindow extends JPanel
                             implements ActionListener, PropertyChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2875550578135610005L;
	private static final int LOG_ROWS = 20;
	private static final int LOG_COLUMNS = 20;
	private static final int MESSAGE_ROWS= 2;
	private static final int MESSAGE_COLUMNS = 20;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
    private OWLOntology ontology;
    private OWLEntity entity;
    

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
            AnnotationEnricher annotationEnricher = new AnnotationEnricher(ontology);
            for(EventListener listener : listeners) {
            	annotationEnricher.addListener(listener);
            }
			annotationEnricher.enrichEntity(entity);
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            //Toolkit.getDefaultToolkit().beep();
        	taskOutput.append("Done.");
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }

    public ImportAnnotationsWindow(OWLOntology ontology, OWLEntity entity) {
        //super(new BorderLayout());
    	//super(new BoxLayout ());
    	super();
        this.ontology = ontology;
        this.entity = entity;
        //Create the UI.
        JTextArea textMessage = new JTextArea(String.format("The entity %s is going to be enriched by extracting the annotation assertion axioms from its IRI. Press start to continue.", entity.getIRI().toQuotedString()), MESSAGE_ROWS, MESSAGE_COLUMNS);
        textMessage.setFont(new Font("Serif", Font.BOLD, 15));
        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);
        textMessage.setOpaque(false);
        textMessage.setEditable(false);
        //textMessage.setMargin(new Insets(5,5,5,5));
        
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        taskOutput = new JTextArea(LOG_ROWS, LOG_COLUMNS);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));    
        add(new JScrollPane(textMessage));
        add(panel);
        add(new JScrollPane(taskOutput));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }
    
    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    	if (evt instanceof ImportAnnotationProgressEvent) {
			ImportAnnotationProgressEvent progress = (ImportAnnotationProgressEvent)evt;
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
        this.taskOutput.append(String.format("Enriching %s\n", this.entity.getIRI().toQuotedString()));
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task(Arrays.asList(this));
        task.execute();
    }
}