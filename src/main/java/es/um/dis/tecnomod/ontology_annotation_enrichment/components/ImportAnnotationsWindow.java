package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.ontology_annotation_enrichment.AnnotationEnricher;

public class ImportAnnotationsWindow extends JPanel
                             implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2875550578135610005L;
	private static final int LOG_ROWS = 20;
	private static final int LOG_COLUMNS = 20;
	private static final int MESSAGE_ROWS= 2;
	private static final int MESSAGE_COLUMNS = 20;
	private ImportAnnotationsProgressBar progressBar;
    private JButton startButton;
    private ImportAnnotationsProgressLog taskOutput;
    private Task task;
    private OWLOntology ontology;
    

    class Task extends SwingWorker<Void, Void> {    	
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            AnnotationEnricher annotationEnricher = new AnnotationEnricher(ontology);
            annotationEnricher.addListener(progressBar);
            annotationEnricher.addListener(taskOutput);
			annotationEnricher.enrichOntology();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            //Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }

    public ImportAnnotationsWindow(OWLOntology ontology) {
        //super(new BorderLayout());
    	//super(new BoxLayout ());
    	super();
        this.ontology = ontology;
        //Create the UI.
        JTextArea textMessage = new JTextArea("All the entities in the ontology are going to be enriched by extracting the annotation assertion axioms from their IRI. This task is performed entity by entity and could be time-consuming. Press start to continue.", MESSAGE_ROWS, MESSAGE_COLUMNS);
        textMessage.setFont(new Font("Serif", Font.BOLD, 15));
        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);
        textMessage.setOpaque(false);
        textMessage.setEditable(false);
        //textMessage.setMargin(new Insets(5,5,5,5));
        
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new ImportAnnotationsProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new ImportAnnotationsProgressLog(LOG_ROWS, LOG_COLUMNS);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
        
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));    
        add(new JScrollPane(textMessage));
        add(panel);
        add(new JScrollPane(taskOutput));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.execute();
    }
}
