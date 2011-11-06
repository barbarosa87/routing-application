/*

JAdhoc ver 0.1 - Java AODV (RFC 3561) Protocol Handler
Copyright 2003 ComNets, University of Bremen

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

package jadhoc;

import jadhoc.gui.GUIInterface;
import jadhoc.gui.GUILinux;
import jadhoc.gui.GUIWindows;
import jadhoc.gui.GUIZaurus;
import jadhoc.net.RouteManager;
import jadhoc.gui.*;
import jadhoc.conf.*;
import jadhoc.other.*;

/**
* This is the starting class of the application. The general task of this
* object is to,
*   - get the configuration info
*   - determine the operation mode (GUI or non-GUI),
*   - create GUI objects if op mode is GUI
*   - create the RouteManager
*   - pass control to the RoutManager
*   - act as the intermediary between the GUI and the RouteManager
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
* @modification history
*  11-feb-2004  select gui based on OS
*/
public class JAdhoc {

	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;
	public Logging log;
	public GUIInterface gui;
	public RouteManager rtMgr;

	/**
	* Constructor to start the GUI and the RouteManager
	* @param String args[] - The command line argument list
	*/
	public JAdhoc(String args[]) {

		printBanner();

		try {

			// check & use cfg-file, if given in command line
			if(args.length >= 1)
				cfgInfo = new ConfigInfo(args[0]);
			else
				cfgInfo = new ConfigInfo("./jadhoc.cfg");

			// create the current info object
			curInfo = new CurrentInfo();

			// setup the logging object
			log = new Logging(cfgInfo, curInfo);
			curInfo.log = log;

			// create & initialize the route manager
			rtMgr = new RouteManager(cfgInfo, curInfo, this);


			// gui mode is active only if set in cfg-file
			if(cfgInfo.executionModeVal.trim().toLowerCase().equals("gui")) {

				// create the gui based on the OS
				if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.LINUX_OS)) {
					gui = new GUILinux(cfgInfo, curInfo, this);
				} else if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.WINDOWS_OS)) {
					gui = new GUIWindows(cfgInfo, curInfo, this);
				} else if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.ZAURUS_OS)) {
					gui = (GUIInterface) new GUIZaurus(cfgInfo, curInfo, this);
				} else {
					gui = null;
					printNonGUIModeBanner();
				}
			} else {
				gui = null;
				printNonGUIModeBanner();
			}

			// if no gui mode, start the application
			if(gui == null)
				rtMgr.startApplication();

		} catch(Exception e) {
			System.out.println("");
			System.out.println("ERROR : " + e);
			printUsage();
			System.exit(1);
		}
	}

	/**
	* Prints the banner of application to the character output
	* screen.
	*/
	private void printBanner() {
		System.out.println("");
		System.out.println("J-Adhoc - AODV Protocol Handler (ver 0.2)");
		System.out.println("Designed & developed at ComNets, ikom, ");
		System.out.println("University of Bremen, Germany - 2004");
		System.out.println("Implements IETF RFC, 3561");
		System.out.println("");
	}

	/**
	* Prints the usage information of the application.
	*/
	private void printUsage() {
		System.out.println("Usage : java -j jadhoc.jar [cfg-file]");
		System.out.println("        where cfg-file is the name & path");
		System.out.println("        of the configuration file, if not");
		System.out.println("        given, defaulted to ./jadhoc.cfg");
		System.out.println("");
	}


	/**
	* Prints the information when run in non GUI mode
	*/
	private void printNonGUIModeBanner() {
		System.out.println("");
		System.out.println("Press Ctrl+C to stop the application");
	}

	/**
	* Method to stop the protocol handler. This is done
	* by requesting route manager to stop activities and
	* then performs an exit with success return code. This
	* method is called by the GUI
	*/
	public void exitApplication() {
		rtMgr.stopApplication();
		System.exit(0);
	}

	/**
	* Method to start the protocol handler. This is done by
	* calling route manager. This method is request by the
	* GUI.
	*/
	public boolean startApplication() {
		return rtMgr.startApplication();
	}

	/**
	* Method to stop the protocol handler. Requested by
	* the GUI. Calls the route manager to stop the
	* protocol handler.
	*/
	public void stopApplication() {
		rtMgr.stopApplication();
	}

	/**
	* Method to request the route manager to return the
	* route entry count, to be sent to the GUI.
	* @return int - the route count
	*/
	public int getRouteCount() {
		return rtMgr.getRouteCount();
	}

	/**
	* Method to return the field count in the routing
	* table, after getting it from the route manager.
	* This is required by the GUI
	* @return int - field count
	*/
	public int getFieldCount() {
                return rtMgr.getFieldCount();
	}

	/**
	* Method to get the specific information value
	* at a particular slot in the routing table.
	* Request is passed to the routing table. This is
	* required by the GUI.
	* @param int row - the routing table row
	* @param int column - the routing table column
	* @return String - value at the given slot
	*/
	public String getRouteValueAt(int row, int column) {
		return rtMgr.getRouteValueAt(row, column);
	}

	/**
	* Method to obtain the requested field name from
	* the routing table. Requested by the GUI. Info
	* provided by the route manager.
	* @param int column - field number
	* @return String - field name
	*/
	public String getFieldName(int column) {
		return rtMgr.getFieldName(column);
	}

	/**
	* Method to stop the GUI from the route manager.
	*/
	public void stopAppFromRouteManager() {
		if(gui == null)
			return;
		gui.stopAppFromRouteManager();
	}

	/**
	* Method to update the GUI when information
	* changes. Requested by the route manager.
	*/
	public void updateDisplay() {
		if(gui == null)
			return;
		gui.redrawTable();
	}

	public void displayError(String msg) {
		if(gui == null)
			return;

		gui.displayError(msg);
	}

	/**
	* Application starting function
	* @param String args[] - contains the command line input
	*			  values
	*/
	public static void main(String args[]) {
		new JAdhoc(args);
	}
}
