package com.nmbs.listeners;


/**
 * Listener for executing post tasks after service execution, can be used by
 * the activities for displaying results or forwarding to other activities.
 * 
 * @author vandousselaerel
 * @version 30/07/2013
 */
public interface ActivityPostExecuteListener {

	/**
	 * After execution, logic to be added here.
	 * 
	 * @param succeeded
	 *            If the execution was successful.
	 */
	public void onPostExecute(boolean succeeded, String message);
	
}