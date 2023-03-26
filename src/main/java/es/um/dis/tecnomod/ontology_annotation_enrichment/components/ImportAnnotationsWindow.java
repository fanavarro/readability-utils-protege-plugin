package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.ontology_annotation_enrichment.AnnotationEnricher;

public class ImportAnnotationsWindow extends JPanel
                             implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2875550578135610005L;
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
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
        }
    }

    public ImportAnnotationsWindow(OWLOntology ontology) {
        super(new BorderLayout());
        this.ontology = ontology;
        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new ImportAnnotationsProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new ImportAnnotationsProgressLog(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
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
        //task.addPropertyChangeListener(this);
        task.execute();
    }
}
