import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Main {
    //class-wide variable declarations
    public static int noteNr;
    public static String note;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                noteTakingApp();
            }
        });
    }

    public static void noteTakingApp() {

        JFrame menuFrame = new JFrame("Note taking app");
        //menuFrame.setSize(500, 300);
        menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JFrame noteFrame = new JFrame("Note");
        noteFrame.setSize(500, 300);
        noteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //Below code recovers note number from .txt file.
        try (FileReader reader = new FileReader("Current note number storage.txt");
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line = bufferedReader.readLine();
            noteNr = Integer.parseInt(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Current note nr = " + noteNr);

        //Window listener declaration and definition.
        //As of now it stores the current note number
        //to a text file before exiting the program.
        //This will be useful later on.
        noteFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                try (FileWriter writer = new FileWriter("Current note number storage.txt")) {
                    writer.write(Integer.toString(noteNr));
                    System.out.println("Current note nr stored. Current note nr = " + noteNr);
                } catch (IOException a) {
                    a.printStackTrace();
                }

                noteFrame.dispose();
            }
        });

        //Component declarations start here
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.PAGE_AXIS));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));


        JTextArea noteInputArea = new JTextArea("Note goes here", 10, 25);
        noteInputArea.setLineWrap(true);
        noteInputArea.setFont(new Font("Century Gothic", Font.PLAIN, 15));

        JButton newNoteButton = new JButton("Open a new note");
        JButton loadNoteButton = new JButton("Load");
        JButton loadLastNoteButton = new JButton("Load last note");
        JButton deleteNoteButton = new JButton("Delete a note");
        JButton saveToButton = new JButton("Save To");
        JButton saveButton = new JButton("Save");


        JPopupMenu loadPopupMenu = createLoadSavePopupMenu(noteInputArea, false, noteFrame);
        loadNoteButton.setComponentPopupMenu(loadPopupMenu);

        JPopupMenu savePopUpMenu = createLoadSavePopupMenu(noteInputArea, true, noteFrame);
        saveToButton.setComponentPopupMenu(savePopUpMenu);

        JPopupMenu deletePopupMenu = createDeleteNotePopupMenu();
        deleteNoteButton.setComponentPopupMenu(deletePopupMenu);
        //Component declarations end here

        //Buttons configuration starts here
        newNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                noteInputArea.setText("");
                noteFrame.setVisible(true);
            }
        });

        loadNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                loadPopupMenu.show(loadNoteButton, 0, loadNoteButton.getHeight());
            }
        });

        loadLastNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                loadLastNote(noteInputArea);
                noteFrame.setVisible(true);
            }
        });

        deleteNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePopupMenu.show(deleteNoteButton, 0, deleteNoteButton.getHeight());
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                saveNote(noteInputArea);
            }
        });

        saveToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                savePopUpMenu.show(saveToButton, 0, saveToButton.getHeight());

            }

        });



        menuPanel.add(newNoteButton);
        menuPanel.add(loadNoteButton);
        menuPanel.add(loadLastNoteButton);
        menuPanel.add(deleteNoteButton);
        notePanel.add(noteInputArea);
        notePanel.add(saveButton);
        notePanel.add(saveToButton);

        menuFrame.add(menuPanel);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
        menuFrame.pack();

        noteFrame.add(notePanel);
        noteFrame.setLocationRelativeTo(null);
        noteFrame.pack();


    }

    //Below method serves to create a popup menu that is triggered after pressing the "Save to" button
    //at the bottom of the note frame, or the "Load" button in the main frame.
    private static JPopupMenu createLoadSavePopupMenu(JTextArea textArea, boolean isSaveButton, JFrame frame) {
        JPopupMenu popupMenu = new JPopupMenu("Choose note slot to save to");

        JMenuItem [] menuItems = new JMenuItem[5];

        //below actionlistener is being attached to menu items a couple of lines later.
       ActionListener menuActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                switch (command) {

                    case "Slot 1" : noteNr = 1;
                    break;

                    case "Slot 2" : noteNr = 2;
                    break;

                    case "Slot 3" : noteNr = 3;
                    break;

                    case "Slot 4" : noteNr = 4;
                    break;

                    case "Slot 5" : noteNr = 5;
                    break;

                    default:
                        System.out.println("LoadSavePopupMenuActionListener switch statement default - this should not appear!");
                    break;
                }


                //below if statements determine whether the user i trying to save a note or load one
                //and also perform corresponding operations (write/read)

                 if(isSaveButton) {
                    try (FileWriter writer = new FileWriter("Note" + noteNr + ".txt")) {
                        writer.write(textArea.getText());
                        System.out.println("Note " + noteNr + " saved.");
                    } catch (IOException a) {
                        a.printStackTrace();
                    }
                }

                if(!isSaveButton) {

                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("Note" + noteNr + ".txt"))) {

                        while((note = bufferedReader.readLine()) != null)
                        {
                            textArea.setText(note);
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    frame.setVisible(true);
                }
            }
       };

       //Menu items get their action listeners here
        for(int i = 0 ; i < 5 ; i++) {

            menuItems[i] = new JMenuItem("Slot " + (i + 1));
            popupMenu.add(menuItems[i]);

            menuItems[i].addActionListener(menuActionListener);
        }


        return popupMenu;
    }

    public static void saveNote(JTextArea textArea) {

        try (FileWriter writer = new FileWriter("Note" + noteNr + ".txt")) {
            writer.write(textArea.getText());
            System.out.println("Note " + noteNr + " saved.");
        } catch (IOException a) {
            a.printStackTrace();
        }
    }

    public static void loadLastNote(JTextArea textArea) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("Note" + noteNr + ".txt"))) {

            while((note = bufferedReader.readLine()) != null)
            {
                textArea.setText(note);
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    public static JPopupMenu createDeleteNotePopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu("Choose which note to delete");

        JMenuItem [] menuItems = new JMenuItem[5];

        ActionListener menuItemActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String command = e.getActionCommand();

                switch (command) {

                    case "Slot 1" : noteNr = 1;
                        break;

                    case "Slot 2" : noteNr = 2;
                        break;

                    case "Slot 3" : noteNr = 3;
                        break;

                    case "Slot 4" : noteNr = 4;
                        break;

                    case "Slot 5" : noteNr = 5;
                        break;

                    default:
                        System.out.println("deletePopupMenuActionListener switch statement default - this should not appear!");
                        break;
                }

                try(FileWriter writer = new FileWriter("Note" + noteNr + ".txt")) {
                    writer.write("");
                } catch (IOException a) {
                    a.printStackTrace();
                }

                System.out.println("Contents of Note" + noteNr + ".txt deleted");
            }
        };

        for(int i = 0 ; i < 5 ; i++) {
            menuItems[i] = new JMenuItem();
            menuItems[i].setText("Slot " + (i+1));
            popupMenu.add(menuItems[i]);

            menuItems[i].addActionListener(menuItemActionListener);
        }

        return popupMenu;
    }
}



/*
1. Dodac opcje zapisywania notatek do osobnych plikow DONE
2. Dodac menu z wyborem opcji. Po odpaleniu programu odpalac ma sie menu, notatki za to w osobnych oknach,
    dopiero po wyborze odopowiednich opcji DONE
3. Dodac opcje do menu:
    a) Odczyt notatek DONE
    b) Otwarcie nowej notatki DONE
    c) Usuwanie notatek DONE
    d) Przycisk do cofania zmian w notatce
4. Opisac kod komentarzami! WORK IN PROGRESS
5. Dodac jakies opcje formatowania tekstu   *Moze wymagac zmiany z JTextArea na JEditorPane
*/