/*

JAdhoc ver 0.2 - Java AODV (RFC 3561) Protocol Handler
Copyright 2003-2004 ComNets, University of Bremen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/

package jadhoc.gui;

import jadhoc.gui.ConfigDialogZaurus;
import jadhoc.gui.GUIInterface;
import jadhoc.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jadhoc.conf.*;
import jadhoc.net.*;
import jadhoc.other.*;

/**
* Class that manages the main user interface of the Protocol
* Handler for Linux
*
* @author : Asanga Udugama
* @date : 12-feb-2004
* @email : adu@comnets.uni-bremen.de
*
* @modification history :
*  11-feb-2004 - introduced GUIInterface
*             	 class name chaged
*/
public class GUIZaurus extends Frame implements GUIInterface {
        ConfigInfo cfgInfo;
	CurrentInfo curInfo;
	JAdhoc jadhoc;

        // graphical components
        Button btnStart, btnQuit, btnInfo, btnConfig;
        Label lblOSInUse, lblIPVersion, lblInterfaceName, lblIPAddr, lblLastSeqNum, lblLastRREQID,
	      lblRouteIPAddr[], lblRouteStatusFlag[], lblRouteHopCount[], lblHeading;
	Panel pnlLabelPanel, pnlButtonPanel, pnlMainPanel, pnlRoutePanel;

	ConfigDialogZaurus cfgDialog;

	// error dialog components
	Dialog errorDialog;
	Panel pnlError;
	Label lblError;
	Button btnErrorExit;

	// info dialog components
	Dialog infoDialog;
	Panel pnlInfo, pnlInfoButton;
	Label lblInfo;
	String info[] =  { "This is an AODV Protocol Handler",
			 "developed by ComNets, a research",
			 "group at the Communications",
			 "Department (ikom) of the University",
			 "of Bremen, Germany - 2004",
                       	 "This software conforms to the",
			 "IETF's AODV protocol as specified",
			 "in RFC 3561" };
	Button btnInfoExit;

	// temp variables
	int rowCount, i;
	String listStr;

	// redraw time variables
	private static final int NEXT_UPDATE_MILLISECONDS = 50;
	private long nextUpdateTime;


	public static final int FRAME_POSITION_TOP = 0;
	public static final int FRAME_POSITION_LEFT = 0;
	//public static final int FRAME_WIDTH = 225;
	//public static final int FRAME_HEIGHT = 250;
	public static final int MAX_ROUTES_DISPLAYED = 5;


        /**
        * Contructor creates and displays the main screen. If the
        * configuration info read status indicates that the config
        * file was not present, then the configuration user interface
        * is also called.
        * @param ConfigInfo cfg - config info object
        * @int readStat - status to indicate whether config file
        *                 is available or not
        */
        public GUIZaurus(ConfigInfo cfg, CurrentInfo cur, JAdhoc ja) {
                cfgInfo = cfg;
		curInfo = cur;
		jadhoc = ja;

                // build the graphical components

		pnlMainPanel = new Panel(new BorderLayout());
		//pnlMainPanel = new Panel(new FlowLayout());

		pnlLabelPanel = new Panel(new GridLayout(6, 1));
		pnlButtonPanel = new Panel(new FlowLayout());

		lblOSInUse = new Label();
         	pnlLabelPanel.add(lblOSInUse);

		lblIPVersion = new Label();
         	pnlLabelPanel.add(lblIPVersion);

		lblInterfaceName = new Label();
         	pnlLabelPanel.add(lblInterfaceName);

		lblIPAddr = new Label();
         	pnlLabelPanel.add(lblIPAddr);

		lblLastSeqNum = new Label();
         	pnlLabelPanel.add(lblLastSeqNum);

		lblLastRREQID = new Label();
         	pnlLabelPanel.add(lblLastRREQID);

		pnlMainPanel.add(pnlLabelPanel, BorderLayout.NORTH);

		// setup route entry panel
		pnlRoutePanel = new Panel(new GridLayout(MAX_ROUTES_DISPLAYED + 1, 3));
		lblRouteIPAddr = new Label[MAX_ROUTES_DISPLAYED];
		lblRouteStatusFlag = new Label[MAX_ROUTES_DISPLAYED];
		lblRouteHopCount = new Label[MAX_ROUTES_DISPLAYED];

		lblHeading = new Label("Destination ");
		lblHeading.setAlignment(Label.LEFT);
		pnlRoutePanel.add(lblHeading);
		lblHeading = new Label("Status");
		lblHeading.setAlignment(Label.CENTER);
		pnlRoutePanel.add(lblHeading);
		lblHeading = new Label("Hop Count");
		lblHeading.setAlignment(Label.CENTER);
		pnlRoutePanel.add(lblHeading);

		for(i = 0; i < MAX_ROUTES_DISPLAYED; i++) {
			lblRouteIPAddr[i] = new Label("-");
			lblRouteIPAddr[i].setAlignment(Label.LEFT);
			lblRouteStatusFlag[i] = new Label("-");
			lblRouteStatusFlag[i].setAlignment(Label.CENTER);
			lblRouteHopCount[i] = new Label("-");
			lblRouteHopCount[i].setAlignment(Label.CENTER);

			pnlRoutePanel.add(lblRouteIPAddr[i]);
			pnlRoutePanel.add(lblRouteStatusFlag[i]);
			pnlRoutePanel.add(lblRouteHopCount[i]);
		}

		//lblRouteInfo[0].setText("No Routes");
		//pnlRoutePanel.add(new Label("No Routes"));
		//lstRouteList = new List(30, false);
		//lstRouteList.add("                                         ");

         	pnlMainPanel.add(pnlRoutePanel, BorderLayout.CENTER);

		btnStart = new Button("Start");
         	pnlButtonPanel.add(btnStart);

		btnQuit = new Button("Quit");
         	pnlButtonPanel.add(btnQuit);

                btnConfig = new Button("Configure");
         	pnlButtonPanel.add(btnConfig);

		btnInfo = new Button("Information");
         	pnlButtonPanel.add(btnInfo);

		pnlMainPanel.add(pnlButtonPanel, BorderLayout.SOUTH);

                setPrimaryHeaderInfo();
                setSecondaryHeaderInfo();

                btnStart.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						startApplication();
					}
				});

                btnQuit.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                exitApplication();
                                        }
                                });

                btnConfig.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                configApplication();
                                        }
                                });

                btnInfo.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                dispInformation();
                                        }
                                });

                addWindowListener(
                                new WindowAdapter() {
                                        public void windowClosing(WindowEvent e) {
                                                exitApplication();
                                        }
                                });


		setLayout(new BorderLayout());
		add(pnlMainPanel, BorderLayout.CENTER);
                setTitle("J-Adhoc - AODV Protocol Handler (ver 0.2)");
                setLocation(FRAME_POSITION_TOP, FRAME_POSITION_LEFT);
                setSize(getToolkit().getScreenSize());
                show();
        }

        /**
        * Method to perform when the user requests a quit application
        */
        void exitApplication() {
                stopApplication();
                jadhoc.exitApplication();
        }

        /**
        * Method to perform when the user requests a start AODV protocol
        * handler
        */
        void startApplication() {
		boolean success;

		// init update timer variables
		nextUpdateTime = (new Date()).getTime() + NEXT_UPDATE_MILLISECONDS;

                // enable/disable buttons for start operation
                setButtonsForStart();

		success = jadhoc.startApplication();
		if(!success) {
			setButtonsForStop();
		}
        }

        /**
        * Method to perform when the user requests a stop AODV protocol
        * handler.
        */
        void stopApplication() {
                setButtonsForStop();
		jadhoc.stopApplication();
        }

        /**
        * Method to perform when the AODV protocol handler itself requests
        * to stop the AODV protocol handler
        */
        public void stopAppFromRouteManager() {
                setButtonsForStop();
        }


        /**
        * Method to perform when the user requests to configure the
        * the application. This will result in showing the configuration
        * user interface.
        */
        void configApplication() {
		cfgDialog = new ConfigDialogZaurus(cfgInfo, this);
		if(cfgInfo.infoChanged) {
                	setPrimaryHeaderInfo();
                	cfgInfo.infoChanged = false;
		}
        }


        /**
        * Method to display information about this AODV protocol
        * handler
        */
        void dispInformation() {

		infoDialog = new Dialog(this, "J-Adhoc - Information", true);
		pnlInfo = new Panel(new FlowLayout());
		for(i = 0; i < info.length; i++) {
			pnlInfo.add(new Label(info[i]));
		}
		btnInfoExit = new Button("Return");
                btnInfoExit.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                infoDialog.hide();
                                        }
                                });
		pnlInfoButton = new Panel(new FlowLayout());
		pnlInfoButton.add(btnInfoExit);
		infoDialog.add("Center", pnlInfo);
		infoDialog.add("South", pnlInfoButton);
		infoDialog.setBounds(getBounds().x + 10, getBounds().y + 10,
					getBounds().width - 20, getBounds().height - 20);
		infoDialog.show();
        }

        /**
        * Method to set the primary header information in the main user
        * interface.
        */
        void setPrimaryHeaderInfo() {
                lblOSInUse.setText("Operating System : " + cfgInfo.osInUseVal);
                lblIPVersion.setText("IP Version : IPv" + cfgInfo.ipVersionVal);
                lblIPAddr.setText("IP Address : " + cfgInfo.ipAddress);
                lblInterfaceName.setText("Interface Name : " + cfgInfo.ifaceNameVal);
        }

        /**
        * Method to set the secondary header information in the main user
        * interface.
        */
        void setSecondaryHeaderInfo() {
                lblLastSeqNum.setText("Last Seq Num : " + curInfo.lastSeqNum);
                lblLastRREQID.setText("Last RREQ ID : " + curInfo.lastRREQID);
        }

        /**
        * Method to set the button status of the main user interface
        * when the user starts AODV protocol handler
        */
        void setButtonsForStart() {
                btnStart.setEnabled(false);
                btnConfig.setEnabled(false);
        }

        /**
        * Method to set the button status of the main user interface
        * when the user stops AODV protocol handler
        */
        void setButtonsForStop() {
                btnStart.setEnabled(true);
        }

        /**
        * Method to redisplay the route table on the main user
        * interface. This is called every time a change is made
        * to the internal routing information.
        */
        public void redrawTable() {
		if(nextUpdateTime > (new Date()).getTime()) {
			return;
		}

		setSecondaryHeaderInfo();
		buildList();

		//repaint();

		pnlLabelPanel.repaint();
		pnlRoutePanel.repaint();

		// update time
		nextUpdateTime = (new Date()).getTime() + NEXT_UPDATE_MILLISECONDS;
        }

	public void displayError(String msg) {

		errorDialog = new Dialog(this, "J-Adhoc - Error Message", true);
		lblError = new Label(msg);
		pnlError = new Panel();
		pnlError.add(lblError);
		btnErrorExit = new Button("Return");
                btnErrorExit.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                errorDialog.hide();
                                        }
                                });
		errorDialog.add("Center", pnlError);
		errorDialog.add("South", btnErrorExit);
		errorDialog.setBounds(getBounds().x + 10, getBounds().y + 10,
					getBounds().width - 20, getBounds().height - 20);
		errorDialog.show();
	}

	void buildList() {

  		try {
			rowCount = jadhoc.getRouteCount();
		} catch(Exception e) {
			rowCount = 0;
		}

		for(i = 0; i < MAX_ROUTES_DISPLAYED; i++) {
			if(i < rowCount) {
				lblRouteIPAddr[i].setText((String) jadhoc.getRouteValueAt(i, 0));
				lblRouteStatusFlag[i].setText((String) jadhoc.getRouteValueAt(i, 3));
				lblRouteHopCount[i].setText((String) jadhoc.getRouteValueAt(i, 5));
			} else {
				lblRouteIPAddr[i].setText("-");
				lblRouteStatusFlag[i].setText("-");
				lblRouteHopCount[i].setText("-");
			}
		}
	}
}
