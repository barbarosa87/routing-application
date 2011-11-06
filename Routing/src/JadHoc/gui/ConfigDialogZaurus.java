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

import java.awt.*;
import java.awt.event.*;

import jadhoc.conf.*;

/**
* Class to display the configuration user interface and update
* the changes to the configuration in a Zaurus environment.
*
* @author : Asanga Udugama
* @date : 16-feb-2004
* @email : adu@comnets.uni-bremen.de
*
*/
public class ConfigDialogZaurus extends Dialog {
        ConfigInfo cfgInfo, tempCfgInfo;
        Panel pnlCfgPanel, pnlBtnPanel;
        Button btnUpdate, btnCancel;
	ScrollPane scrCfgScrollPane;

	Label lblConfigFieldHeading[];
	TextField txtConfigFieldData[];

	int i;
	//public static final int DIALOG_WIDTH = 500;
	//public static final int DIALOG_HEIGHT = 280;

	// error dialog components
	Panel pnlError;
	Label lblError;

        /**
        * Constructs a configuration information display and shows it.
        * @param ConfigInfo cfg - the configuration object
        * @param GUI gui - main user interface
        */
        public ConfigDialogZaurus(ConfigInfo cfg, Frame gui) {
                super(gui, "J-Adhoc Configuration", true);

                cfgInfo = cfg;

                tempCfgInfo = new ConfigInfo();
                tempCfgInfo.setValuesUsing(cfgInfo);

		// setup fields
                pnlCfgPanel = new Panel();
                pnlCfgPanel.setLayout(new GridLayout(getRowCount(), 2));
		lblConfigFieldHeading = new Label[getRowCount()];
		txtConfigFieldData = new TextField[getRowCount()];
		for(i = 0; i < getRowCount(); i++) {
			lblConfigFieldHeading[i] = new Label(getConfigFieldInfo(i, 0));
			txtConfigFieldData[i] = new TextField(getConfigFieldInfo(i, 1), 10);
		}
		for(i = 0; i < getRowCount(); i++) {
			pnlCfgPanel.add(lblConfigFieldHeading[i]);
			pnlCfgPanel.add(txtConfigFieldData[i]);
		}
		scrCfgScrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrCfgScrollPane.add(pnlCfgPanel);

		// setup buttons
		pnlBtnPanel = new Panel();
                btnUpdate = new Button("Update");
                btnUpdate.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                updateConfigInfo();
                                        }
                                });
                btnCancel = new Button("Cancel");
                btnCancel.addActionListener(
                                new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                cancelConfigUpdate();
                                        }
                                });
                pnlBtnPanel.add(btnUpdate);
                pnlBtnPanel.add(btnCancel);

                addWindowListener(
                                new WindowAdapter() {
                                        public void windowClosing(WindowEvent e) {
                                                cancelConfigUpdate();
                                        }
                                });

		lblError = new Label("");
		pnlError = new Panel();
		pnlError.add(lblError);

		setLayout(new BorderLayout());
		add(pnlError, BorderLayout.NORTH);
		add(scrCfgScrollPane, BorderLayout.CENTER);
		add(pnlBtnPanel, BorderLayout.SOUTH);

		setBounds(gui.getBounds().x + 1, gui.getBounds().y + 10,
				gui.getBounds().width - 2, gui.getBounds().height - 20);
		show();
        }

	private String getConfigFieldInfo(int row, int column) {
		if(row == 0 && column == 0)
                	return tempCfgInfo.executionModeStr;
                else if(row == 0 && column == 1)
                	return tempCfgInfo.executionMode;
                else if(row == 1 && column == 0)
                        return tempCfgInfo.osInUseStr;
                else if(row == 1 && column == 1)
                        return tempCfgInfo.osInUse;
                else if(row == 2 && column == 0)
                        return tempCfgInfo.ipVersionStr;
                else if(row == 2 && column == 1)
                        return tempCfgInfo.ipVersion;
                else if(row == 3 && column == 0)
                        return tempCfgInfo.ipAddressStr;
                else if(row == 3 && column == 1)
                        return tempCfgInfo.ipAddress;
                else if(row == 4 && column == 0)
                        return tempCfgInfo.ifaceNameStr;
                else if(row == 4 && column == 1)
                        return tempCfgInfo.ifaceName;
                else if(row == 5 && column == 0)
                        return tempCfgInfo.ipAddressGatewayStr;
                else if(row == 5 && column == 1)
                        return tempCfgInfo.ipAddressGateway;
                else if(row == 6 && column == 0)
                        return tempCfgInfo.loIfaceNameStr;
                else if(row == 6 && column == 1)
                        return tempCfgInfo.loIfaceName;
		else if(row == 7 && column == 0)
                        return tempCfgInfo.loggingStatusStr;
                else if(row == 7 && column == 1)
                        return tempCfgInfo.loggingStatus;
		else if(row == 8 && column == 0)
                        return tempCfgInfo.loggingLevelStr;
                else if(row == 8 && column == 1)
                        return tempCfgInfo.loggingLevel;
                else if(row == 9 && column == 0)
                        return tempCfgInfo.logFileStr;
                else if(row == 9 && column == 1)
                        return tempCfgInfo.logFile;
                else if(row == 10 && column == 0)
                        return tempCfgInfo.pathToSystemCmdsStr;
                else if(row == 10 && column == 1)
                        return tempCfgInfo.pathToSystemCmds;
		else if(row == 11 && column == 0)
                        return tempCfgInfo.onlyDestinationStr;
                else if(row == 11 && column == 1)
                        return tempCfgInfo.onlyDestination;
                else if(row == 12 && column == 0)
                        return tempCfgInfo.gratuitousRREPStr;
                else if(row == 12 && column == 1)
                        return tempCfgInfo.gratuitousRREP;
                else if(row == 13 && column == 0)
                        return tempCfgInfo.RREPAckRequiredStr;
                else if(row == 13 && column == 1)
                        return tempCfgInfo.RREPAckRequired;
                else if(row == 14 && column == 0)
                        return tempCfgInfo.ipAddressMulticastStr;
                else if(row == 14 && column == 1)
                        return tempCfgInfo.ipAddressMulticast;
                else if(row == 15 && column == 0)
                        return tempCfgInfo.RERRSendingModeStr;
                else if(row == 15 && column == 1)
                        return tempCfgInfo.RERRSendingMode;
                else if(row == 16 && column == 0)
                        return tempCfgInfo.deletePeriodModeStr;
                else if(row == 16 && column == 1)
                        return tempCfgInfo.deletePeriodMode;
                else if(row == 17 && column == 0)
                        return tempCfgInfo.routeDiscoveryModeStr;
                else if(row == 17 && column == 1)
                        return tempCfgInfo.routeDiscoveryMode;
                else if(row == 18 && column == 0)
                        return tempCfgInfo.packetBufferingStr;
                else if(row == 18 && column == 1)
                        return tempCfgInfo.packetBuffering;
		else if(row == 19 && column == 0)
                        return tempCfgInfo.activeRouteTimeoutStr;
                else if(row == 19 && column == 1)
                        return tempCfgInfo.activeRouteTimeout;
                else if(row == 20 && column == 0)
                        return tempCfgInfo.allowedHelloLossStr;
                else if(row == 20 && column == 1)
                        return tempCfgInfo.allowedHelloLoss;
                else if(row == 21 && column == 0)
                        return tempCfgInfo.helloIntervalStr;
                else if(row == 21 && column == 1)
                        return tempCfgInfo.helloInterval;
                else if(row == 22 && column == 0)
                        return tempCfgInfo.localAddTTLStr;
                else if(row == 22 && column == 1)
                        return tempCfgInfo.localAddTTL;
                else if(row == 23 && column == 0)
                        return tempCfgInfo.netDiameterStr;
                else if(row == 23 && column == 1)
                        return tempCfgInfo.netDiameter;
                else if(row == 24 && column == 0)
                        return tempCfgInfo.nodeTraversalTimeStr;
                else if(row == 24 && column == 1)
                        return tempCfgInfo.nodeTraversalTime;
		else if(row == 25 && column == 0)
                        return tempCfgInfo.RERRRatelimitStr;
                else if(row == 25 && column == 1)
                        return tempCfgInfo.RERRRatelimit;
		else if(row == 26 && column == 0)
                        return tempCfgInfo.RREQRetriesStr;
                else if(row == 26 && column == 1)
                        return tempCfgInfo.RREQRetries;
                else if(row == 27 && column == 0)
                        return tempCfgInfo.RREQRateLimitStr;
                else if(row == 27 && column == 1)
                        return tempCfgInfo.RREQRateLimit;
                else if(row == 28 && column == 0)
                        return tempCfgInfo.timeoutBufferStr;
                else if(row == 28 && column == 1)
                        return tempCfgInfo.timeoutBuffer;
                else if(row == 29 && column == 0)
                        return tempCfgInfo.TTLStartStr;
                else if(row == 29 && column == 1)
                        return tempCfgInfo.TTLStart;
                else if(row == 30 && column == 0)
                        return tempCfgInfo.TTLIncrementStr;
                else if(row == 30 && column == 1)
                        return tempCfgInfo.TTLIncrement;
                else if(row == 31 && column == 0)
                        return tempCfgInfo.TTLThresholdStr;
                else if(row == 31 && column == 1)
                        return tempCfgInfo.TTLThreshold;
		else
                	return " ";
	}

	private void setConfigFieldInfo(String val, int row, int column) {
		if(row == 0 && column == 1)
			tempCfgInfo.executionMode = new String(val);
                else if(row == 1 && column == 1)
                        tempCfgInfo.osInUse = new String(val);
                else if(row == 2 && column == 1)
                        tempCfgInfo.ipVersion = new String(val);
                else if(row == 3 && column == 1)
                        tempCfgInfo.ipAddress = new String(val);
                else if(row == 4 && column == 1)
                        tempCfgInfo.ifaceName = new String(val);
                else if(row == 5 && column == 1)
                        tempCfgInfo.ipAddressGateway = new String(val);
                else if(row == 6 && column == 1)
                        tempCfgInfo.loIfaceName = new String(val);
		else if(row == 7 && column == 1)
                        tempCfgInfo.loggingStatus = new String(val);
                else if(row == 8 && column == 1)
                        tempCfgInfo.loggingLevel = new String(val);
		else if(row == 9 && column == 1)
                        tempCfgInfo.logFile = new String(val);
                else if(row == 10 && column == 1)
                        tempCfgInfo.pathToSystemCmds = new String(val);
                else if(row == 11 && column == 1)
                        tempCfgInfo.onlyDestination = new String(val);
                else if(row == 12 && column == 1)
                        tempCfgInfo.gratuitousRREP = new String(val);
		else if(row == 13 && column == 1)
			tempCfgInfo.RREPAckRequired = new String(val);
		else if(row == 14 && column == 1)
			tempCfgInfo.ipAddressMulticast = new String(val);
		else if(row == 15 && column == 1)
			tempCfgInfo.RERRSendingMode = new String(val);
		else if(row == 16 && column == 1)
                        tempCfgInfo.deletePeriodMode = new String(val);
                else if(row == 17 && column == 1)
			tempCfgInfo.routeDiscoveryMode = new String(val);
                else if(row == 18 && column == 1)
			tempCfgInfo.packetBuffering = new String(val);
		else if(row == 19 && column == 1)
                        tempCfgInfo.activeRouteTimeout = new String(val);
                else if(row == 20 && column == 1)
                        tempCfgInfo.allowedHelloLoss = new String(val);
                else if(row == 21 && column == 1)
                        tempCfgInfo.helloInterval = new String(val);
                else if(row == 22 && column == 1)
                        tempCfgInfo.localAddTTL = new String(val);
                else if(row == 23 && column == 1)
                        tempCfgInfo.netDiameter = new String(val);
                else if(row == 24 && column == 1)
                        tempCfgInfo.nodeTraversalTime = new String(val);
		else if(row == 25 && column == 1)
                        tempCfgInfo.RERRRatelimit = new String(val);
                else if(row == 26 && column == 1)
                        tempCfgInfo.RREQRetries = new String(val);
                else if(row == 27 && column == 1)
                        tempCfgInfo.RREQRateLimit = new String(val);
                else if(row == 28 && column == 1)
                        tempCfgInfo.timeoutBuffer = new String(val);
                else if(row == 29 && column == 1)
                        tempCfgInfo.TTLStart = new String(val);
                else if(row == 30 && column == 1)
                        tempCfgInfo.TTLIncrement = new String(val);
                else if(row == 31 && column == 1)
                        tempCfgInfo.TTLThreshold = new String(val);

	}

	private int getRowCount() {
		return 32;
	}

        /**
        * Method to validate and update the changes to the configuration.
        * This method is associated with the Update button
        */
        void updateConfigInfo() {
                try {
			for(i = 0; i < getRowCount(); i++) {
				setConfigFieldInfo(txtConfigFieldData[i].getText(), i, 1);
			}

                        cfgInfo.validateInfo(tempCfgInfo);
                        cfgInfo.setValuesUsing(tempCfgInfo);
                        cfgInfo.updateInfo();
                        hide();

                } catch(Exception e) {
			lblError.setText(e.toString());
			lblError.repaint();
			pnlError.repaint();
                }
        }

        /**
        * Method to leave the configuration user interface without making
        * any changes to the config info. This is associated with the
        * Cancel button.
        */
        void cancelConfigUpdate() {
                hide();
        }
}
