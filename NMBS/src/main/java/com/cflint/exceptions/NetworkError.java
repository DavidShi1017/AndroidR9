package com.cflint.exceptions;
/**
 * Call service appear exception.
 * @author:Alice
 */
public enum NetworkError {
	RefreshConirmationError, BOOKINGTIMEOUT, 
	CONNECTION, INVALIDJSON, TIMEOUT, OTHER, REQUESTFAIL, 
	URLWRONG, NOTICKET, IO, DBOOKINGERROR, REFRESHPAYMENTFAIL, 
	wrongCombination, donotContainTicke, journeyPast, CustomError, DBookingNoSeatAvailableError
}
