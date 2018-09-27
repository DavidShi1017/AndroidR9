package com.cfl.services;

import com.cfl.model.MobileMessage;
import com.cfl.model.MobileMessageResponse;

import java.util.List;


public interface IMessageService {
	public void getMessageData(String language, boolean isChangeLanguage)throws Exception;
	public MobileMessageResponse readMobileMessages();
	public MobileMessageResponse getMessageResponse();
	public void saveMessageStateInWichTab(String whichTab);
	public void deleteMessageState();
	public boolean isShowed(String whichTab);
	public boolean isShowOverLay(String whichTab,MobileMessageResponse mobileMessageResponse);
	public MobileMessageResponse getMobileMessagesByAutoOverlayDisplayLocation(String whichTab);
	public boolean isShowCheckUpdate();
	public void setShowCheckUpdate(boolean isShowCheckUpdate);
	public void saveShouldShowMessage(boolean isNull);
	public boolean shouldShowMessage();

	public void saveMessageNextDisplay(List<MobileMessage> messages);

	public List<MobileMessage> getShowMessage(List<MobileMessage> messages);
}
