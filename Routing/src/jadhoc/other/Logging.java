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

package jadhoc.other;

import java.io.*;
import java.util.*;
import java.text.*;

import jadhoc.conf.*;

/**
* This class provide functionality related to logging different activities
* that occur in the protocol handler. There are 3 types of logging,
*	critical logging - error notifications
*	activity logging - error notifications and summarized
*				activity logging
*	info logging     - error notifications, summarized activity logging
*				and detailed activity logging
*
* Based on the logging level defined in the parameters by the user and the
* predefined logging level associated with any logging call, the entry
* would be written to or not, to the given log file.
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class Logging {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	// local variables
	private BufferedWriter logFilePtr;
	private SimpleDateFormat timeFormatter;
	private String timeStr;

	// logging level definitions
	public static final int CRITICAL_LOGGING = 1;
	public static final int ACTIVITY_LOGGING = 2;
	public static final int INFO_LOGGING     = 3;
	public static final int MIN_LOGGING      = 1;
	public static final int MAX_LOGGING      = 3;

	/**
	* Constructor creates a loggin object initializing the
	* internal variables.
	* @param ConfigInfo cfg - config info object
	* @param CurrentInfo cur - current info object
	*/
	public Logging(ConfigInfo cfg, CurrentInfo cur) {
		cfgInfo = cfg;
		curInfo = cur;
		logFilePtr = null;
	}

	/**
	* Method to start the logger
	* @exception Exception - thrown if error in integer & IO functions
	*/
	public void start() throws Exception {
		// check log status
		if(!cfgInfo.loggingStatusVal)
			return;

		// open logging file (text file)
                logFilePtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cfgInfo.logFileVal, true)));
		timeFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
	}

	/**
	* Method to start the logger
	* @exception Exception - thrown if error in IO functions
	*/
	public void stop() throws Exception {
		if(!cfgInfo.loggingStatusVal)
			return;

		logFilePtr.close();

		logFilePtr = null;
	}

	/**
	* Method to write to the log file. Whether to write or not is
	* determined by the log level defined by the user and the log level
	* assigned in the program (ll).
	* @param int ll - predetermined log level
	* @param String line - string to log to log file
	*/
	public void write(int ll, String line) {

		// every thread calls this to log to the
		// log file, therefore made thread safe
		synchronized(this) {
			// check if logging activated
			if(!cfgInfo.loggingStatusVal)
				return;

			// check if log level defined in parameters
			// allow this given log level to be considered
			if(ll > cfgInfo.loggingLevelVal)
				return;

			// get date
			timeStr = timeFormatter.format(new Date()) + " : " ;

			try {
				// writing to log file
				logFilePtr.write(timeStr, 0, timeStr.length());
				logFilePtr.write(line, 0, line.length());
				logFilePtr.newLine();
				logFilePtr.flush();

			} catch(Exception e) {
			}
		}
	}
}
