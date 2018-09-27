package com.cfl.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeliveryMethodResponse extends RestResponse {

	private static final long serialVersionUID = 1L;
	@SerializedName("DeliveryMethods")
	private List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
	public List<DeliveryMethod> getDeliveryMethods() {
		return deliveryMethods;
	}
	public DeliveryMethodResponse(List<DeliveryMethod> deliveryMethods){
		this.deliveryMethods = deliveryMethods;
	}
}
