// Lots of source code thanks to Katelyn from my class
// Also parts of code adapted from
// http://java.sun.com/docs/books/tutorial/uiswing/components/button.html

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Window extends JPanel implements ActionListener {
  private JTextPane currList;
  private JTextPane txtInput;
  private JTextPane txtOutput;
  private JButton execute;
  private JButton spacer;
  private JRadioButton btn_AddHead;
  private JRadioButton btn_AddTail;
  private JRadioButton btn_AddOrder;
  private JRadioButton btn_DelHead;
  private JRadioButton btn_DelTail;
  private JRadioButton btn_DelVal;
  private JRadioButton btn_ReportSorted;
  private JRadioButton btn_CountValues;
  private JRadioButton btn_ClrList;
  private JLabel lblSpacer;
  private ButtonGroup grp_Buttons;
  private List myList;
  private int currentAction=0;

  public Window(Container pane) {
		
		// All test cases use the List(String[] inData) constructor
		// first test case: this is a valid, unsorted list
		String[] testCase1 = {"cat","dog","cow","Animal","aardvark","pig","zebra"};
		// second test case: this is a valid, sorted list
		String[] testCase2 = {"Animal","aardvark","cat","cow","dog","pig","zebra"};
		// third test case: this is an invalid list, it contains a numeral
		String[] testCase3 = {"cat","dog","cow","Animal","aard456vark","pig","zebra"};
		// fourth test case: this is an invalid list, it contains accented chars
		String[] testCase4 = {"cát","dóg","çów","Ánímál","àárdvärk","pïg","zëbrä"};
		// fifth test case: this is a valid list, with a space in one of the values
		String[] testCase5 = {"kitty cat","dog","cow","Animal","aardvark","pig","zebra"};
		// sixth test case: this is a valid empty list
		String[] testCase6 = {};
		// seventh test case: this is a valid list with multiple duplicates
		String[] testCase7 = {"cat","dog","cat","pig","dog","cat","cat"};
		// eigth test case: this is a valid list with all duplicates.
		// Try adding another duplicate using the insert-in-order method
		String[] testCase8 = {"cat","cat","cat","cat"};
		
		try {
			myList = new List(testCase2);
			myList.printList();
		} catch(InvalidInputDataException e) {
			txtOutput.setText("ERROR: Invalid character in value! "+
				"Substituting empty list.");
			myList=new List();
		}

    pane.setLayout(new GridBagLayout());
	 
    currList = new JTextPane();
    GridBagConstraints textConstraints = new GridBagConstraints();
    textConstraints.fill = GridBagConstraints.BOTH;
    textConstraints.gridx = 0;
    textConstraints.gridy = 0;
    textConstraints.gridwidth = 10;
    textConstraints.gridheight = 12;
    pane.add(currList, textConstraints);
	 
    lblSpacer = new JLabel("                                              ");
    GridBagConstraints spacerConstraints = new GridBagConstraints();
    spacerConstraints.fill = GridBagConstraints.BOTH;
    spacerConstraints.gridx = 0;
    spacerConstraints.gridy = 13;
    spacerConstraints.gridwidth = 10;
    spacerConstraints.gridheight = 1;
    pane.add(lblSpacer, spacerConstraints);

    txtInput = new JTextPane();
    GridBagConstraints textInputConstraints = new GridBagConstraints();
    textInputConstraints.fill = GridBagConstraints.BOTH;
    textInputConstraints.gridx = 12;
    textInputConstraints.gridy = 10;
    textInputConstraints.gridwidth = 4;
    textInputConstraints.gridheight = 1;
    pane.add(txtInput, textInputConstraints);

    txtOutput = new JTextPane();
    GridBagConstraints textOutputConstraints = new GridBagConstraints();
    textOutputConstraints.fill = GridBagConstraints.BOTH;
    textOutputConstraints.gridx = 12;
    textOutputConstraints.gridy = 11;
    textOutputConstraints.gridwidth = 4;
    textOutputConstraints.gridheight = 1;
    pane.add(txtOutput, textOutputConstraints);

    btn_AddHead = new JRadioButton("Add object as head");
    GridBagConstraints addHeadConstraints = new GridBagConstraints();
    addHeadConstraints.fill = GridBagConstraints.BOTH;
    addHeadConstraints.gridx = 11;
    addHeadConstraints.gridy = 0;
    addHeadConstraints.gridwidth = 5;
    addHeadConstraints.gridheight = 1;
    btn_AddHead.setActionCommand("1");
    btn_AddHead.setSelected(true);
    pane.add(btn_AddHead, addHeadConstraints);

    btn_AddTail = new JRadioButton("Add object as tail");
    GridBagConstraints addTailConstraints = new GridBagConstraints();
    addTailConstraints.fill = GridBagConstraints.BOTH;
    addTailConstraints.gridx = 11;
    addTailConstraints.gridy = 1;
    addTailConstraints.gridwidth = 5;
    addTailConstraints.gridheight = 1;
    btn_AddTail.setActionCommand("2");
    pane.add(btn_AddTail, addTailConstraints);

    btn_AddOrder = new JRadioButton("Add object in order");
    GridBagConstraints addOrderConstraints = new GridBagConstraints();
    addOrderConstraints.fill = GridBagConstraints.BOTH;
    addOrderConstraints.gridx = 11;
    addOrderConstraints.gridy = 2;
    addOrderConstraints.gridwidth = 5;
    addOrderConstraints.gridheight = 1;
    btn_AddOrder.setActionCommand("3");
    pane.add(btn_AddOrder, addOrderConstraints);

    btn_DelHead = new JRadioButton("Delete first object");
    GridBagConstraints delHeadConstraints = new GridBagConstraints();
    delHeadConstraints.fill = GridBagConstraints.BOTH;
    delHeadConstraints.gridx = 11;
    delHeadConstraints.gridy = 3;
    delHeadConstraints.gridwidth = 5;
    delHeadConstraints.gridheight = 1;
    btn_DelHead.setActionCommand("4");
    pane.add(btn_DelHead, delHeadConstraints);

    btn_DelTail = new JRadioButton("Delete last object");
    GridBagConstraints delTailConstraints = new GridBagConstraints();
    delTailConstraints.fill = GridBagConstraints.BOTH;
    delTailConstraints.gridx = 11;
    delTailConstraints.gridy = 4;
    delTailConstraints.gridwidth = 5;
    delTailConstraints.gridheight = 1;
    btn_DelTail.setActionCommand("5");
    pane.add(btn_DelTail, delTailConstraints);
	 
    btn_DelVal = new JRadioButton("Delete first instance of object");
    GridBagConstraints delValConstraints = new GridBagConstraints();
    delValConstraints.fill = GridBagConstraints.BOTH;
    delValConstraints.gridx = 11;
    delValConstraints.gridy = 5;
    delValConstraints.gridwidth = 5;
    delValConstraints.gridheight = 1;
    btn_DelVal.setActionCommand("6");
    pane.add(btn_DelVal, delValConstraints);
	 
    btn_ReportSorted = new JRadioButton("Report whether list is sorted");
    GridBagConstraints repSortConstraints = new GridBagConstraints();
    repSortConstraints.fill = GridBagConstraints.BOTH;
    repSortConstraints.gridx = 11;
    repSortConstraints.gridy = 6;
    repSortConstraints.gridwidth = 5;
    repSortConstraints.gridheight = 1;
    btn_ReportSorted.setActionCommand("7");
    pane.add(btn_ReportSorted, repSortConstraints);
	 
    btn_CountValues = new JRadioButton("Report how many instances of value exist");
    GridBagConstraints cntValsConstraints = new GridBagConstraints();
    cntValsConstraints.fill = GridBagConstraints.BOTH;
    cntValsConstraints.gridx = 11;
    cntValsConstraints.gridy = 7;
    cntValsConstraints.gridwidth = 5;
    cntValsConstraints.gridheight = 1;
    btn_CountValues.setActionCommand("8");
    pane.add(btn_CountValues, cntValsConstraints);
	 
    btn_ClrList = new JRadioButton("Clear list");
    GridBagConstraints clrListConstraints = new GridBagConstraints();
    clrListConstraints.fill = GridBagConstraints.BOTH;
    clrListConstraints.gridx = 11;
    clrListConstraints.gridy = 8;
    clrListConstraints.gridwidth = 5;
    clrListConstraints.gridheight = 1;
    btn_ClrList.setActionCommand("9");
    pane.add(btn_ClrList, clrListConstraints);

    //Group the radio buttons.
    grp_Buttons = new ButtonGroup();
    grp_Buttons.add(btn_AddHead);
    grp_Buttons.add(btn_AddTail);
    grp_Buttons.add(btn_AddOrder);
    grp_Buttons.add(btn_DelHead);
    grp_Buttons.add(btn_DelTail);
    grp_Buttons.add(btn_DelVal);
    grp_Buttons.add(btn_ReportSorted);
    grp_Buttons.add(btn_CountValues);
    grp_Buttons.add(btn_ClrList);

    //Register a listener for the radio buttons.
    btn_AddHead.addActionListener(this);
    btn_AddTail.addActionListener(this);
    btn_AddOrder.addActionListener(this);
    btn_DelHead.addActionListener(this);
    btn_DelTail.addActionListener(this);
    btn_DelVal.addActionListener(this);
    btn_ReportSorted.addActionListener(this);
    btn_CountValues.addActionListener(this);
    btn_ClrList.addActionListener(this);


    execute = new JButton("Do action");
    GridBagConstraints buttonConstraints = new GridBagConstraints();
    buttonConstraints.fill = GridBagConstraints.BOTH;
    buttonConstraints.gridx = 17;
    buttonConstraints.gridy = 10;
    buttonConstraints.gridwidth = 1;
    buttonConstraints.gridheight = 1;
    pane.add(execute, buttonConstraints);    

    execute.setActionCommand("execute");
    execute.addActionListener(this);
	 
    currList.setText(myList.printList());
	 txtInput.setText("INPUT");
	 txtOutput.setText("OUTPUT");
  } // constructor

  public void actionPerformed(ActionEvent action) {
    if (action.getActionCommand().equals("1"))
	   currentAction=1;
    if (action.getActionCommand().equals("2"))
	   currentAction=2;
    if (action.getActionCommand().equals("3"))
	   currentAction=3;
    if (action.getActionCommand().equals("4"))
	   currentAction=4;
    if (action.getActionCommand().equals("5"))
	   currentAction=5;
    if (action.getActionCommand().equals("6"))
	   currentAction=6;
    if (action.getActionCommand().equals("7"))
	   currentAction=7;
    if (action.getActionCommand().equals("8"))
	   currentAction=8;
    if (action.getActionCommand().equals("9"))
	   currentAction=9;
    
	 if (action.getActionCommand().equals("execute")) {
	   if(currentAction==1) {
        try {
          myList.addFirstObject(txtInput.getText());
          currList.setText(myList.printList());
        } catch(InvalidInputDataException e) {
          txtOutput.setText("ERROR: Invalid value entered");
        }
      }
      if(currentAction==2) {
        try {
          myList.addLastObject(txtInput.getText());
          currList.setText(myList.printList());
        } catch(InvalidInputDataException e) {
          txtOutput.setText("ERROR: Invalid value entered");
        }
      }
      if(currentAction==3) {
        try {
          myList.addSortObject(txtInput.getText());
          currList.setText(myList.printList());
        } catch(InvalidInputDataException e) {
          txtOutput.setText("ERROR: Invalid value entered");
        } catch(ListNotSortedException e) {
          txtOutput.setText("ERROR: List not sorted");
        }
      }
      if(currentAction==4) {
        try {
          myList.delFirstObject();
          currList.setText(myList.printList());
        } catch(DeleteFromEmptyListException e) {
          txtOutput.setText("ERROR: Cannot delete from empty list");
        }
      }
      if(currentAction==5) {
        try {
          myList.delLastObject();
          currList.setText(myList.printList());
        } catch(DeleteFromEmptyListException e) {
          txtOutput.setText("ERROR: Cannot delete from empty list");
        }
      }
      if(currentAction==6) {
        try {
          myList.delSpecObject(txtInput.getText());
          currList.setText(myList.printList());
        } catch(ItemNotFoundException e) {
          txtOutput.setText("ERROR: Item not found");
        } catch(DeleteFromEmptyListException e) {
          txtOutput.setText("ERROR: Cannot delete from empty list");
        }
      }
      if(currentAction==7) {
        if(myList.sorted())
          txtOutput.setText("List is sorted.");
        else
          txtOutput.setText("List is not sorted.");
      }
      if(currentAction==8) {
		  String res=Integer.toString(myList.howMany(txtInput.getText()));
        txtOutput.setText(res);
      }
      if(currentAction==9) {
		  myList.clear();
        currList.setText(myList.printList());
      }
    }
  }
}
